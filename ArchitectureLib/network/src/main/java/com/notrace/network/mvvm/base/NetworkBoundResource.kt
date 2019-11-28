package com.notrace.network.mvvm.base

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.notrace.network.util.AbsentLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

abstract class NetworkBoundResource<ResultType, RequestType>
@MainThread constructor() {
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {

        result.value = Resource.loading(null)

        @Suppress("LeakingThis")
        val localSource = loadFromLocal()
        result.addSource(localSource) { data ->
            result.removeSource(localSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(localSource)
            } else {
                result.addSource(localSource) { newData ->
                    setValue(Resource.success(newData))
                }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }


    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { newData ->
            setValue(Resource.loading(newData))
        }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when (response) {
                is ApiSuccessResponse -> {

                    Schedulers.io().scheduleDirect {
                        saveCallResult(processResponse(response))
                        AndroidSchedulers.mainThread().scheduleDirect {
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromLocal()) { newData ->
                                setValue(Resource.success(newData))
                            }
                        }
                    }
                }
                is ApiEmptyResponse -> {
                    AndroidSchedulers.mainThread().scheduleDirect {
                        // reload from disk whatever we had
                        result.addSource(loadFromLocal()) { newData ->
                            setValue(Resource.success(newData))
                        }
                    }
                }
                is ApiErrorResponse -> {
                    onFetchFailed()
                    result.addSource(dbSource) { newData ->
                        setValue(Resource.error(response.message, newData))
                    }
                }
            }
        }
    }


    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>?) = response?.data

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType?)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected open fun loadFromLocal(): LiveData<ResultType> {
        return AbsentLiveData.create()
    }

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
}


