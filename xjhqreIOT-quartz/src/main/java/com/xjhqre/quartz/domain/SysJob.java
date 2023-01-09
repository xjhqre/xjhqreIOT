package com.xjhqre.quartz.domain;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xjhqre.common.base.BaseEntity;
import com.xjhqre.common.constant.ScheduleConstants;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.quartz.util.CronUtils;

import lombok.Data;

/**
 * 定时任务调度表 sys_job
 * 
 * @author ruoyi
 */
@Data
public class SysJob extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private Long jobId;

    /** 任务名称 */
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 64, message = "任务名称不能超过64个字符")
    private String jobName;

    /** 任务组名 */
    private String jobGroup;

    /** 调用目标字符串 */
    @NotBlank(message = "调用目标字符串不能为空")
    @Size(max = 500, message = "调用目标字符串长度不能超过500个字符")
    private String invokeTarget;

    /** cron执行表达式 */
    @NotBlank(message = "Cron执行表达式不能为空")
    @Size(max = 255, message = "Cron执行表达式不能超过255个字符")
    private String cronExpression;

    /**
     * cron计划策略, 0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行
     */
    private String misfirePolicy = ScheduleConstants.MISFIRE_DEFAULT;

    /** 是否并发执行（0允许 1禁止） */
    private String concurrent;

    /** 任务状态（0正常 1暂停） */
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getNextValidTime() {
        if (StringUtils.isNotEmpty(this.cronExpression)) {
            return CronUtils.getNextExecution(this.cronExpression);
        }
        return null;
    }

}
