package com.notrace.network.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.notrace.network.base.BaseHttpResult;
import com.notrace.network.mvvm.base.ApiResponse;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class CommonConvertFactory extends Converter.Factory {
    Gson gson;

    public static CommonConvertFactory create(Gson gson) {
        return new CommonConvertFactory(gson);
    }

    private CommonConvertFactory(Gson gson) {
        this.gson = gson;
    }

    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonRequestBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new GsonResponseBodyConverter(gson, gson.getAdapter(TypeToken.get(type)));
    }

    class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Gson gson;
        private final TypeAdapter<T> adapter;

        GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public T convert(@NotNull ResponseBody value) throws IOException {
            JsonReader jsonReader = null;
            T result=null;
            try {
//                System.out.println("----value"+value.string());
                jsonReader = gson.newJsonReader(value.charStream());
                 result = adapter.read(jsonReader);
                if (result instanceof BaseHttpResult) {
                    BaseHttpResult baseHttpResult = (BaseHttpResult) result;

                } else if (result instanceof ApiResponse) {
                    ApiResponse response = (ApiResponse) result;
                    result = (T) (ApiResponse.success(response.getCode(), response.getMsg(), response.getData()));

                }
                return result;

            } catch (Exception e){
                e.printStackTrace();
            }finally {
                jsonReader.close();
            }

            return result;
        }
    }

}
