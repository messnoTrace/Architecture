package com.notrace.network.mvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.notrace.multytype.ItemBindingHolder
import com.notrace.network.mvvm.ListDataSource
import com.notrace.network.mvvm.ListDataSourceFactory
import com.notrace.network.mvvm.NetworkState
import com.notrace.network.mvvm.base.Status
import com.notrace.network.util.netError
import com.notrace.network.util.noData
import com.notrace.network.util.yes
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

/**
 *create by chenyang on 2019/4/3
 *
 * 多种数据源viewmodel 没写完
 **/
abstract class MultableDataSourceViewModel<T> : ViewModel() {

    private var firstTime = true

    private val compositeDisposable = CompositeDisposable()
    private val sourceFactory: ListDataSourceFactory<T>

    val refreshEvent = MutableLiveData<Boolean>()
    lateinit var designList: LiveData<PagedList<T>>

    var items = MutableLiveData<List<Any>>()//正常一个数据源用这个
    var beforeItems = MutableLiveData<MutableList<Any>>()//多个数据源
    private var mutableList = mutableListOf<Any>()
    var mutableItems = MutableLiveData<MutableList<Any>>()
    val holder = ItemBindingHolder()

    init {
        sourceFactory = ListDataSourceFactory(provideData(), compositeDisposable)

        registerHolder(holder)
    }


    fun loadData() {
        val config = PagedList.Config.Builder()
            .setPageSize(10)
            .setInitialLoadSizeHint(20)
            .setEnablePlaceholders(false)
            .build()

        designList = LivePagedListBuilder<Int, T>(sourceFactory, config).build()
        designList.observeForever {

            sourceFactory.listDataSource.value!!.newDataArrived.observeForever {
                designList.value.let {
                    when (it) {
                        null -> listOf()
                        else -> convert(it)
                    }
                }.let {
                    items.postValue(it)
                    mutableList.clear()
                    beforeItems.value?.run {
                        mutableList.addAll(this)
                    }
                    items.value?.run {
                        mutableList.addAll(this)
                    }
                    mutableItems.postValue(mutableList)
                }

            }

        }
    }

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


    fun refresh() {
        sourceFactory.listDataSource.value!!.invalidate()
    }

    fun retry() {
        sourceFactory.listDataSource.value!!.retry()
    }

    public fun loadEnd(): LiveData<Boolean> =
        Transformations.switchMap<com.notrace.network.mvvm.ListDataSource<T>, Boolean>(sourceFactory.listDataSource) {
            Transformations.map(
                it.networkState
            ) { it.status == Status.FAILED && it.throwable != null && it.throwable.noData() }
        }

    fun networkState(): LiveData<NetworkState> = Transformations.switchMap<ListDataSource<T>, NetworkState>(
        sourceFactory.listDataSource, {
            it.networkState
        }
    )

    fun initNetError(): LiveData<Boolean> = Transformations.switchMap<ListDataSource<T>, Boolean>(
        sourceFactory.listDataSource,
        {
            Transformations.map(
                it.initialLoad,
                {
                    it.status == Status.FAILED
                            && it.throwable != null
                            && it.throwable.netError()
                })
        }
    )

    fun loading(): LiveData<Boolean> = Transformations.switchMap<ListDataSource<T>, Boolean>(
        sourceFactory.listDataSource,
        { Transformations.map(it.initialLoad, { it.status == Status.RUNNING && firstTime }) }
    )

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    abstract fun provideData(): (page: Int) -> Single<List<T>>
    abstract fun registerHolder(holder: ItemBindingHolder)
    abstract fun convert(list: List<T>): List<Any>
}