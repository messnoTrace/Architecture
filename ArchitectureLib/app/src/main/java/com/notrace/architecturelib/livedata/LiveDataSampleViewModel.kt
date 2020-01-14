package com.notrace.architecturelib.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.notrace.architecturelib.data.model.RepoSearchResponse
import com.notrace.architecturelib.inject.GithubDataSourceInject
import com.notrace.network.mvvm.base.ApiResponse
import com.notrace.network.mvvm.base.OnlyNetworkResource
import com.notrace.network.mvvm.base.Resource
import com.notrace.network.util.AbsentLiveData

class LiveDataSampleViewModel : ViewModel() {
    val requestData = MutableLiveData<Boolean>()
    var page = 0

    var items= mutableListOf<Any>()
    var search = "google"
    val repoSource = Transformations.switchMap(requestData) {
        if (it) {
            getReppo(
                search,
                page
            )
        } else {
            AbsentLiveData.create()
        }
    }

    var finishLoadMore = MutableLiveData<Boolean>()
    var noMoreData: LiveData<Boolean> = Transformations.map(repoSource) {
        if (it?.data != null) {
            it.data!!.items.isEmpty()
        } else {
            true
        }

        true
    }
    val loadMore = {
        page += 1
        requestData.postValue(true)

    }
    var refresh = {
        requestData.postValue(true)
    }
    var retry = {
        requestData.postValue(true)
    }


    fun getReppo(search: String, page: Int): LiveData<Resource<RepoSearchResponse>> {
        return object : OnlyNetworkResource<RepoSearchResponse>() {
            override fun createCall(): LiveData<ApiResponse<RepoSearchResponse>> {
                return GithubDataSourceInject.gitHubDataSource.serchRepos(
                    search,
                    page
                )
            }
        }.asLiveData()
    }
}