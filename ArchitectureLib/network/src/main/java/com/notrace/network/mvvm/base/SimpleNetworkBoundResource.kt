package com.notrace.network.mvvm.base

import androidx.lifecycle.LiveData
import com.notrace.network.util.AbsentLiveData

 abstract class SimpleNetworkBoundResource<ResultType,RequestType>:NetworkBoundResource<ResultType,RequestType>() {


     override fun saveCallResult(item: RequestType) {

     }

    override fun shouldFetch(data: ResultType?): Boolean {
        return true
    }
     override fun loadFromLocal(): LiveData<ResultType> {
         return AbsentLiveData.create()
     }
}