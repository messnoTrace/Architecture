package com.notrace.network.gson

import com.google.gson.annotations.SerializedName

/**
 *create by chenyang on 2019/4/2
 * 服务器返回基本结构
 **/
data class ServerResponse<T>(
    @field:SerializedName("code")
    val code: Int,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("data")
    val data: T?,
    @field:SerializedName("timestamp")
    val timestamp: Long
)