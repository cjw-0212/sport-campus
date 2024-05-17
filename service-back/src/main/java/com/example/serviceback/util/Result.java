package com.example.serviceback.util;

import lombok.Data;

/**
 * 统一响应结果类
 *
 * @author CJW
 * @since 2024/3/21
 */
@Data
public class Result<T> {
    /**
     * 响应状态码,1表示成功，非1表示失败
     */
    private Integer code;
    /**
     * 错误信息
     */
    private String message;
    /**
     * 响应携带的数据
     */
    private T data;

    /**
     * 成功响应快捷方法
     *
     * @param object 携带的数据对象
     * @return 一个成功的响应
     */
    public static <E> Result<E> success(E object) {
        Result<E> result = new Result<>();
        result.code = 1;
        result.data = object;
        return result;
    }

    public static Result<Void> success() {
        Result<Void> result = new Result<>();
        result.code = 1;
        return result;
    }

    /**
     * 失败响应快捷方法
     *
     * @param message 错误的提示信息
     * @return 一个失败的响应
     */
    public static <E> Result<E> error(String message) {
        Result<E> result = new Result<>();
        result.code = 0;
        result.message = message;
        return result;
    }
}
