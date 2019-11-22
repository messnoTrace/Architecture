package com.notrace.network.mvvm

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.notrace.network.mvvm.base.ApiResponse


class LiveDataListDataSourceFactory<T>(
    val dataSource: (page: Int) -> LiveData<ApiResponse<List<T>>>
) :
    DataSource.Factory<Int, T>() {
    val listDataSource = MutableLiveData<LiveDataListDataSource<T>>()
    override fun create(): DataSource<Int, T> {
        val designDataSource = LiveDataListDataSource(dataSource)
        listDataSource.postValue(designDataSource)
        return designDataSource
    }
}