package com.notrace.network.mvvm

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.notrace.network.mvvm.base.*
import io.reactivex.schedulers.Schedulers

class LiveDataListDataSource<T>(
    val dataSource: (page: Int) -> LiveData<ApiResponse<List<T>>>
) : PageKeyedDataSource<Int, T>() {

    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null


    /**
     * There is no sync on the state because paging will always call loadInitial first then wait
     * for it to return some success value before calling loadAfter.
     */
    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()
    val newDataArrived = SingleLiveData<Void>()


    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            Schedulers.io().scheduleDirect {
                it.invoke()
            }
        }
    }
    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        object :SimpleNetworkBoundResource<List<T>,List<T>>(){
            override fun createCall(): LiveData<ApiResponse<List<T>>> {
                return dataSource.invoke(1)
            }

            override fun processResponse(response: ApiSuccessResponse<List<T>>): List<T> {
                networkState.postValue(NetworkState.LOADED)
                initialLoad.postValue(NetworkState.LOADED)
                callback.onResult(response.body,params.key - 1)
                newDataArrived.postCall()
                return super.processResponse(response)
            }

            override fun onFetchFailed(message:String?) {
                super.onFetchFailed(message)
                newDataArrived.postCall()
                retry = {
                    loadAfter(params,callback)
                }
                networkState.postValue(NetworkState.error(Throwable(message)))

            }

        }
    }


    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, T>
    ) {


    }
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        networkState.postValue(NetworkState.LOADING)

        object :SimpleNetworkBoundResource<List<T>,List<T>>(){
            override fun createCall(): LiveData<ApiResponse<List<T>>> {
                return dataSource.invoke(1)
            }

            override fun processResponse(response: ApiSuccessResponse<List<T>>): List<T> {
                retry = null

                networkState.postValue(NetworkState.LOADED)
                callback.onResult(response.body, params.key + 1)
                newDataArrived.postCall()
                return super.processResponse(response)

            }

            override fun onFetchFailed(message:String?) {
                super.onFetchFailed(message)
                retry = {
                    loadAfter(params, callback)
                }
                networkState.postValue(NetworkState.error(Throwable(message)))
            }

        }

    }


}