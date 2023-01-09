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
 * @author kerwincui
 * @date 2022-01-13
 */
@Data
@TableName("iot_scene")
public class Scene extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 场景ID
     */
    @ApiModelProperty(name = "场景ID")
    @TableId(value = "scene_id", type = IdType.AUTO)
    private Long sceneId;

    /**
     * 场景名称
     */
    @ApiModelProperty(name = "场景名称")
    private String sceneName;

    /**
     * 用户ID
     */
    @ApiModelProperty(name = "用户ID")
    private Long userId;

    /**
     * 用户名称
     */
    @ApiModelProperty(name = "用户名称")
    private String userName;

    /**
     * 触发器
     */
    @ApiModelProperty(name = "触发器")
    private String triggers;

    /**
     * 执行动作
     */
    @ApiModelProperty(name = "执行动作")
    private String actions;
}
