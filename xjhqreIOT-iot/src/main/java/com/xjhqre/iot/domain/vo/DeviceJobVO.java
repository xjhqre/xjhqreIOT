package com.xjhqre.iot.domain.vo;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xjhqre.iot.domain.entity.DeviceJob;
import com.xjhqre.iot.domain.entity.ThingsModel;
import com.xjhqre.iot.domain.model.Enum;

import lombok.Data;

/**
 * <p>
 * DeviceJobVO
 * </p>
 *
 * @author xjhqre
 * @since 4月 15, 2023
 */
@Data
public class DeviceJobVO extends DeviceJob {
    private static final long serialVersionUID = 7105789817598576388L;

    /**
     * 定时任务动作名称
     */
    @TableField(exist = false)
    private String actionName;

    /**
     * 产品动作列表
     */
    @TableField(exist = false)
    private List<ThingsModel> deviceThingModel;

    /**
     * 定时任务选中的服务
     */
    @TableField(exist = false)
    private ThingsModel serviceModel;

    /**
     * 定时任务动作类型，integer、string、enum
     */
    @TableField(exist = false)
    private String type;

    /**
     * 定时任务动作，枚举类型的列表
     */
    @TableField(exist = false)
    private List<Enum> enumList;

    /**
     * 定时任务动作，布尔类型的真值
     */
    @TableField(exist = false)
    private String trueText;

    /**
     * 定时任务动作，布尔类型的假值
     */
    @TableField(exist = false)
    private String falseText;
}
