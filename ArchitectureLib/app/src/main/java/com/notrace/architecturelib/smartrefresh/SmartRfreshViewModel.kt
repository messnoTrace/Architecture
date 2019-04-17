package com.notrace.architecturelib.smartrefresh

import android.arch.lifecycle.MutableLiveData
import com.notrace.architecturelib.R
import com.notrace.architecturelib.data.model.Repo
import com.notrace.architecturelib.inject.GithubDataSourceInject
import com.notrace.multytype.BR
import com.notrace.multytype.ItemBindingHolder
import com.notrace.multytype.ItemViewBinder
import com.notrace.network.mvvm.viewmodel.MultableDataSourceViewModel
import com.notrace.network.rx.CommonSingleObsever
import io.reactivex.Single

/**
 *create by chenyang on 2019/4/4
 **/
class SmartRfreshViewModel : MultableDataSourceViewModel<Repo>() {

    init {

        searchRepo()
    }

    override fun onCleared() {

    }

    override fun convert(list: List<Repo>): List<Any> {

        var mutableList = mutableListOf<Any>()
        mutableList.addAll(list)
        return mutableList
    }

    override fun registerHolder(holder: ItemBindingHolder) {

        holder.register(Repo::class.java, ItemViewBinder(BR.item, R.layout.item_repo))
    }

    override fun provideData(): (page: Int) -> Single<List<Repo>> {
        return { page ->
            searchRepoByPage("google", page)
        }
    }

    var toastMessage = MutableLiveData<String>()

    fun searchRepoByPage(query: String, page: Int): Single<List<Repo>> {
        return GithubDataSourceInject.gitHubDataSource.searchRepos(query, page).flatMap {
            return@flatMap Single.just(it.items)
        }
    }

    fun searchRepo() {

        GithubDataSourceInject.gitHubDataSource.searchRepos("google")
            .map {
                val items = mutableListOf<Any>()
                items.addAll(it.items)
                items
            }
            .subscribe(object : CommonSingleObsever<MutableList<Any>>() {
                override fun onError(errorCode: Int, message: String) {
                    loadData()
                    toastMessage.postValue(message)
                }

                override fun onSuccess(t: MutableList<Any>) {
                    beforeItems.value = t
                    loadData()
                }
            })

    }
}