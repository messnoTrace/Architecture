package com.notrace.network.util

/**
 *create by chenyang on 2019/4/3
 **/

sealed class BooleanUtil<out T>

object Otherwise : BooleanUtil<Nothing>()

class WithData<T>(val data: T) : BooleanUtil<T>()

inline fun <T> Boolean.yes(block: () -> T) =
    when {
        this -> WithData(block())
        else -> Otherwise
    }

inline fun <T> Boolean.no(block: () -> T) = when {
    this -> Otherwise
    else -> WithData(block())

}

inline fun <T> BooleanUtil<T>.otherwise(block: () -> T): T =
    when (this) {
        is Otherwise -> block()
        is WithData -> this.data
    }