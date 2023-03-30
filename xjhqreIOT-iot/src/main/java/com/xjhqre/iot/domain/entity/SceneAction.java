package com.xjhqre.iot.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import lombok.Data;

/**
 * <p>
 * SceneAction
 * </p>
 *
 * @author xjhqre
 * @since 3月 29, 2023
 */
@Data
@TableName("iot_scene_action")
public class SceneAction extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "action_id", type = IdType.AUTO)
    private Long actionId;

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
     * 服务调用JSON语句
     */
    private String value;
}
