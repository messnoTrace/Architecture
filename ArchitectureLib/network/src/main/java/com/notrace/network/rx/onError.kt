package com.notrace.network.rx

/**
 *create by chenyang on 2019/4/2
 **/
interface OnError {
    /**
     * 异常回调
     * @param errorCode 错误码 : ApiException
     * @param message 错误描述
     */
    fun onError(errorCode: Int, message: String)
}