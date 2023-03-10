package com.xjhqre.iot.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 场景联动对象 iot_scene
 * 
 * @author xjhqre
 * @date 2022-01-13
 */
@Data
@TableName("iot_scene")
public class Scene extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 场景ID
     */
    @ApiModelProperty(value = "场景ID", hidden = true)
    @TableId(value = "scene_id", type = IdType.AUTO)
    private Long sceneId;

    /**
     * 场景名称
     */
    @ApiModelProperty(value = "场景名称")
    private String sceneName;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    private String userName;

    /**
     * 触发器
     */
    @ApiModelProperty(value = "触发器")
    private String triggers;

    /**
     * 执行动作
     */
    @ApiModelProperty(value = "执行动作")
    private String actions;
}
