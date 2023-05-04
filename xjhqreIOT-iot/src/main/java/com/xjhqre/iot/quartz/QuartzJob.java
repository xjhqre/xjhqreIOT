package com.xjhqre.iot.quartz;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;

import java.util.Date;

import com.xjhqre.common.utils.ExceptionUtil;
import com.xjhqre.iot.constant.LogTypeConstant;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.DeviceLog;
import com.xjhqre.iot.service.DeviceLogService;
import com.xjhqre.iot.service.DeviceService;
import org.apache.commons.lang3.StringUtils;
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

import javax.annotation.Resource;

/**
 * QuartzJob
 *
 * @author xjhqre
 * @since 2023-1-7
 */
@Slf4j
public class QuartzJob implements Job {

    @Resource
    DeviceService deviceService;

    @Override
    public void execute(JobExecutionContext context) {
        DeviceJob deviceJob = new DeviceJob();
        BeanUtils.copyProperties(context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES), deviceJob);
        Device device = deviceService.getById(deviceJob.getDeviceId());
        if (device.getStatus() != 3) {
            return;
        }
        Date startTime = DateUtils.getNowDate();
        String message = "";
        try {
            message = this.doExecute(deviceJob);
            this.after(device, deviceJob, message, null);
        } catch (Exception e) {
            log.error("任务执行异常  - ：", e);
            this.after(device, deviceJob, message, e);
        }
    }

    /**
     * 执行任务
     * 
     */
    private String doExecute(DeviceJob deviceJob) {
        log.info("执行定时任务：{}", deviceJob);
        // 设备定时任务
        ModelIdAndValue modelIdAndValue = new ModelIdAndValue();
        modelIdAndValue.setIdentifier(deviceJob.getIdentifier());
        modelIdAndValue.setValue(deviceJob.getValue());
        EmqxService emqxService = SpringUtils.getBean(EmqxService.class);
        // 调用设备服务
        emqxService.callService(deviceJob.getProductKey(), deviceJob.getDeviceNumber(), modelIdAndValue);
        return JSON.toJSONString(modelIdAndValue);
    }

    /**
     * 执行后
     *
     */
    protected void after(Device device, DeviceJob deviceJob, String message, Exception e) {
        DeviceLog deviceLog = new DeviceLog();
        deviceLog.setLogType(LogTypeConstant.TIMED_TASK);
        deviceLog.setMessage(message);
        deviceLog.setProductId(device.getProductId());
        deviceLog.setProductName(device.getProductName());
        deviceLog.setDeviceId(device.getDeviceId());
        deviceLog.setDeviceName(device.getDeviceName());
        deviceLog.setRouter("/" + deviceJob.getProductKey() + "/" + deviceJob.getDeviceNumber() + "/service/get");
        deviceLog.setQos(1);
        deviceLog.setCreateTime(new Date());
        deviceLog.setUpdateTime(new Date());

        if (e != null) {
            String errorMsg = StringUtils.substring(ExceptionUtil.getExceptionMessage(e), 0, 2000);
            deviceLog.setMessage(errorMsg);
        }
        // 写入数据库当中
        SpringUtils.getBean(DeviceLogService.class).save(deviceLog);
    }

}
