package com.notrace.network.base

import java.io.Serializable

open class BaseHttpResult<out DATA> constructor(
    val code: String,
    val msg: String,
    open val data: DATA? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 2338354325715964704L

        fun createEmpty() = EmptyDataResult(ResponseData.SUCCESS, "")
    }


}


class EmptyDataResult(
    code: String,
    msg: String

) : BaseHttpResult<EmptyData>(code, msg) {

    companion object {
        private val emptyData: EmptyData = EmptyData()
    }

    override val data: EmptyData?
        get() = emptyData

    fun ok(): Boolean = ResponseData.SUCCESS==(code)

}

class EmptyData
object  ResponseData{

    const val SUCCESS="1000"
}