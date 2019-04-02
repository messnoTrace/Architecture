package cn.com.open.shuxiaotong.netlib.exception

import com.notrace.network.API_CODE_DATA_ERROR
import com.notrace.network.exception.ApiException


/**
 * 未知或未处理的异常
 */

class DataErrorException : ApiException {

    constructor(message: String) : super(message, API_CODE_DATA_ERROR)

    constructor(message: String, e: Exception) : super(message, API_CODE_DATA_ERROR, e)
}
