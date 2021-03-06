package com.notrace.network.mvvm

import com.notrace.network.mvvm.base.Status

/**
 *create by chenyang on 2019/4/2
 **/




@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
    val status: Status,
    val throwable: Throwable? = null
) {
    companion object {
        val LOADED = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.RUNNING)
        fun error(throwable: Throwable?) =
            NetworkState(Status.FAILED, throwable)
    }
}