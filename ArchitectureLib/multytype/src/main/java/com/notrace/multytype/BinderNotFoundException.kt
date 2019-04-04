package com.notrace.multytype

/**
 *create by chenyang on 2019/4/3
 **/
internal class BinderNotFoundException(clazz: Class<*>) : RuntimeException(
    "Have you registered the ${clazz.name} type and its binder to the adapter or type list?"
)