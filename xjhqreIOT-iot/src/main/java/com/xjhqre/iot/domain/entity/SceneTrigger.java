package com.xjhqre.iot.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import lombok.Data;

/**
 * <p>
 * SceneTrigger
 * </p>
 *
 * @author xjhqre
 * @since 3月 29, 2023
 */
@Data
@TableName("iot_scene_trigger")
public class SceneTrigger extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "trigger_id", type = IdType.AUTO)
    private Long triggerId;

    /**
     * 关联场景id
     */
    private Long sceneId;

    /**
     * 关联场景名称
     */
    private String sceneName;

    /**
     * 设备id
     */
    private Long deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 定时表达式
     */
    private String cronExpression;

    /**
     * 物模型id
     */
    private Long modelId;

    /**
     * 物模型名称
     */
    private String modelName;

    /**
     * 操作符
     */
    private String operator;

    /**
     * 值
     */
    private String value;
}
