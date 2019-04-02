package com.notrace.network.gson;

import com.google.gson.*;
import com.notrace.network.ResponseCodeKt;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * create by chenyang on 2019/4/2
 **/
public class ServerResponseTypeAdapter implements JsonDeserializer<ServerResponse> {
    @Override
    public ServerResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        if (!json.isJsonObject()) {
            throw new IllegalArgumentException("JsonElement should be JsonObject but now is:" + json.getClass().getName());
        }
        JsonObject object = json.getAsJsonObject();
        int errorCode = object.get("code").getAsInt();
        String messsage = object.get("message").getAsString();
        long time = object.get("timestamp").getAsLong();
        Object data = null;
        if (errorCode == ResponseCodeKt.API_CODE_SUCCESS) {
            data = context.deserialize(object.get("data"), ((ParameterizedType) typeOfT).getActualTypeArguments()[0]);
        }
        return new ServerResponse(errorCode, messsage, data, time);
    }
}
