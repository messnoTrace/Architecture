package com.notrace.network.util

import android.os.SystemClock

/**
 *create by chenyang on 2019/4/2
 **/
internal object TimeUtils {
    var serverTime = System.currentTimeMillis() - SystemClock.elapsedRealtime()
        get() = field + SystemClock.elapsedRealtime()
        private set(value) {
            field = value - SystemClock.elapsedRealtime()
        }

    /**
     * 校准时间
     */
    fun calibrate(input: Long) {
        serverTime = input
    }
}