package com.xjhqre.quartz.controller;

import java.util.List;

import javax.annotation.Resource;

import org.quartz.SchedulerException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.annotation.Log;
import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.common.exception.TaskException;
import com.xjhqre.common.group.Insert;
import com.xjhqre.common.group.Update;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.quartz.domain.SysJob;
import com.xjhqre.quartz.service.SysJobService;
import com.xjhqre.quartz.util.CronUtils;

import io.swagger.annotations.ApiOperation;

/**
 * 调度任务信息操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/job")
public class SysJobController extends BaseController {
    @Resource
    private SysJobService jobService;

    @ApiOperation(value = "分页查询定时任务列表")
    @PreAuthorize("@ss.hasPermission('monitor:job:list')")
    @GetMapping("/find")
    public R<IPage<SysJob>> find(SysJob sysJob, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return R.success(this.jobService.find(sysJob, pageNum, pageSize));
    }

    /**
     * 获取定时任务详细信息
     */
    @ApiOperation(value = "获取定时任务详细信息")
    @PreAuthorize("@ss.hasPermission('monitor:job:query')")
    @RequestMapping(value = "/getDetail", method = {RequestMethod.POST, RequestMethod.GET})
    public R<SysJob> getDetail(@RequestParam Long jobId) {
        return R.success(this.jobService.getDetail(jobId));
    }

    /**
     * 新增定时任务
     */
    @ApiOperation(value = "新增定时任务")
    @PreAuthorize("@ss.hasPermission('monitor:job:add')")
    @Log(title = "定时任务", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<String> add(@Validated(Insert.class) @RequestBody SysJob job) throws SchedulerException, TaskException {
        if (!CronUtils.isValid(job.getCronExpression())) {
            return R.error("新增任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        } else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI)) {
            return R.error("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi://'调用");
        } else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_LDAP)) {
            return R.error("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap://'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(),
            new String[] {Constants.HTTP, Constants.HTTPS})) {
            return R.error("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)//'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR)) {
            return R.error("新增任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        }
        this.jobService.add(job);
        return R.success("添加定时任务成功");
    }

    /**
     * 修改定时任务
     */
    @ApiOperation(value = "修改定时任务")
    @PreAuthorize("@ss.hasPermission('monitor:job:update')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public R<String> update(@Validated(Update.class) @RequestBody SysJob job) throws SchedulerException, TaskException {
        if (!CronUtils.isValid(job.getCronExpression())) {
            return R.error("修改任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        } else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI)) {
            return R.error("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi://'调用");
        } else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_LDAP)) {
            return R.error("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap://'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(),
            new String[] {Constants.HTTP, Constants.HTTPS})) {
            return R.error("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)//'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR)) {
            return R.error("修改任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        }
        this.jobService.updateJob(job);
        return R.success("修改定时任务成功");
    }

    /**
     * 定时任务状态修改
     */
    @ApiOperation(value = "定时任务状态修改")
    @PreAuthorize("@ss.hasPermission('monitor:job:update')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public R<String> changeStatus(@RequestBody SysJob job) throws SchedulerException {
        this.jobService.changeStatus(job);
        return R.success("修改定时任务状态成功");
    }

    /**
     * 删除定时任务
     */
    @ApiOperation(value = "删除定时任务")
    @PreAuthorize("@ss.hasPermission('monitor:job:delete')")
    @Log(title = "定时任务", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/delete/{jobIdList}", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> delete(@PathVariable List<Long> jobIdList) throws SchedulerException, TaskException {
        this.jobService.delete(jobIdList);
        return R.success("删除定时任务成功");
    }

    /**
     * 定时任务立即执行一次
     */
    @ApiOperation(value = "定时任务立即执行一次")
    @PreAuthorize("@ss.hasPermission('monitor:job:update')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PostMapping("/run")
    public R<String> run(@RequestBody SysJob job) throws SchedulerException {
        this.jobService.run(job);
        return R.success("执行成功");
    }

    /**
     * 校验cron表达式是否有效
     */
    @ApiOperation(value = "校验cron表达式是否有效")
    @PreAuthorize("@ss.hasPermission('monitor:job:query')")
    @RequestMapping(value = "/checkCron", method = {RequestMethod.POST, RequestMethod.GET})
    public R<Integer> checkCron(String cronString) {
        return R.success(this.jobService.checkCron(cronString) ? 1 : 0);
    }

}
