package com.xjhqre.iot.domain.entity;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xjhqre.common.base.BaseEntity;
import com.xjhqre.common.constant.ScheduleConstants;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.quartz.util.CronUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备定时任务
 * 
 * @author xjhqre
 */
@Data
@TableName("iot_device_job")
public class DeviceJob extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    @ApiModelProperty(value = "任务ID")
    @TableId(value = "job_id", type = IdType.AUTO)
    private Long jobId;

    /** 任务名称 */
    @ApiModelProperty(value = "任务名称")
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 64, message = "任务名称不能超过64个字符")
    private String jobName;

    /**
     * 任务组名
     */
    @ApiModelProperty(value = "任务组名")
    private String jobGroup;

    /**
     * cron执行表达式
     */
    @NotBlank(message = "Cron执行表达式不能为空")
    @Size(max = 255, message = "Cron执行表达式不能超过255个字符")
    @ApiModelProperty(value = "cron执行表达式")
    private String cronExpression;

    /**
     * cron计划策略, 0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行
     */
    @ApiModelProperty(value = "cron计划策略, 0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行")
    private String misfirePolicy = ScheduleConstants.MISFIRE_DEFAULT;

    /**
     * 是否并发执行（0允许 1禁止）
     */
    @ApiModelProperty(value = "是否并发执行（0允许 1禁止）")
    private String concurrent;

    /**
     * 任务状态（0正常 1暂停）
     */
    @ApiModelProperty(value = "任务状态（0正常 1暂停）")
    private Integer status;

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id")
    private Long deviceId;

    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号")
    private String deviceNumber;

    /**
     * 产品ID
     */
    @ApiModelProperty(value = "产品ID")
    private Long productId;

    /**
     * 产品键
     */
    @ApiModelProperty(value = "产品键")
    private String productKey;

    /**
     * 服务物模型id
     */
    private Long modelId;

    /**
     * 服务物模型标识符
     */
    private String identifier;

    /**
     * 值
     */
    private String value;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getNextValidTime() {
        if (StringUtils.isNotEmpty(this.cronExpression)) {
            return CronUtils.getNextExecution(this.cronExpression);
        }
        return null;
    }
}
