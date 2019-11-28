package com.notrace.network.adapter

import android.arch.lifecycle.LiveData
import com.notrace.network.mvvm.base.ApiErrorResponse
import com.notrace.network.mvvm.base.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataCallAdapter<R>(private val responseType: Type) : CallAdapter<R, LiveData<ApiResponse<R>>> {
    override fun responseType(): Type {
        return responseType
    }

    // 返回LiveData<ApiResponse<**>>
    override fun adapt(call: Call<R>): LiveData<ApiResponse<R>> {

        return object : LiveData<ApiResponse<R>>() {
            private var stated = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (stated.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<R> {
                        override fun onResponse(call: Call<R>, response: Response<R>) {
                            postValue(ApiResponse.create(response))

                        }
                        override fun onFailure(call: Call<R>, t: Throwable) {
                            postValue(ApiErrorResponse("error", t.message!!))
                        }
                    })
                }
            }
        }
    }
}