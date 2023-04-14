package com.xjhqre.iot.domain.entity;

import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
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
     * 条件限制，部分满足或全部满足，值为 any 或者 all
     */
    @JsonProperty("condition")
    private String restriction;

    /**
     * 是否启用，1：启动 0：禁用
     */
    private Integer status;

    /**
     * 触发器
     */
    @TableField(exist = false)
    private List<SceneTrigger> triggers;

    /**
     * 执行动作
     */
    @TableField(exist = false)
    private List<SceneAction> actions;
}
