package cn.com.open.shuxiaotong.netlib.exception

import com.notrace.network.API_CODE_NET_ERROR
import com.notrace.network.exception.ApiException


/**
 * 网络链接失败的异常
 */

class NetErrorException : ApiException {

    constructor(message: String) : super(message, API_CODE_NET_ERROR)

    constructor(message: String, e: Exception) : super(message, API_CODE_NET_ERROR, e)
}
