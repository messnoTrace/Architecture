package com.notrace.network.mvvm

/**
 *create by chenyang on 2019/4/2
 **/


enum class Status {
    RUNNING,
    SUCCESS,
    FAILED
}

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