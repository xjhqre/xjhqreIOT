package com.xjhqre.iot.domain.model.thingsModels;

import com.alibaba.fastjson2.JSONObject;

import lombok.Data;

/**
 * 指设备可供外部调用的指令或方法。服务调用中可设置输入和输出参数。输入参数是服务执行时的参数，输出参数是服务执行后的结果。 相比于属性，服务可通过一条指令实现更复杂的业务逻辑，例如执行某项特定的任务。 服务分为异步和同步两种调用方式。
 *
 * @author xjhqre
 * @date 2022-12-16
 */
@Data
public class FunctionVO {
    /**
     * 物模型唯一标识符
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 模型排序
     */
    private Integer sort;

    /**
     * 是否首页显示（0-否，1-是）
     */
    private Integer isTop;

    /**
     * 数据类型
     */
    private JSONObject datatype;
}
