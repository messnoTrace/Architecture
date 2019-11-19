package com.notrace.network.adapter

import android.arch.lifecycle.LiveData
import com.notrace.network.gson.ServerResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataCallAdapter<T>(private val responseType: Type) : CallAdapter<T, LiveData<T>>{
    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<T>): LiveData<T> {

        return object : LiveData<T>() {
            private val stated = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (stated.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<T> {

                        override fun onFailure(call: Call<T>, t: Throwable) {

                            //TODO
                            val value =
                                ServerResponse<T>(-1, "", null, System.currentTimeMillis()) as T
                            postValue(value)
                        }

                        override fun onResponse(call: Call<T>, response: Response<T>) {
                            postValue(response.body())

                        }
                    })
                }
            }
        }


    }
}