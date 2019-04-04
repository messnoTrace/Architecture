package com.notrace.architecturelib.data

import com.notrace.architecturelib.data.model.RepoSearchResponse
import com.notrace.architecturelib.data.remote.GithubService
import io.reactivex.Single

/**
 *create by chenyang on 2019/4/4
 **/
class GithubDataSource(val githubApi: GithubService) {
    fun searchRepos(query: String): Single<RepoSearchResponse> {
        return githubApi.searchRepos(query)
    }

    fun searchRepos(query: String, page: Int): Single<RepoSearchResponse> {
        return githubApi.searchRepos(query, page)
    }
}