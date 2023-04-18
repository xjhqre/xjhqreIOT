package com.xjhqre.iot.domain.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * <p>
 * DeviceJobDTO
 * </p>
 *
 * @author xjhqre
 * @since 4月 15, 2023
 */
@Data
public class DeviceJobAddDTO implements Serializable {
    private static final long serialVersionUID = -5881231712606340756L;

    /** 任务名称 */
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 64, message = "任务名称不能超过64个字符")
    private String jobName;

    /**
     * cron执行表达式
     */
    @NotBlank(message = "Cron执行表达式不能为空")
    @Size(max = 255, message = "Cron执行表达式不能超过255个字符")
    private String cronExpression;

    /**
     * 任务状态（0正常 1暂停）
     */
    private Integer status;

    /**
     * 设备id
     */
    private Long deviceId;

    /**
     * 服务物模型id
     */
    private Long modelId;

    /**
     * 值
     */
    private String value;
}
