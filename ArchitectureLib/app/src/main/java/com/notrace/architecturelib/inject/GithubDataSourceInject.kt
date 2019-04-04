package com.notrace.architecturelib.inject

import com.notrace.architecturelib.data.GithubDataSource
import com.notrace.architecturelib.data.remote.GithubService
import com.notrace.network.ServiceFactory

/**
 *create by chenyang on 2019/4/4
 **/
object GithubDataSourceInject {
    val gitHubDataSource by lazy {
        GithubDataSource(ServiceFactory.convertService(GithubService::class.java))
    }
}