package com.notrace.network.mvvm.base

import java.io.Serializable

const val SUCCESS = "SUCCESS"
const val ERROR = "ERROR"

open class ApiResponse<T> constructor(
    val code: String,
    val msg: String,
    open val data: T?
) : Serializable {


    companion object {
        @JvmStatic
        fun error(code: String, t: Throwable): ApiErrorResponse<Nothing> {
            return ApiErrorResponse(code, t.message.orEmpty(), t)
        }

        @JvmStatic
        fun <T> success(code: String, msg: String, data: T?): ApiResponse<T> {
            return if (data == null)
                ApiEmptyResponse(code, msg)
            else
                ApiSuccessResponse(code, msg, data)
        }

    }

    override fun toString(): String {
        return "ApiResponse(code='$code', msg='$msg', data=$data)"
    }

}

fun <T> ApiResponse<T>.toResource(): Resource<T> {
    return when (this) {
        is ApiSuccessResponse -> Resource.success(this.data)
        is ApiEmptyResponse -> Resource.success(null)
        is ApiErrorResponse -> Resource.error(this.msg, null)
        else -> Resource.error(this.msg, null)
    }
}

class ApiEmptyResponse<T>(code: String, message: String) : ApiResponse<T>(code, message, null)
class ApiSuccessResponse<T> constructor(code: String, message: String, data: T?) : ApiResponse<T>(code, message, data)
class ApiErrorResponse<T>(code: String, message: String, val throwable: Throwable?) : ApiResponse<T>(code, message, null)


