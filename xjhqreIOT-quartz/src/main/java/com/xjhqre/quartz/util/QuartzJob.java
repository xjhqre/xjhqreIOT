package com.xjhqre.quartz.util;

import java.util.Date;

import org.quartz.JobExecutionContext;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.constant.ScheduleConstants;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.ExceptionUtil;
import com.xjhqre.common.utils.SpringUtils;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.quartz.domain.SysJob;
import com.xjhqre.quartz.domain.SysJobLog;
import com.xjhqre.quartz.service.SysJobLogService;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * QuartzJob
 * </p>
 *
 * @author xjhqre
 * @since 1月 06, 2023
 */
@Slf4j
public class QuartzJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) {
        SysJob sysJob = new SysJob();
        BeanUtils.copyProperties(context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES), sysJob);
        Date startTime = DateUtils.getNowDate();
        try {
            this.doExecute(sysJob);
            this.after(sysJob, startTime, null);
        } catch (Exception e) {
            log.error("任务执行异常  - ：", e);
            this.after(sysJob, startTime, e);
        }
    }

    private void doExecute(SysJob sysJob) throws Exception {
        // 执行方法
        JobInvokeUtil.invokeMethod(sysJob);
    }

    private void after(SysJob sysJob, Date startTime, Exception e) {
        final SysJobLog sysJobLog = new SysJobLog();
        sysJobLog.setJobName(sysJob.getJobName());
        sysJobLog.setJobGroup(sysJob.getJobGroup());
        sysJobLog.setInvokeTarget(sysJob.getInvokeTarget());
        sysJobLog.setStartTime(startTime);
        sysJobLog.setStopTime(DateUtils.getNowDate());
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
