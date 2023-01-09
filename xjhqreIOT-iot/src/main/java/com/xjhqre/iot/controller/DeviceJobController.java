package com.xjhqre.iot.controller;

import java.util.List;

import javax.annotation.Resource;

import org.quartz.SchedulerException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.annotation.Log;
import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.common.exception.TaskException;
import com.xjhqre.iot.domain.entity.DeviceJob;
import com.xjhqre.iot.service.DeviceJobService;
import com.xjhqre.quartz.util.CronUtils;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 调度任务信息操作处理
 *
 * @author xjhqre
 * @since 2023-1-6
 */
@RestController
@RequestMapping("/iot/deviceJob")
public class DeviceJobController extends BaseController {

    @Resource
    private DeviceJobService deviceJobService;

    @ApiOperation(value = "分页查询设备定时任务")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "正整数，表示查询第几页", required = true, dataType = "int", example = "1"),
        @ApiImplicitParam(name = "pageSize", value = "正整数，表示每页几条记录", required = true, dataType = "int",
            example = "10")})
    @PreAuthorize("@ss.hasPermission('iot:device:list')")
    @GetMapping("find/{pageNum}/{pageSize}")
    public R<IPage<DeviceJob>> find(DeviceJob deviceJob, @PathVariable("pageNum") Integer pageNum,
        @PathVariable("pageSize") Integer pageSize) {
        return R.success(this.deviceJobService.find(deviceJob, pageNum, pageSize));
    }

    /**
     * 获取设备定时任务详情
     */
    @ApiOperation(value = "获取设备定时任务详情")
    @PreAuthorize("@ss.hasPermission('iot:device:query')")
    @RequestMapping(value = "/getDetail", method = {RequestMethod.POST, RequestMethod.GET})
    public R<DeviceJob> getDetail(@RequestParam Long jobId) {
        return R.success(this.deviceJobService.getDetail(jobId));
    }

    /**
     * 新增设备定时任务
     */
    @ApiOperation(value = "新增设备定时任务")
    @PreAuthorize("@ss.hasPermission('iot:device:add')")
    @Log(title = "设备定时任务", businessType = BusinessType.INSERT)
    @RequestMapping(value = "/add", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> add(DeviceJob job) throws SchedulerException, TaskException {
        if (!CronUtils.isValid(job.getCronExpression())) {
            return R.error("新增任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        }
        this.deviceJobService.add(job);
        return R.success("添加设备定时任务成功");
    }

    /**
     * 修改设备定时任务
     */
    @ApiOperation(value = "修改设备定时任务")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    @Log(title = "设备定时任务", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> update(DeviceJob job) throws SchedulerException, TaskException {
        if (!CronUtils.isValid(job.getCronExpression())) {
            return R.error("修改任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        }
        this.deviceJobService.update(job);
        return R.success("修改设备定时任务成功");
    }

    /**
     * 设备定时任务状态修改
     */
    @ApiOperation(value = "设备定时任务状态修改")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    @Log(title = "设备定时任务", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/changeStatus", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> changeStatus(DeviceJob job) throws SchedulerException {
        this.deviceJobService.changeStatus(job);
        return R.success("定时任务状态修改成功");
    }

    /**
     * 设备定时任务立即执行一次
     */
    @ApiOperation(value = "设备定时任务立即执行一次")
    @PreAuthorize("@ss.hasPermission('iot:device:edit')")
    @Log(title = "设备定时任务", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/run", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> run(DeviceJob job) {
        this.deviceJobService.run(job);
        return R.success("执行成功");
    }

    /**
     * 删除设备定时任务
     */
    @ApiOperation(value = "删除设备定时任务")
    @PreAuthorize("@ss.hasPermission('iot:device:delete')")
    @Log(title = "设备定时任务", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> delete(@RequestParam List<Long> jobIdList) throws SchedulerException, TaskException {
        this.deviceJobService.delete(jobIdList);
        return R.success("删除设备定时任务成功");
    }

}
