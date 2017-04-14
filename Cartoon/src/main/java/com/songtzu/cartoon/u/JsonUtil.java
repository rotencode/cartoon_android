package com.songtzu.cartoon.u;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2016/10/26.
 */
public class JsonUtil {
    private static JsonUtil util;
    private Gson gson = new Gson();

    private JsonUtil() {
    }

    public static JsonUtil getJsonUtil() {
        if (util == null) {
            util = new JsonUtil();
        }

        return util;
    }

    public <T> T fromJson(String jsonData, Class<T> clz) {
        return this.gson.fromJson(jsonData, clz);
    }

    public <T> T fromJson(String jsonData, Type type) {
        return this.gson.fromJson(jsonData, type);
    }

    public String toJson(Object object) {
        return this.gson.toJson(object);
    }

}
