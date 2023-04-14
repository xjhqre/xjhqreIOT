package com.xjhqre.iot.domain.entity;

import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;
import com.xjhqre.iot.domain.model.Enum;

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
     * 物模型id
     */
    private Long modelId;

    /**
     * 物模型编码
     */
    private String identifier;

    /**
     * 参数值
     */
    private String value;

    /**
     * 服务物模型
     */
    @TableField(exist = false)
    private ThingsModel serviceModel;

    /**
     * 服务入参类型
     */
    @TableField(exist = false)
    private String type;

    /**
     * 服务物列表
     */
    @TableField(exist = false)
    private List<ThingsModel> deviceThingModel;

    /**
     * 服务入参枚举列表
     */
    @TableField(exist = false)
    private List<Enum> enumList;

    /**
     * 服务布尔型入参真值
     */
    @TableField(exist = false)
    private String trueText;

    /**
     * 服务布尔型入参假值
     */
    @TableField(exist = false)
    private String falseText;
}
