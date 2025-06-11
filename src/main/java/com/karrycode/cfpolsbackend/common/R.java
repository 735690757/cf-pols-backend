package com.karrycode.cfpolsbackend.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @param <T>
 */
@Data
public class R<T> {
    // 状态码
    private Integer code;
    //错误信息
    private String msg;
    //数据
    private T data;

    private Map map = new HashMap(); //动态数据

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 200;
        return r;
    }

    public static <T> R<T> success(T object, String msg) {
        R<T> r = new R<T>();
        r.msg = msg;
        r.data = object;
        r.code = 200;
        return r;
    }

    public static <T> R<T> success(T object, String msg, Integer code) {
        R<T> r = new R<T>();
        r.msg = msg;
        r.data = object;
        r.code = code;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 500;
        return r;
    }

    public static <T> R<T> error(String msg, Integer code) {
        R r = new R();
        r.msg = msg;
        r.code = code;
        return r;
    }

    public static <T> R<T> error(String msg, Integer code, T object) {
        R r = new R();
        r.msg = msg;
        r.code = code;
        r.data = object;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
