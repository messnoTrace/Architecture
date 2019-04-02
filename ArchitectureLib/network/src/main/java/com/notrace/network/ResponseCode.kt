package com.notrace.network

/**
 *create by chenyang on 2019/4/2
 **/

const val API_CODE_SUCCESS = 1000

/**
 * 内容为空
 */
const val API_CODE_EMPTY = 5

/**
 * 登录用户的token错误
 */
const val API_CODE_TOKEN_ERROR = 6

/**
 * 时间戳错误
 */
const val API_CODE_TIMER = 7

/**
 * 本地预置错误码：未知错位或未处理的错误
 */
const val API_CODE_DATA_ERROR = -2
/**
 * 本地预置错误码：网络错误
 */
const val API_CODE_NET_ERROR = -1

const val NET_ERROR_MESSAGE="网络连接失败，请稍后重试"
internal object ErrorBehaviourHolder {

    private val behaviours = mutableListOf<Pair<Int, (message: String) -> Unit>>()

    fun addBehaviour(behavior: Pair<Int, (message: String) -> Unit>) {
        behaviours.add(behavior)
    }

    fun getBehaviours(): List<Pair<Int, (message: String) -> Unit>> = behaviours
}