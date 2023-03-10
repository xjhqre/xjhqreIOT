package com.xjhqre.iot.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.xjhqre.common.domain.R;
import com.xjhqre.iot.domain.entity.DeviceProp;
import com.xjhqre.iot.service.DevicePropService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * DevicePropController
 * </p>
 *
 * @author xjhqre
 * @since 2月 09, 2023
 */
@Api(tags = "物模型值接口")
@RestController
@RequestMapping("/iot/deviceProp")
public class DevicePropController {

    @Resource
    DevicePropService devicePropService;

    @PreAuthorize("@ss.hasPermission('iot:device:list')")
    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("查询设备物模型值列表")
    public R<List<DeviceProp>> list(DeviceProp deviceProp) {
        return R.success(this.devicePropService.list(deviceProp));
    }

    /**
     * 添加产品物模型
     */
    @PreAuthorize("@ss.hasPermission('iot:model:add')")
    @PostMapping("/add")
    @ApiOperation("添加物模型参数")
    public R<String> add(Long productId, Long deviceId, Long modelId, String value) {
        this.devicePropService.add(productId, deviceId, modelId, value);
        return R.success("添加物模型值成功");
    }
}
