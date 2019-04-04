package com.notrace.network.mvvm

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

/**
 *create by chenyang on 2019/4/3
 **/
class ListDataSourceFactory<T>(
    val remoteData: (page: Int) -> Single<List<T>>,
    val compositeDisposable: CompositeDisposable
) : DataSource.Factory<Int, T>() {
    val listDataSource = MutableLiveData<ListDataSource<T>>()
    override fun create(): DataSource<Int, T> {
        val myDesignDataSource = ListDataSource(remoteData, compositeDisposable)
        listDataSource.postValue(myDesignDataSource)
        return myDesignDataSource
    }
}