package com.notrace.network.adapter

import androidx.lifecycle.LiveData
import com.notrace.network.mvvm.base.ApiResponse
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.IllegalArgumentException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class LiveDataCallAdapterFactory : CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (getRawType(returnType) != LiveData::class.java) {
            return null
        }
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawObservableType = getRawType(observableType)


        if (rawObservableType == ApiResponse::class.java) {
            // 返回LiveData<ApiResponse<**>>
            if (observableType !is ParameterizedType) {
                throw IllegalArgumentException("resource must be parameterized")
            }
            return LiveDataCallAdapter<Any>(observableType)
//            return LiveDataCallAdapter<Any>(observableType)
        } else
            throw IllegalArgumentException("type must be a ApiResponse")
    }
}