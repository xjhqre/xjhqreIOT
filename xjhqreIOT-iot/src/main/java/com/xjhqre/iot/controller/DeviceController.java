package com.xjhqre.iot.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.annotation.Log;
import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.common.group.Insert;
import com.xjhqre.common.group.Update;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.ThingsModel;
import com.xjhqre.iot.domain.model.DeviceStatistic;
import com.xjhqre.iot.domain.vo.DeviceVO;
import com.xjhqre.iot.mqtt.EmqxService;
import com.xjhqre.iot.service.DeviceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 设备操作接口
 * </p>
 *
 * @author xjhqre
 * @since 12月 19, 2022
 */
@Api(tags = "设备操作接口")
@RestController
@RequestMapping("/iot/device")
public class DeviceController extends BaseController {
    @Resource
    private DeviceService deviceService;
    @Resource
    private EmqxService emqxService;

    @ApiOperation(value = "分页查询设备列表")
    @PreAuthorize("@ss.hasPermission('iot:device:list')")
    @GetMapping("/find")
    public R<IPage<DeviceVO>> find(Device device, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return R.success(this.deviceService.find(device, pageNum, pageSize));
    }

    /**
     * 查询分组可添加设备，设备分组添加设备时用
     */
    @ApiOperation(value = "查询分组可添加设备分页列表")
    @PreAuthorize("@ss.hasPermission('iot:device:list')")
    @GetMapping("/findByGroup}")
    public R<IPage<Device>> findByGroup(Device device, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return R.success(this.deviceService.findByGroup(device, pageNum, pageSize));
    }

    /**
     * 查询所有设备
     */
    @ApiOperation(value = "查询所有设备")

    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "正整数，表示查询第几页", required = true, dataType = "int", example = "1"),
        @ApiImplicitParam(name = "pageSize", value = "正整数，表示每页几条记录", required = true, dataType = "int",
            example = "10")})
    @PreAuthorize("@ss.hasPermission('iot:device:list')")
    @GetMapping("/all/{pageNum}/{pageSize}")
    public R<IPage<Device>> all(Device device, @PathVariable("pageNum") Integer pageNum,
        @PathVariable("pageSize") Integer pageSize) {
        return R.success(this.deviceService.selectAllDevice(device, pageNum, pageSize));
    }

    /**
     * 查询设备列表
     */
    @ApiOperation("查询设备列表")
    @PreAuthorize("@ss.hasPermission('iot:device:list')")
    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    public R<List<Device>> list(Device device) {
        return R.success(this.deviceService.list(device));
    }

    /**
     * 获取设备详细信息
     */
    @PreAuthorize("@ss.hasPermission('iot:device:query')")
    @RequestMapping(value = "/getDetail", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("获取设备详情")
    public R<DeviceVO> getDetail(@RequestParam Long deviceId) {
        return R.success(this.deviceService.getDetail(deviceId));
    }

    /**
     * 获取设备属性值
     */
    @PreAuthorize("@ss.hasPermission('iot:device:query')")
    @RequestMapping(value = "/listPropertiesWithLastValue", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("获取设备属性值")
    public R<List<ThingsModel>> listPropertiesWithLastValue(@RequestParam Long deviceId,
        @RequestParam(required = false) String modelName) {
        return R.success(this.deviceService.listPropertiesWithLastValue(deviceId, modelName));
    }

    /**
     * 设备数据同步
     */
    @PreAuthorize("@ss.hasPermission('iot:device:query')")
    @GetMapping(value = "/synchronization/{deviceNumber}")
    @ApiOperation("设备数据同步")
    public R<Device> deviceSynchronization(@PathVariable("deviceNumber") String deviceNumber) {
        return R.success(this.emqxService.deviceSynchronization(deviceNumber));
    }

    /**
     * 根据设备编号详细信息
     */
    @PreAuthorize("@ss.hasPermission('iot:device:query')")
    @GetMapping(value = "/getDetailByDeviceNumber/{deviceNumber}")
    @ApiOperation("根据设备编号获取设备详情")
    public R<Device> getDetailByDeviceNumber(@PathVariable("deviceNumber") String deviceNumber) {
        return R.success(this.deviceService.getByDeviceNumber(deviceNumber));
    }

    /**
     * 获取设备统计信息
     */
    @PreAuthorize("@ss.hasPermission('iot:device:query')")
    @GetMapping(value = "/statistic")
    @ApiOperation("获取设备统计信息")
    public R<DeviceStatistic> getStatisticInfo() {
        return R.success(this.deviceService.getStatisticInfo());
    }

    @PreAuthorize("@ss.hasPermission('iot:device:list')")
    @GetMapping(value = "/getDeviceCount")
    @ApiOperation("获取各种状态设备数量")
    public R<Map<String, Integer>> getDeviceCount() {
        return R.success(this.deviceService.getDeviceCount());
    }

    /**
     * 新增设备
     */
    @PreAuthorize("@ss.hasPermission('iot:device:add')")
    @Log(title = "添加设备", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ApiOperation("添加设备")

    public R<String> add(@Validated(Insert.class) @RequestBody Device device) {
        this.deviceService.add(device);
        return R.success("添加设备成功");
    }

    /**
     * 修改设备
     */
    @ApiOperation("修改设备")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    @Log(title = "修改设备", businessType = BusinessType.UPDATE)
    @PutMapping("/update")
    public R<String> update(@Validated(Update.class) @RequestBody Device device) {
        this.deviceService.update(device);
        return R.success("修改设备成功");
    }

    /**
     * 启用/禁用设备
     */
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    @Log(title = "设备状态", businessType = BusinessType.UPDATE)
    @PutMapping("/updateDeviceStatus")
    @ApiOperation("启用/禁用设备")
    public R<String> updateDeviceStatus(@RequestParam String deviceId, @RequestParam Integer status) {
        this.deviceService.updateDeviceStatus(deviceId, status);
        return R.success("启用/禁用设备成功");
    }

    /**
     * 重置设备状态
     */
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    @Log(title = "重置设备状态", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/reset", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("重置设备状态")
    public R<String> resetDeviceStatus(@RequestParam String deviceNumber) {
        this.deviceService.resetDeviceStatus(deviceNumber);
        return R.success("重置设备状态成功");
    }

    /**
     * 删除设备
     */
    @PreAuthorize("@ss.hasPermission('iot:device:delete')")
    @Log(title = "删除设备", businessType = BusinessType.DELETE)
    @ApiOperation("批量删除设备")
    @DeleteMapping("/delete/{deviceIds}")
    public R<String> delete(@PathVariable List<Long> deviceIds) {
        this.deviceService.delete(deviceIds);
        return R.success("删除设备成功");
    }

    /**
     * 生成设备编号
     */
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    @RequestMapping(value = "/generator", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("生成设备编号")
    public R<String> generatorDeviceNum() {
        return R.success(this.deviceService.generationDeviceNum());
    }
}
