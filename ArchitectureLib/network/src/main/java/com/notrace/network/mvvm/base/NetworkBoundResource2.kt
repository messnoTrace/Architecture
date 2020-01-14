package com.notrace.network.mvvm.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.notrace.network.util.AbsentLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

// ResultType: Type for the Resource data
// RequestType: Type for the API response
abstract class NetworkBoundResource2<ResultType, RequestType>
@MainThread constructor() {
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {

        result.value = Resource.loading(null)

        if (needReadLocal()) {
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
        } else {
            val api = createCall()
            result.addSource(api) { response ->
                result.removeSource(api)
                postData(response)
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
            postData(response)
        }
    }

    private fun postData(response: ApiResponse<RequestType>?) {
        when (response) {
            is ApiSuccessResponse -> {
                Schedulers.io().scheduleDirect {
                    saveCallResult(processResponse(response))
                    AndroidSchedulers.mainThread().scheduleDirect {
                        setValue(Resource.success(converToResult(response.data)))
                    }
                }
            }
            is ApiEmptyResponse -> {
                AndroidSchedulers.mainThread().scheduleDirect {
                    setValue(Resource.success(null))
                }
            }
            is ApiErrorResponse -> {
                onFetchFailed()
                setValue(Resource.error(response.msg, null))
            }
        }
    }


    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.data!!

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun converToResult(data: RequestType?): ResultType?

    @MainThread
    protected abstract fun needReadLocal(): Boolean

    @MainThread
    protected open fun loadFromLocal(): LiveData<ResultType> {
        return AbsentLiveData.create()
    }

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
}


