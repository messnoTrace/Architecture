package com.notrace.network.adapter

import androidx.lifecycle.LiveData
import com.notrace.network.mvvm.base.ApiResponse
import com.notrace.network.mvvm.base.ERROR
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

//如果这个地方 CallAdapter<R, LiveData<ApiResponse<R>>>改成了  CallAdapter<R, LiveData<R>>OnlyNetworkResource
//解析状态需要放在OnlyNetworkResource中处理
class LiveDataCallAdapter<R>(private val responseType: Type) : CallAdapter<R, LiveData<R>> {
    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): LiveData<R> {

        return object : LiveData<R>() {
            private var stated = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (stated.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<R> {
                        override fun onResponse(call: Call<R>, response: Response<R>) {
                            if (response.isSuccessful) {
                                postValue(response.body())
                            } else {
                                postValue(
                                    ApiResponse.error(response.code().toString(),
                                        Exception(response.message())
                                    ) as R
                                )
                            }

                        }

                        override fun onFailure(call: Call<R>, t: Throwable) {
                            try {
                                val data = ApiResponse.error(ERROR, t) as R
                                postValue(data)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                postValue(null)
                            }
                        }

                    })
                }
            }
        }
    }
}