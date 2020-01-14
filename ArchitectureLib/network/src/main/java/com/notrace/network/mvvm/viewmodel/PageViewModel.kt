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
 *create by chenyang on 2019/7/19
 * 带上拉下拉的viewmodel
 **/
abstract class PageViewModel<T> : ViewModel() {


    private var firstTime = true
    private val compositeDisposable = CompositeDisposable()

    private val sourceFactory: ListDataSourceFactory<T>

    val refreshEvent = MutableLiveData<Boolean>()

    lateinit var designList: LiveData<PagedList<T>>


    val data = MutableLiveData<List<Any>>()
    val holder = ItemBindingHolder()


    init {
        sourceFactory = ListDataSourceFactory(privideSourceData(), compositeDisposable)
        registerHolder(holder)
    }


    fun loadData() {
        val config = PagedList.Config.Builder()
            .setPageSize(getPageSize())
            .setInitialLoadSizeHint(getInitLoadSize())
            .setEnablePlaceholders(false)
            .build()


        designList = LivePagedListBuilder<Int, T>(sourceFactory, config).build()

        designList.observeForever {
            sourceFactory.listDataSource.value!!.newDataArrived.observeForever {

                designList.value.let {
                    when (it) {
                        null -> listOf()
                        else -> convertData(it)
                    }
                }.let {
                    data.postValue(it)
                }

            }
        }

    }

    val retry = {
        data.apply {
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

            val index = if (data.value != null) {
                data.value!!.size
            } else {
                0
            }
            loadAround(index)
        }
    }

    fun refresh() {
        sourceFactory.listDataSource.value?.invalidate()
    }

    fun retry() {
        sourceFactory.listDataSource.value?.retry()
    }

    fun loadEnd(): LiveData<Boolean> =
        Transformations.switchMap<ListDataSource<T>, Boolean>(sourceFactory.listDataSource)
        {
            Transformations.map(it.networkState) {
                it.status == Status.FAILED && it.throwable != null && it.throwable.noData()
            }
        }


    fun networkState(): LiveData<NetworkState> =
        Transformations.switchMap<ListDataSource<T>, NetworkState>(
            sourceFactory.listDataSource
        ) {
            it.networkState
        }


    fun initNetError(): LiveData<Boolean> {
        return Transformations.switchMap<ListDataSource<T>, Boolean>(
            sourceFactory.listDataSource
        ) {
            Transformations.map(it.initialLoad) { it ->
                it.status == Status.FAILED && it.throwable != null && it.throwable.netError()
            }
        }
    }

    fun loading(): LiveData<Boolean> {
        return Transformations.switchMap<ListDataSource<T>, Boolean>(
            sourceFactory.listDataSource
        ) { Transformations.map(it.initialLoad) { it.status == Status.RUNNING && firstTime } }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    //提供数据源
    abstract fun privideSourceData(): (page: Int) -> Single<List<T>>

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