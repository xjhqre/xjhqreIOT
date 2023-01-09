package com.xjhqre.iot.quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson2.JSON;
import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.constant.ScheduleConstants;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.ExceptionUtil;
import com.xjhqre.common.utils.SpringUtils;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.iot.domain.entity.DeviceJob;
import com.xjhqre.iot.domain.model.Action;
import com.xjhqre.iot.domain.model.thingsModels.ModelIdAndValue;
import com.xjhqre.iot.mqtt.EmqxService;
import com.xjhqre.quartz.domain.SysJobLog;
import com.xjhqre.quartz.service.SysJobLogService;

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
        // 设备定时任务
        if (deviceJob.getJobType() == 1) {
            System.out.println("------------------------执行定时任务-----------------------------");
            List<Action> actions = JSON.parseArray(deviceJob.getActions(), Action.class);
            List<ModelIdAndValue> properties = new ArrayList<>();
            List<ModelIdAndValue> functions = new ArrayList<>();
            for (Action action : actions) {
                ModelIdAndValue model = new ModelIdAndValue();
                model.setModelId(action.getModelId());
                model.setValue(action.getValue());
                if (action.getType() == 1) {
                    properties.add(model);
                } else if (action.getType() == 2) {
                    functions.add(model);
                }
            }
            EmqxService emqxService = SpringUtils.getBean(EmqxService.class);
            // 发布属性
            if (properties.size() > 0) {
                emqxService.publishProperty(deviceJob.getProductId(), deviceJob.getDeviceNumber(), properties);
            }
            // 发布功能
            if (functions.size() > 0) {
                emqxService.publishFunction(deviceJob.getProductId(), deviceJob.getDeviceNumber(), functions);
            }

        }
        // 告警
        else if (deviceJob.getJobType() == 2) {

            System.out.println("------------------------执行告警-----------------------------");
        }
        // 场景联动
        else if (deviceJob.getJobType() == 3) {

            System.out.println("------------------------执行场景联动-----------------------------");
        }
    }

    /**
     * 执行后
     *
     */
    protected void after(DeviceJob deviceJob, Exception e, Date startTime) {
        final SysJobLog sysJobLog = new SysJobLog();
        sysJobLog.setJobName(deviceJob.getJobName());
        sysJobLog.setJobGroup(deviceJob.getJobGroup());
        sysJobLog.setInvokeTarget(deviceJob.getDeviceName());
        sysJobLog.setStartTime(startTime);
        sysJobLog.setStopTime(new Date());
        long runMs = sysJobLog.getStopTime().getTime() - sysJobLog.getStartTime().getTime();
        sysJobLog.setJobMessage(sysJobLog.getJobName() + " 总共耗时：" + runMs + "毫秒");
        if (e != null) {
            sysJobLog.setStatus(Constants.FAIL);
            String errorMsg = StringUtils.substring(ExceptionUtil.getExceptionMessage(e), 0, 2000);
            sysJobLog.setExceptionInfo(errorMsg);
        } else {
            sysJobLog.setStatus(Constants.SUCCESS);
        }

        // 写入数据库当中
        SpringUtils.getBean(SysJobLogService.class).save(sysJobLog);
    }

}
