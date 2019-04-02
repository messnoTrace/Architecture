/*
 * Copyright (C) 2016 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.notrace.network.adapter

import android.os.Handler
import android.os.Looper
import cn.com.open.shuxiaotong.netlib.exception.NetErrorException
import com.notrace.network.API_CODE_SUCCESS
import com.notrace.network.API_CODE_TIMER
import com.notrace.network.ErrorBehaviourHolder
import com.notrace.network.NET_ERROR_MESSAGE
import com.notrace.network.exception.ApiException
import com.notrace.network.gson.ServerResponse
import com.notrace.network.util.TimeUtils
import com.notrace.network.util.convert

import java.lang.reflect.Type
import java.net.SocketException
import java.net.SocketTimeoutException


import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Scheduler
import io.reactivex.exceptions.CompositeException
import io.reactivex.functions.Function
import io.reactivex.plugins.RxJavaPlugins
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException

import io.reactivex.functions.BiFunction
import java.net.UnknownHostException
import com.orhanobut.logger.Logger

internal class RxJava2CallAdapter<R>(
    private val responseType: Type,
    private val scheduler: Scheduler?,
    private val isAsync: Boolean,
    private val isResult: Boolean,
    private val isBody: Boolean,
    private val isFlowable: Boolean,
    private val isSingle: Boolean,
    private val isMaybe: Boolean,
    private val isCompletable: Boolean
) : CallAdapter<R, Any> {

    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): Any {
        val responseObservable = if (isAsync)
            CallEnqueueObservable(call)
        else
            CallExecuteObservable(call)

        var observable = when {
            isResult -> ResultObservable(responseObservable)
            isBody -> BodyObservable(responseObservable)
            else -> responseObservable
        }

        if (scheduler != null) {
            observable = observable.subscribeOn(scheduler)
        }

        observable = observable.map { o ->
            if (o is ServerResponse<*>) {
                TimeUtils.calibrate(o.timestamp)
                if (o.code == API_CODE_SUCCESS) {
                    return@map o.data
                } else {

                    // 处理错误码逻辑
                    Handler(Looper.getMainLooper()).post {
                        ErrorBehaviourHolder.getBehaviours()
                            .filter {
                                it.first == o.code
                            }
                            .forEach {
                                it.second.invoke(o.message)
                            }
                    }
                    throw ApiException(o.message, o.code)
                }
            } else {
                return@map o
            }
        }.retryWhen { throwableObservable ->
            throwableObservable
                .zipWith(Observable.range(1, 4), BiFunction<Throwable, Int, ZipBean> { t1, t2 -> ZipBean(t1, t2) })
                .flatMap { zipBean ->
                    val throwable = zipBean.throwable
                    // 最多尝试3次
                    if (zipBean.times < 4 && throwable is ApiException) {
                        if (throwable.serverCode == API_CODE_TIMER) {
                            Logger.d("匹配到时间戳异常进行重试 %d", zipBean.times)
                            return@flatMap Observable.just("")
                        }
                    }
                    return@flatMap Observable.error<Any>(throwable)
                }
                .onErrorResumeNext(Function<Throwable, ObservableSource<Any>> { it ->
                    it.printStackTrace()

                    val e = if (it is CompositeException) {
                        it.exceptions[it.size() - 1]
                    } else {
                        it
                    }
                    if (e is HttpException
                        || e is SocketException
                        || e is SocketTimeoutException
                        || e is UnknownHostException
                    ) {
                        Observable.error(NetErrorException(NET_ERROR_MESSAGE))
                    } else if (e is ApiException) {
                        Observable.error(e)
                    } else {
                        Observable.error<Any>(e.convert())
                    }
                })
        }

        if (isFlowable) {
            return observable.toFlowable(BackpressureStrategy.LATEST)
        }
        if (isSingle) {
            return observable.singleOrError()
        }
        if (isMaybe) {
            return observable.singleElement()
        }
        return if (isCompletable) {
            observable.ignoreElements()
        } else RxJavaPlugins.onAssembly<Any>(observable)
    }


    private class ZipBean(throwable: Throwable, times: Int) {
        var throwable: Throwable
            internal set
        var times: Int = 0
            internal set

        init {
            this.throwable = throwable
            this.times = times
        }
    }
}
