package com.xjhqre.iot.domain.model.thingsModels;

import com.alibaba.fastjson2.JSONObject;

import lombok.Data;

/**
 * 用于描述设备运行时具体信息和状态。 例如，环境监测设备所读取的当前环境温度、智能灯开关状态、电风扇风力等级等。 属性可分为读写和只读两种类型。读写类型支持读取和设置属性值，只读类型仅支持读取属性值。
 *
 * @author xjhqre
 * @date 2022-12-16
 */
@Data
public class PropertyVO {
    /**
     * 物模型唯一标识符
     */
    private String id;

    /**
     * 物模型名称
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
     * 是否实时监测（0-否，1-是）
     */
    private Integer isMonitor;

    /**
     * 数据定义
     */
    private JSONObject datatype;
}
