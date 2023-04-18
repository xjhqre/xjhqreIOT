package com.xjhqre.iot.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import lombok.Data;

/**
 * <p>
 * 场景联动日志
 * </p>
 *
 * @author xjhqre
 * @since 4月 15, 2023
 */
@Data
@TableName("iot_scene_log")
public class SceneLog extends BaseEntity {

    private static final long serialVersionUID = 7146907863963220504L;

    /** 主键 */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /** 场景联动id */
    private Long sceneId;

    /** 场景联动名称 */
    private String sceneName;

    /** 触发数据 */
    private String triggerData;

    /** 动作数据 */
    private String actions;

}
