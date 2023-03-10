package com.xjhqre.iot.controller;

import java.util.List;

import javax.annotation.Resource;

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
import com.xjhqre.common.domain.R;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.iot.domain.entity.Firmware;
import com.xjhqre.iot.service.FirmwareService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 产品固件操作接口
 * </p>
 *
 * @author xjhqre
 * @since 12月 19, 2022
 */
@Api(tags = "产品固件操作接口")
@RestController
@RequestMapping("/iot/firmware")
public class FirmwareController extends BaseController {

    @Resource
    private FirmwareService firmwareService;

    @ApiOperation(value = "产品固件分页列表")
    @PreAuthorize("@ss.hasPermission('iot:firmware:list')")
    @GetMapping("/find")
    public R<IPage<Firmware>> find(Firmware firmware, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return R.success(this.firmwareService.find(firmware, pageNum, pageSize));
    }

    @ApiOperation("产品固件列表")
    @PreAuthorize("@ss.hasPermission('iot:firmware:list')")
    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    public R<List<Firmware>> list(Firmware firmware) {
        return R.success(this.firmwareService.list(firmware));
    }

    /**
     * 获取产品固件详细信息
     */
    @ApiOperation("获取固件详情")
    @PreAuthorize("@ss.hasPermission('iot:firmware:query')")
    @RequestMapping(value = "/getDetail", method = {RequestMethod.POST, RequestMethod.GET})
    public R<Firmware> getDetail(@RequestParam Long firmwareId) {
        return R.success(this.firmwareService.getDetail(firmwareId));
    }

    /**
     * 获取设备最新固件
     */
    @ApiOperation("获取设备最新固件")
    @PreAuthorize("@ss.hasPermission('iot:firmware:query')")
    @RequestMapping(value = "/getLatest", method = {RequestMethod.POST, RequestMethod.GET})
    public R<Firmware> getLatest(@RequestParam Long deviceId) {
        return R.success(this.firmwareService.getLatest(deviceId));
    }

    /**
     * 新增产品固件
     */
    @ApiOperation("添加产品固件")
    @PreAuthorize("@ss.hasPermission('iot:firmware:add')")
    @Log(title = "产品固件", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<String> add(@RequestBody @Validated Firmware firmware) {
        this.firmwareService.add(firmware);
        return R.success("添加产品固件成功");
    }

    /**
     * 修改产品固件
     */
    @ApiOperation("修改产品固件")
    @PreAuthorize("@ss.hasPermission('iot:firmware:update')")
    @Log(title = "产品固件", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public R<String> update(@RequestBody Firmware firmware) {
        this.firmwareService.update(firmware);
        return R.success("修改产品固件成功");
    }

    /**
     * 删除产品固件
     */
    @ApiOperation("批量删除产品固件")
    @PreAuthorize("@ss.hasPermission('iot:firmware:delete')")
    @Log(title = "产品固件", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/delete/{firmwareIds}", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> delete(@PathVariable Long[] firmwareIds) {
        this.firmwareService.delete(firmwareIds);
        return R.success("删除产品固件成功");
    }
}
