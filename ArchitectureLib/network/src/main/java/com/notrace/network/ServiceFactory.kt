package com.notrace.network

import com.google.gson.GsonBuilder
import com.notrace.network.adapter.CommonConvertFactory
import com.notrace.network.adapter.LiveDataCallAdapterFactory
import com.notrace.network.adapter.RxJava2CallAdapterFactory
import com.notrace.network.gson.*
import com.notrace.network.util.TimeUtils
import com.orhanobut.logger.Logger
import io.reactivex.schedulers.Schedulers
import okhttp3.FormBody
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

/**
 *create by chenyang on 2019/4/2
 **/
object ServiceFactory {
    val gson by lazy {
        GsonBuilder().registerTypeAdapter(EmptyEntity::class.java, EmptyTypeAdapter())
            .registerTypeAdapter(ServerResponse::class.java, ServerResponseTypeAdapter(false))
            .registerTypeAdapter(Boolean::class.java, BooleanTypeAdapter())
            .registerTypeAdapter(Int::class.java, IntZeroAdapter())
            .create()
    }
    private var retrofit: Retrofit
    private val commonParameters: MutableMap<String, () -> String> = HashMap()

    private var baseUrl = "https://api.github.com/"

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create(
                    ServerResponse::class.java

                )
            )
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create(String::class.java))
            .addCallAdapterFactory( LiveDataCallAdapterFactory())
            .addConverterFactory(CommonConvertFactory.create(gson))
//            .addConverterFactory(GsonConverterFactory.create(gson))

            .client(initClient())
            .build()

    }

    private fun initClient(): OkHttpClient {
        return OkHttpClient.Builder()
            /**
             * 日志
             */
            .addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Logger.d(it)
            }).apply { level = HttpLoggingInterceptor.Level.BODY })
            /**
             * 参数
             */
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    //                    .addHeader("App-Info", AppEnvironment.appInfo())
                    .apply {
                        chain.request().run {
                            if (method().equals("POST", true)) {
                                val oldBody = body()
                                when {
                                    //参数加密过程
                                    oldBody is FormBody -> this@apply.post(oldBody.formRequest())
                                    oldBody is MultipartBody -> this@apply.post(oldBody.multipartRequest())
                                    else -> oldBody?.request()?.let {
                                        this@apply.post(it)
                                    }
                                }
                            }
                        }
                    }
                    .build()

                return@addInterceptor chain.proceed(request)
            }
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }


    /**
     * 添加公共参数
     */
    fun addCommParameter(key: String, value: () -> String) {
        commonParameters[key] = value
    }

    /**
     * 获取接口实现
     */
    fun <T> convertService(clazz: Class<T>): T {
        return retrofit.create(clazz)
    }

    /**
     * 添加全局异常的处理
     */
    fun addGlobalBehaviour(errorCode: Int, block: (message: String) -> Unit) {
        ErrorBehaviourHolder.addBehaviour(Pair(errorCode, block))
    }

    /**
     * 处理form请求逻辑
     */
    private fun FormBody.formRequest(): FormBody {

        val map: MutableMap<String, String> = HashMap()
        for (i in 0 until this.size()) {
            map[this.name(i)] = this.value(i)
        }

        commonParameters.forEach {
            map[it.key] = it.value()
        }

        val newFormBodyBuilder = FormBody.Builder()
        // Verify添加
        map.generateVerify().forEach {
            newFormBodyBuilder.add(it.key, it.value)
        }

        return newFormBodyBuilder.build()
    }

    /**
     * 处理retort无参数请求逻辑
     */
    private fun RequestBody.request(): RequestBody? {
        var length = try {
            contentLength()
        } catch (e: IOException) {
            -1
        }

        return if (length == 0L && contentType() == null) {
            val map: MutableMap<String, String> = HashMap()
            commonParameters.forEach {
                map[it.key] = it.value()
            }

            val newFormBodyBuilder = FormBody.Builder()
            // Verify添加
            map.generateVerify().forEach {
                newFormBodyBuilder.add(it.key, it.value)
            }
            newFormBodyBuilder.build()
        } else {
            null
        }
    }

    /**
     * 处理Multipart请求逻辑
     */
    private fun MultipartBody.multipartRequest(): MultipartBody {
        val map: MutableMap<String, String> = HashMap()
        commonParameters.forEach {
            map[it.key] = it.value()
        }

        val multipartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        // Verify添加
        map.generateVerify().forEach {
            multipartBuilder.addFormDataPart(it.key, it.value)
        }

        this.parts().forEach {
            multipartBuilder.addPart(it)
        }
        return multipartBuilder.build()
    }

    /**
     * 生成加密参数
     */
    private fun MutableMap<String, String>.generateVerify(): MutableMap<String, String> {
        /**
         * 1.过滤非法参数
         * 2.链接成a=b&c=d
         * 3.添加固定字符串
         * 4.md5加密
         * 5.加入到参数map中k
         */

        toSortedMap().filter {
            it.key != null
        }.toList().joinToString("&") { it ->
            "${it.first}=${it.second}"
        }.let {
            //TODO 加密字符串
            it + "commonLib"
        }.let {
            MessageDigest.getInstance("MD5")
                .digest(it.toByteArray())
                .joinToString("") {
                    String.format("%02x", it)
                }
        }.let {
            //TODO 添加加密后的key-value
            this.put("sign", it)
        }
        return this
    }

    @JvmStatic
    fun serverTime(): String {
        return TimeUtils.serverTime.toString()
    }
}