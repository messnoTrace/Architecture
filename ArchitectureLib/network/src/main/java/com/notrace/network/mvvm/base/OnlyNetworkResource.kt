package com.notrace.network.mvvm.base

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import io.reactivex.android.schedulers.AndroidSchedulers


abstract class OnlyNetworkResource<ResultType, RequestType>
@MainThread
constructor() {
    val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        val apiResponse = createCall()
        result.addSource(apiResponse) { response ->
            when (response) {
                is ApiSuccessResponse -> {
                    AndroidSchedulers.mainThread().scheduleDirect {
                        postValue(Resource.success(response.data))
                    }
                }
                is ApiEmptyResponse -> {

                    AndroidSchedulers.mainThread().scheduleDirect {
                        postValue(Resource.success(response.data))
                    }
                }
                is ApiEmptyResponse -> {
                    AndroidSchedulers.mainThread().scheduleDirect {
                        postValue(Resource.error(response.message, response.data))
                    }
                }

            }
        }


    }

    @MainThread
    private fun postValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.postValue(newValue)
        }
    }


    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<ResultType>>

    fun asLiveData() = result as LiveData<Resource<ResultType>>
}


