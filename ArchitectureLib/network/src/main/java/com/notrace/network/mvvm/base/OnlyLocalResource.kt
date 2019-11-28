package com.notrace.network.mvvm.base

import android.arch.lifecycle.LiveData
import com.notrace.network.util.AbsentLiveData


abstract class OnlyLocalResource<ResultType,RequestType>
//    NetworkBoundResource<ResultType, RequestType>()  {
//    override fun shouldFetch(data: ResultType?): Boolean {
//        return false
//    }
//
//    override fun createCall(): LiveData<ApiResponse<RequestType>> {
//        return AbsentLiveData.create()
//    }
//
//    override fun saveCallResult(item: RequestType) {
//
//    }
//}