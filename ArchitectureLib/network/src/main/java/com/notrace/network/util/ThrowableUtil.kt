package com.notrace.network.util


import cn.com.open.shuxiaotong.netlib.exception.DataErrorException
import com.notrace.network.API_CODE_EMPTY
import com.notrace.network.API_CODE_NET_ERROR
import com.notrace.network.exception.ApiException
import com.orhanobut.logger.Logger

fun Throwable.convert(): ApiException {
    if (this is ApiException) {
        return this
    }
    var msg = if (message.isNullOrBlank()) {
        ""
    } else {
        message
    }

    Logger.e(this, "接口异常")

    // 优化异常信息的显示
    msg = if (msg!!.length <= 50 && CharacterUtil.containChinese(msg)) msg else "获取数据失败"
    return DataErrorException(msg)
}

fun Throwable.netError(): Boolean {
    return if (this is ApiException) {
        this.serverCode == API_CODE_NET_ERROR
    } else {
        false
    }
}

fun Throwable.noData(): Boolean {
    return if (this is ApiException) {
        this.serverCode == API_CODE_EMPTY
    } else {
        false
    }
}
