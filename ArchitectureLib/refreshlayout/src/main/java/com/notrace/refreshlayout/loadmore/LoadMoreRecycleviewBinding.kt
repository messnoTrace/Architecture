package com.notrace.refreshlayout.loadmore

import android.databinding.BindingAdapter
import com.notrace.multytype.ItemBindingHolder
import com.notrace.multytype.MultiTypeAdapter
import com.notrace.network.mvvm.NetworkState
import com.notrace.network.mvvm.Status
import java.util.ArrayList

/**
 * 对LoadMoreRecyclerView的一些操作做绑定
 * *create by chenyang on 2019/4/4
 */
@BindingAdapter(value = ["loadMoreListener"], requireAll = false)
fun LoadMoreRecyclerView.onLoadMore(loadMore: Function0<Any>) {
    setLoadMoreListener({ loadMore.invoke() })
}

@BindingAdapter(value = ["noMoreData"], requireAll = false)
fun LoadMoreRecyclerView.noMoreData(noMoreData: Boolean) {
    if (noMoreData) {
        noMoreData()
    } else {
        resetState()
    }
}

@BindingAdapter(value = ["netState"], requireAll = false)
fun LoadMoreRecyclerView.netState(state: NetworkState?) {
    state?.run {
        when (status) {
            Status.FAILED -> loadMoreFail()
            Status.RUNNING -> {
            }
            Status.SUCCESS -> loadMoreSucces()
        }
    }
}

@BindingAdapter(value = ["bindingHolders", "items"])
fun <T> LoadMoreRecyclerView.setItems(bindingHolder: ItemBindingHolder?, data: List<T>?) {
    if (bindingHolder == null) {
        throw IllegalArgumentException("itemBinding must not be null")
    }
    val items = data ?: ArrayList()
    val adapter = if (realAdapter == null) {
        MultiTypeAdapter()
    } else {
        realAdapter as MultiTypeAdapter
    }

    adapter.registerAll(bindingHolder.typePool)

    if (realAdapter !== adapter) {
        this.adapter = adapter
    }

    adapter.items = items
    adapter.notifyDataSetChanged()
}
