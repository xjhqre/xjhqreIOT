/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.xjhqre.common.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.xjhqre.common.constant.HttpStatus;

import lombok.Data;

/**
 * @Author: xjhqre
 * @DateTime: 2022/6/15 17:04
 */
@Data
public class R<T> implements Serializable {

    private Integer code; // 编码：1成功，0和其它数字为失败

    private String msg; // 错误信息

    private T data; // 数据

    private Map map = new HashMap(); // 动态数据

    public static <T> R<T> success(T object) {
        R<T> r = new R<>();
        r.data = object;
        r.code = HttpStatus.SUCCESS;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R<T> r = new R<>();
        r.msg = msg;
        r.code = HttpStatus.ERROR;
        return r;
    }

    public static <T> R<T> error(int code, String msg) {
        R<T> r = new R<>();
        r.msg = msg;
        r.code = code;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
