package com.notrace.architecturelib.smartrefresh

import androidx.lifecycle.MutableLiveData
import com.notrace.architecturelib.R
import com.notrace.architecturelib.data.model.Repo
import com.notrace.architecturelib.inject.GithubDataSourceInject
import com.notrace.multytype.BR
import com.notrace.multytype.ItemBindingHolder
import com.notrace.multytype.ItemViewBinder
import com.notrace.network.mvvm.viewmodel.MultableDataSourceViewModel
import com.notrace.network.mvvm.viewmodel.PageViewModel
import com.notrace.network.rx.CommonSingleObsever
import io.reactivex.Single

/**
 *create by chenyang on 2019/4/4
 **/
class SmartRfreshViewModel : PageViewModel<Repo>() {
    override fun privideSourceData(): (page: Int) -> Single<List<Repo>> {

        return {
            page->
            searchRepoByPage("google", page)
        }
    }

    override fun convertData(source: List<Repo>): List<Any> {
        return source
    }

    init {

        searchRepo()
    }

    override fun onCleared() {

    }


    override fun registerHolder(holder: ItemBindingHolder) {

        holder.register(Repo::class.java, ItemViewBinder(BR.item, R.layout.item_repo))
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
                    loadData()
                }
            })

    }
}