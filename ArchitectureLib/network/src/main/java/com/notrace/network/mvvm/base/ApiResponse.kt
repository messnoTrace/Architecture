package com.notrace.network.mvvm.base

import retrofit2.Response

const val SUCCESS = "SUCCESS"
const val ERROR = "ERROR"

open class ApiResponse< T> constructor(val code: String, val message: String, open val data: T?) {


    companion object {
        fun <T> create(code: String, message: String): ApiErrorResponse<T> {
            return ApiErrorResponse(code, message)
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
//            return if (response.code == (SUCCESS)) {
//
//                ApiSuccessResponse(response.code, response.message, response.data)
//            } else {
//                //TODO 空是啥样？这里写个错误的
//                ApiErrorResponse(response.code, response.message)
//            }
//            /**
            return if (response.isSuccessful) {
                val body = response.body() as ApiResponse<T>
                ApiSuccessResponse(SUCCESS, SUCCESS, body.data)

            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                ApiErrorResponse(ERROR, errorMsg ?: "unknown error")
            }

        }
//             **/

    }

}

fun <T> ApiResponse<T>.toResource(): Resource<T> {
    return when (this) {
        is ApiSuccessResponse -> Resource.success(this.data)
        is ApiEmptyResponse -> Resource.success(null)
        is ApiErrorResponse -> Resource.error(this.message, null)
        else -> Resource.error(this.message, null)
    }
}

class ApiEmptyResponse<T>(code: String, message: String) : ApiResponse<T>(code, message, null)
class ApiSuccessResponse<T> constructor(code: String, message: String, data: T?) : ApiResponse<T>(code, message, data)
class ApiErrorResponse<T>(code: String, errorMessage: String) : ApiResponse<T>(code, errorMessage, null)


