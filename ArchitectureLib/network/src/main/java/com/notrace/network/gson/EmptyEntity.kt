package com.notrace.network.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 *create by chenyang on 2019/4/2
 * 适配completable中传入的空类型
 **/
class EmptyEntity

class EmptyTypeAdapter : JsonDeserializer<EmptyEntity> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): EmptyEntity {
        return EmptyEntity()
    }
}