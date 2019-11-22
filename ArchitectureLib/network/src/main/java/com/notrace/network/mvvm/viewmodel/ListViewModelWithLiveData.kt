package com.notrace.network.mvvm.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.notrace.multytype.ItemBindingHolder
import com.notrace.network.mvvm.LiveDataListDataSource
import com.notrace.network.mvvm.LiveDataListDataSourceFactory
import com.notrace.network.mvvm.NetworkState
import com.notrace.network.mvvm.base.ApiResponse
import com.notrace.network.mvvm.base.Status
import com.notrace.network.util.yes

abstract class ListViewModelWithLiveData<T> : ViewModel() {


    var firstTime = true
    val sourceFactory: LiveDataListDataSourceFactory<T>

    val refreshEvent = MutableLiveData<Boolean>()

    lateinit var designList: LiveData<PagedList<T>>


    val holder = ItemBindingHolder()
    var beforeItems = MutableLiveData<MutableList<Any>>()
    var mutableList= mutableListOf<Any>()
    var mutableItems = MutableLiveData<MutableList<Any>>()

    var items = MutableLiveData<List<Any>>()


    val retry = {
        items.apply {
            (value?.size == 0).yes {
                this.value = null
            }
        }
        refreshEvent.postValue(true)
    }


    val refresh = {
        firstTime = false
        refresh()
    }

    val loadMore = {
        designList.value?.apply {
            val index = if (items.value != null) {
                items.value!!.size
            } else {
                0
            }
            loadAround(index)
        }
    }

    fun loadMainData() {
        val config = PagedList.Config.Builder()
            .setPageSize(getPageSize())
            .setInitialLoadSizeHint(getInitLoadSize())
            .setEnablePlaceholders(getEnablePlaceholders())
            .build()
        designList = LivePagedListBuilder<Int, T>(sourceFactory, config).build()

        designList.observeForever {
            sourceFactory.listDataSource.value!!.newDataArrived.observeForever {
                designList.value.let {
                    when (it) {
                        null -> convertData(listOf())
                        else -> convertData(it)
                    }
                }.let {
                    items.postValue(it)
                    mutableList.clear()
                    beforeItems.value?.let {
                        mutableList.addAll(it)
                    }
                    mutableList.addAll(it)
                    mutableItems.postValue(mutableList)

                }
            }
        }
    }

    init {
        sourceFactory = LiveDataListDataSourceFactory(provideSourceData())
        registerHolder(holder)
    }



    fun dataEnd(): LiveData<Boolean> = Transformations.switchMap<LiveDataListDataSource<T>, Boolean>(
        sourceFactory.listDataSource, { Transformations.map(it.networkState, { it.status == Status.FAILED && it.throwable != null }) }
    )

    fun networkState(): LiveData<NetworkState> = Transformations.switchMap<LiveDataListDataSource<T>, NetworkState>(
        sourceFactory.listDataSource, { it.networkState })

    fun initNetError(): LiveData<Boolean> = Transformations.switchMap<LiveDataListDataSource<T>, Boolean>(
        sourceFactory.listDataSource, { Transformations.map(it.initialLoad, { it.status == Status.FAILED && it.throwable != null }) }
    )

    fun loading(): LiveData<Boolean> = Transformations.switchMap<LiveDataListDataSource<T>, Boolean>(
        sourceFactory.listDataSource, { Transformations.map(it.initialLoad, { it.status == Status.RUNNING && firstTime }) }
    )

    fun refresh(){
        sourceFactory.listDataSource.value?.invalidate()
    }

    fun retry(){
        sourceFactory.listDataSource.value?.retryAllFailed()
    }

    //提供数据源
    abstract fun provideSourceData(): (page: Int) -> LiveData<ApiResponse<List<T>>>

    //绑定holder
    abstract fun registerHolder(holder: ItemBindingHolder)

    //数据转换
    abstract fun convertData(source: List<T>): List<Any>


    fun getPageSize(): Int {
        return 10
    }

    fun getInitLoadSize(): Int {
        return 20
    }

    fun getEnablePlaceholders(): Boolean {
        return false
    }
}