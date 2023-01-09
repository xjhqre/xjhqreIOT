package com.xjhqre.quartz.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.xjhqre.quartz.domain.SysJobLog;
import com.xjhqre.quartz.service.SysJobLogService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 调度日志操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/jobLog")
public class SysJobLogController extends BaseController {
    @Resource
    private SysJobLogService jobLogService;

    @ApiOperation(value = "分页查询定时任务调度日志列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "正整数，表示查询第几页", required = true, dataType = "int", example = "1"),
        @ApiImplicitParam(name = "pageSize", value = "正整数，表示每页几条记录", required = true, dataType = "int",
            example = "10")})
    @PreAuthorize("@ss.hasPermission('monitor:job:list')")
    @GetMapping("find/{pageNum}/{pageSize}")
    public R<IPage<SysJobLog>> find(SysJobLog sysJobLog, @PathVariable("pageNum") Integer pageNum,
        @PathVariable("pageSize") Integer pageSize) {
        return R.success(this.jobLogService.find(sysJobLog, pageNum, pageSize));
    }

    /**
     * 获取定时任务日志详情
     */
    @ApiOperation(value = "分页查询定时任务调度日志列表")
    @PreAuthorize("@ss.hasPermission('monitor:job:query')")
    @RequestMapping(value = "/getDetail", method = {RequestMethod.POST, RequestMethod.GET})
    public R<SysJobLog> getDetail(@RequestParam Long jobLogId) {
        return R.success(this.jobLogService.getDetail(jobLogId));
    }

    /**
     * 删除定时任务日志
     */
    @PreAuthorize("@ss.hasPermission('monitor:job:delete')")
    @Log(title = "定时任务日志", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> delete(@RequestParam List<Long> jobLogIdList) {
        this.jobLogService.delete(jobLogIdList);
        return R.success("删除定时任务调度日志成功");
    }

    /**
     * 清空定时任务日志
     */
    @PreAuthorize("@ss.hasPermission('monitor:job:remove')")
    @Log(title = "定时任务日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public R<String> clean() {
        this.jobLogService.cleanJobLog();
        return R.success("清空定时任务调度日志成功");
    }
}
