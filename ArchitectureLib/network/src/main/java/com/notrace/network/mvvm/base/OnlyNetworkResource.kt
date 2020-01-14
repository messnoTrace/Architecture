package com.notrace.network.mvvm.base

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import io.reactivex.android.schedulers.AndroidSchedulers

abstract class OnlyNetworkResource<ResultType>
@MainThread
constructor() {
    val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        var apiResponse = createCall()

        result.addSource(apiResponse) { response ->
            run {
                //这里需要注意下，LiveDataCallAdapter中设置的泛型不是LiveData<ApiResponse<R>>
                //那么需要在这里自己解析结果

                response.let {
                    when (response.code) {
                        SUCCESS -> {
                            AndroidSchedulers.mainThread().scheduleDirect {
                                postValue(Resource.success(response.data))
                            }
                        }
                        ERROR -> {
                            AndroidSchedulers.mainThread().scheduleDirect {
                                postValue(Resource.error(response.msg, response.data))
                            }
                        }
                        else -> {
                            AndroidSchedulers.mainThread().scheduleDirect {
                                postValue(Resource.error(response.msg, response.data))
                            }
                        }
                    }
                }

                /**
                when (response) {
                is ApiSuccessResponse -> {
                AndroidSchedulers.mainThread().scheduleDirect {
                Timber.d("success")
                postValue(Resource.success(response.data))
                }
                }
                is ApiEmptyResponse -> {

                AndroidSchedulers.mainThread().scheduleDirect {
                Timber.d("empty")
                postValue(Resource.success(response.data))
                }
                }
                is ApiErrorResponse -> {
                AndroidSchedulers.mainThread().scheduleDirect {
                Timber.d("error")
                postValue(Resource.error(response.msg, response.data))
                }
                }
                else -> {
                AndroidSchedulers.mainThread().scheduleDirect {
                Timber.d("else error")
                postValue(Resource.error(response.msg, response.data))
                }
                }

                }
                 **/
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

//    @MainThread
//    protected abstract fun fetchFaild()

    fun asLiveData() = result as LiveData<Resource<ResultType>>
}


