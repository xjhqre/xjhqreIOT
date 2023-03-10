package com.xjhqre.iot.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.annotation.Log;
import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.iot.domain.entity.AlertLog;
import com.xjhqre.iot.service.AlertLogService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 设备告警日志接口
 *
 * @author xjhqre
 * @since 2023-01-6
 */
@Api(tags = "设备告警日志接口")
@RestController
@RequestMapping("/iot/alertLog")
public class AlertLogController extends BaseController {
    @Resource
    private AlertLogService alertLogService;

    @ApiOperation(value = "分页查询设备告警日志列表")
    @PreAuthorize("@ss.hasPermission('iot:alert:list')")
    @GetMapping("/find")
    public R<IPage<AlertLog>> find(AlertLog alertLog, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return R.success(this.alertLogService.find(alertLog, pageNum, pageSize));
    }

    /**
     * 获取设备告警详细信息
     */
    @PreAuthorize("@ss.hasPermission('iot:alert:query')")
    @RequestMapping(value = "/getDetail", method = {RequestMethod.POST, RequestMethod.GET})
    public R<AlertLog> getDetail(@RequestParam Long alertLogId) {
        return R.success(this.alertLogService.getDetail(alertLogId));
    }

    /**
     * 新增设备告警
     */
    @PreAuthorize("@ss.hasPermission('iot:alert:add')")
    @Log(title = "设备告警", businessType = BusinessType.INSERT)
    @PostMapping
    public R<String> add(AlertLog alertLog) {
        this.alertLogService.add(alertLog);
        return R.success("新增设备告警成功");
    }

    /**
     * 修改设备告警
     */
    @PreAuthorize("@ss.hasPermission('iot:alert:update')")
    @Log(title = "设备告警", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> update(AlertLog alertLog) {
        this.alertLogService.update(alertLog);
        return R.success("修改设备告警成功");
    }

    /**
     * 删除设备告警
     */
    @PreAuthorize("@ss.hasPermission('iot:alert:delete')")
    @Log(title = "设备告警", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/delete/{alertLogIds}", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> delete(@PathVariable List<Long> alertLogIds) {
        this.alertLogService.delete(alertLogIds);
        return R.success("删除设备告警成功");
    }
}
