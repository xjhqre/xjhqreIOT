package com.xjhqre.iot.quartz;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.BeanUtils;

import com.xjhqre.common.constant.ScheduleConstants;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SpringUtils;
import com.xjhqre.iot.domain.entity.DeviceJob;
import com.xjhqre.iot.domain.model.thingsModels.ModelIdAndValue;
import com.xjhqre.iot.mqtt.EmqxService;

import lombok.extern.slf4j.Slf4j;

/**
 * QuartzJob
 *
 * @author xjhqre
 * @since 2023-1-7
 */
@Slf4j
public class QuartzJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        DeviceJob deviceJob = new DeviceJob();
        BeanUtils.copyProperties(context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES), deviceJob);
        Date startTime = DateUtils.getNowDate();
        try {
            this.doExecute(deviceJob);
            this.after(deviceJob, null, startTime);
        } catch (Exception e) {
            log.error("任务执行异常  - ：", e);
            this.after(deviceJob, e, startTime);
        }
    }

    /**
     * 执行任务
     * 
     */
    private void doExecute(DeviceJob deviceJob) {
        log.info("执行定时任务：{}", deviceJob);
        // 设备定时任务
        ModelIdAndValue modelIdAndValue = new ModelIdAndValue();
        modelIdAndValue.setIdentifier(deviceJob.getIdentifier());
        modelIdAndValue.setValue(deviceJob.getValue());
        EmqxService emqxService = SpringUtils.getBean(EmqxService.class);
        // 调用设备服务
        emqxService.callService(deviceJob.getProductKey(), deviceJob.getDeviceNumber(), modelIdAndValue);
    }

    /**
     * 执行后
     *
     */
    protected void after(DeviceJob deviceJob, Exception e, Date startTime) {
        // final SysJobLog sysJobLog = new SysJobLog();
        // sysJobLog.setJobName(deviceJob.getJobName());
        // sysJobLog.setJobGroup(deviceJob.getJobGroup());
        // sysJobLog.setInvokeTarget(deviceJob.getDeviceName());
        // sysJobLog.setStartTime(startTime);
        // sysJobLog.setStopTime(new Date());
        // long runMs = sysJobLog.getStopTime().getTime() - sysJobLog.getStartTime().getTime();
        // sysJobLog.setJobMessage(sysJobLog.getJobName() + " 总共耗时：" + runMs + "毫秒");
        // if (e != null) {
        // sysJobLog.setStatus(Constants.FAIL);
        // String errorMsg = StringUtils.substring(ExceptionUtil.getExceptionMessage(e), 0, 2000);
        // sysJobLog.setExceptionInfo(errorMsg);
        // } else {
        // sysJobLog.setStatus(Constants.SUCCESS);
        // }
        //
        //// 写入数据库当中
        // SpringUtils.getBean(SysJobLogService.class).save(sysJobLog);
    }

}
