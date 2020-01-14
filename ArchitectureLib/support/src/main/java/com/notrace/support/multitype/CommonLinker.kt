package com.notrace.support.multitype

import com.drakeet.multitype.Linker

class CommonLinker<T> : Linker<T> {
        override fun index(position: Int, item: T): Int = 0
}