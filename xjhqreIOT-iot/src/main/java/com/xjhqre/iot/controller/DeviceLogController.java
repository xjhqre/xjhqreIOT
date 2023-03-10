package com.xjhqre.iot.controller;

import java.util.List;

import javax.annotation.Resource;

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
import com.xjhqre.iot.domain.entity.DeviceLog;
import com.xjhqre.iot.service.DeviceLogService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 设备日志接口
 *
 * @author xjhqre
 * @since 2023-01-6
 */
@Api(tags = "设备日志接口")
@RestController
@RequestMapping("/iot/deviceLog")
public class DeviceLogController extends BaseController {
    @Resource
    private DeviceLogService deviceLogService;

    @ApiOperation(value = "分页查询设备日志列表")
    @PreAuthorize("@ss.hasPermission('iot:device:list')")
    @GetMapping("/find")
    public R<IPage<DeviceLog>> find(DeviceLog deviceLog, @RequestParam Integer pageNum,
        @RequestParam Integer pageSize) {
        return R.success(this.deviceLogService.find(deviceLog, pageNum, pageSize));
    }

    /**
     * 获取设备日志详情
     */
    @PreAuthorize("@ss.hasPermission('iot:device:query')")
    @RequestMapping(value = "/getDetail", method = {RequestMethod.POST, RequestMethod.GET})
    public R<DeviceLog> getDetail(@RequestParam Long logId) {
        return R.success(this.deviceLogService.getDetail(logId));
    }

    /**
     * 新增设备日志
     */
    @PreAuthorize("@ss.hasPermission('iot:device:add')")
    @Log(title = "设备日志", businessType = BusinessType.INSERT)
    @RequestMapping(value = "/add", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> add(DeviceLog deviceLog) {
        this.deviceLogService.add(deviceLog);
        return R.success("新增设备日志成功");
    }

    /**
     * 修改设备日志
     */
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    @Log(title = "设备日志", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "update", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> update(DeviceLog deviceLog) {
        this.deviceLogService.update(deviceLog);
        return R.success("修改设备日志成功");
    }

    /**
     * 删除设备日志
     */
    @PreAuthorize("@ss.hasPermission('iot:device:delete')")
    @Log(title = "设备日志", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/delete/{logIds}", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> delete(@PathVariable List<Long> logIds) {
        this.deviceLogService.delete(logIds);
        return R.success("删除设备日志");
    }
}
