package com.notrace.network.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import android.util.Log
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.functions.Action

/**
 *create by chenyang on 2019/4/3
 **/
class ListDataSource<T>(
    val remoteData: (page: Int) -> Single<List<T>>,
    val compositeDisposable: CompositeDisposable
) : PageKeyedDataSource<Int, T>() {

    val networkState = MutableLiveData<NetworkState>()
    val initialLoad = MutableLiveData<NetworkState>()
    val newDataArrived = SingleLiveData<Void>()


    /**
     * keep completable reference for the retry event
     */
    private var retryCompletable: Completable? = null

    fun retry() {
        retryCompletable?.let {
            compositeDisposable.add(
                retryCompletable!!
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({}, { Log.e("ListDataSource", it.message) })
            )

        }

    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, T>) {

        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        compositeDisposable.add(
            remoteData.invoke(1)
                .subscribe({
                    setRetry(null)
                    networkState.postValue(NetworkState.LOADED)
                    initialLoad.postValue(NetworkState.LOADED)
                    callback.onResult(it, null, 2)
                    newDataArrived.postCall()
                }, {
                    newDataArrived.postCall()
                    setRetry(Action { loadInitial(params, callback) })
                    val error = NetworkState.error(it)
                    networkState.postValue(error)
                    initialLoad.postValue(error)
                })
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        // set network value to loading.
        networkState.postValue(NetworkState.LOADING)

        //get the users from the api after id
        compositeDisposable.add(remoteData.invoke(params.key).subscribe({ items ->
            // clear retry since last request succeeded
            setRetry(null)
            networkState.postValue(NetworkState.LOADED)
            callback.onResult(items, params.key + 1)
            newDataArrived.postCall()
        }, { throwable ->
            // keep a Completable for future retry
            setRetry(Action { loadAfter(params, callback) })
            // publish the error
            networkState.postValue(NetworkState.error(throwable))
        }))
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        networkState.postValue(NetworkState.LOADING)

        compositeDisposable.add(
            remoteData.invoke(params.key)
                .subscribe({
                    setRetry(null)
                    networkState.postValue(NetworkState.LOADED)
                    callback.onResult(it, params.key - 1)
                    newDataArrived.postCall()
                }, {
                    // keep a Completable for future retry
                    setRetry(Action { loadAfter(params, callback) })
                    // publish the error
                    networkState.postValue(NetworkState.error(it))
                })
        )

    }

    fun setRetry(action: Action?) {
        if (null == action) {
            this.retryCompletable = null
        } else {
            this.retryCompletable = Completable.fromAction(action)
        }
    }
}