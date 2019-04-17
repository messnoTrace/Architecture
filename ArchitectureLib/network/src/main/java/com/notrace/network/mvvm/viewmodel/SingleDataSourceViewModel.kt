package com.notrace.network.mvvm.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.notrace.network.API_CODE_NET_ERROR
import com.notrace.network.exception.ApiException
import com.notrace.network.mvvm.NetworkState
import com.notrace.network.mvvm.Status
import com.notrace.network.util.convert
import com.notrace.network.util.netError
import com.notrace.network.util.no
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers

/**
 *create by chenyang on 2019/4/9
 *
 * 单个数据源viewmodel 没写完
 **/

open abstract class SingleDataSourceViewModel<T>(autoFetch:Boolean = true) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    /**
     * Keep Completable reference for the retry event
     */
    private var retryCompletable: Completable? = null

    private val initialLoad = MutableLiveData<NetworkState>()

    val errorMessage = MutableLiveData<String>()

    val data = MutableLiveData<T>()

    val retry = {
        retry()
    }

    init {
        if(autoFetch)
            loadInitial()
    }


    protected fun loadInitial() {
        // update network states.
        // we also provide an initial load state to the listeners so that the UI can know when the
        // very first list is loaded.
        initialLoad.postValue(NetworkState.LOADING)

        //get the initial users from the api
        compositeDisposable.add(
            dataProvider().invoke()
                .subscribeOn(Schedulers.io())
                .subscribe({ datas ->
                    // clear retry since last request succeeded
                    setRetry(null)
                    initialLoad.postValue(NetworkState.LOADED)
                    data.postValue(datas)
                }, { throwable ->
                    // keep a Completable for future retry
                    setRetry(Action { loadInitial() })
                    val error = NetworkState.error(throwable)
                    // publish the error
                    initialLoad.postValue(error)
                    throwable.convert().apply {
                        interceptorException(this).no {
                            if (serverCode != API_CODE_NET_ERROR) {
                                data.postValue(data.value)
                            }
                            errorMessage.postValue(message)
                        }
                    }
                }))
    }

    fun retry() {
        if (retryCompletable != null) {
            compositeDisposable.add(retryCompletable!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ }, { throwable -> Log.e("SingleDataSourceViewModel", throwable.message) }))
        }
    }

    fun networkState(): LiveData<NetworkState> {
        return initialLoad
    }

    fun initNetError(): LiveData<Boolean> = Transformations.map(initialLoad) { it.status == Status.FAILED && it.throwable != null && it.throwable.netError() }

    fun loading(): LiveData<Boolean> = Transformations.map(initialLoad) { it.status == Status.RUNNING }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }


    abstract fun dataProvider(): () -> Single<T>

    /**
     * 自己处理的异常数据，返回true为自己处理，终止之后的异常处理，否则之后的处理继续
     */
    open fun interceptorException(exception: ApiException): Boolean {
        return false
    }

    private fun setRetry(action: Action?) {
        if (action == null) {
            this.retryCompletable = null
        } else {
            this.retryCompletable = Completable.fromAction(action)
        }
    }
}

