package com.notrace.network.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
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