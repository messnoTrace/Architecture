package com.notrace.network.gson;

import com.google.gson.*;
import com.notrace.network.ResponseCodeKt;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * create by chenyang on 2019/4/2
 **/
public class ServerResponseTypeAdapter implements JsonDeserializer<ServerResponse> {

    //这个字段的意思表示这里的返回值，是不是和你们服务器的一致
    private boolean isSameOfServer;

    @Override
    public ServerResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        if (!json.isJsonObject()) {
            throw new IllegalArgumentException("JsonElement should be JsonObject but now is:" + json.getClass().getName());
        }

        //TODO 这里有个地方要注意，当我们指定了返回的数据类型的时候，就表示会限定死，扩展性就没那么好了，如果服务器不是这种格式，这里代码解析就需要动一动了,这么写不好，但是怎么改，是个问题
        JsonObject object = json.getAsJsonObject();
        if (isSameOfServer) {
            int errorCode = object.get("code").getAsInt();
            String messsage = object.get("message").getAsString();
            long time = object.get("timestamp").getAsLong();
            Object data = null;
            if (errorCode == ResponseCodeKt.API_CODE_SUCCESS) {
                data = context.deserialize(object.get("data"), ((ParameterizedType) typeOfT).getActualTypeArguments()[0]);
            }
            return new ServerResponse(errorCode, messsage, data, time);
        } else {
            Object data = null;
            data = context.deserialize(object, ((ParameterizedType) typeOfT).getActualTypeArguments()[0]);
            return new ServerResponse(ResponseCodeKt.API_CODE_SUCCESS, "success", data, System.currentTimeMillis());
        }

    }

    public ServerResponseTypeAdapter(boolean isSameOfServer) {

        this.isSameOfServer = isSameOfServer;
    }
}
