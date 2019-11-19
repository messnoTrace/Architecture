package com.notrace.network.mvvm.base

/***
 * 一个通用类包含了当前状态，数据和提示信息
 */
data class Resource<out T>(val status:Status,val data:T?,val message:String?) {
    companion object{
        fun <T>success(data:T?):Resource<T>{
            return Resource(Status.SUCCESS,data,null)
        }
        fun <T>error(msg:String,data:T?):Resource<T>{
            return Resource(Status.FAILED,data,msg)
        }

        fun <T>loading(data:T?):Resource<T>{
            return Resource(Status.RUNNING,data,null)
        }
    }
}