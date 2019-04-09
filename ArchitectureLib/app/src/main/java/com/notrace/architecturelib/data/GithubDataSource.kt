package com.notrace.architecturelib.data

import com.notrace.architecturelib.data.model.RepoSearchResponse
import com.notrace.architecturelib.data.remote.GithubService
import com.notrace.network.rx.RxTransform
import io.reactivex.Single

/**
 *create by chenyang on 2019/4/4
 **/
class GithubDataSource(val githubApi: GithubService) {
    fun searchRepos(query: String): Single<RepoSearchResponse> {
        return githubApi.searchRepos(query).compose(RxTransform.singleIOMain())
    }

    fun searchRepos(query: String, page: Int): Single<RepoSearchResponse> {
        return githubApi.searchRepos(query, page).compose(RxTransform.singleIOMain())
    }
}