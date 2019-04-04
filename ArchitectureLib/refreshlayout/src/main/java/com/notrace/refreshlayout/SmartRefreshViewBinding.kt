package com.notrace.refreshlayout

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import com.notrace.multytype.ItemBindingHolder
import com.notrace.multytype.MultiTypeAdapter
import com.notrace.network.mvvm.NetworkState
import com.notrace.network.mvvm.Status
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import java.util.ArrayList

/**
 *create by chenyang on 2019/4/4
 * 对SmartrefreshLayout的一些操作绑定
 **/

/**
 * 刷新
 */
@BindingAdapter(value = ["refreshListener"],requireAll = true)
fun SmartRefreshLayout.refreshListener(invoke: Function0<Any>) {
    setOnRefreshListener {
        invoke.invoke()
    }

}

/**
 * 刷新完成
 */
@BindingAdapter(value = ["refreshFinish"],requireAll = true)
fun SmartRefreshLayout.refreshFinish(finishRefresh: Boolean) {
    if (finishRefresh) {
        finishRefresh(true)
    }
}

/**
 * 自动刷新
 */
@BindingAdapter(value = ["aotuRefresh"],requireAll = true)
fun SmartRefreshLayout.aotuRefresh(refresh: Boolean) {
    if (refresh) {
        autoRefresh()
    }
}

/**
 * 加载更多
 */
@BindingAdapter(value = ["loadMoreListener"],requireAll = true)
fun SmartRefreshLayout.setLoadMoreListener(invoke: Function0<Any>) {
    setOnLoadMoreListener {
        invoke.invoke()
    }
}


@BindingAdapter(value = ["noMoreData"], requireAll = true)
fun SmartRefreshLayout.noMoreData(noMoreData: Boolean) {
    finishLoadMore(noMoreData)
}

@BindingAdapter(value = ["netState"], requireAll = true)
fun SmartRefreshLayout.netState(state: NetworkState?) {
    state?.run {
        when (status) {
            Status.FAILED -> finishLoadMore(false)
            Status.RUNNING -> {
            }
            Status.SUCCESS -> finishLoadMore(true)
        }
    }
}

@BindingAdapter(value = ["bindingHolders", "items"],requireAll = true)
fun <T> RecyclerView.setItems(bindingHolder: ItemBindingHolder?, data: List<T>?) {
    if (bindingHolder == null) {
        throw IllegalArgumentException("itemBinding must not be null")
    }
    val items = data ?: ArrayList()
    val adapter = if (getAdapter() == null) {
        MultiTypeAdapter()
    } else {
        getAdapter() as MultiTypeAdapter
    }

    adapter.registerAll(bindingHolder.typePool)

    if (getAdapter() !== adapter) {
        this.adapter = adapter
    }

    adapter.items = items
    adapter.notifyDataSetChanged()
}
