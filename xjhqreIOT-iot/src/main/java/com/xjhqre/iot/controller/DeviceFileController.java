package com.xjhqre.iot.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.domain.R;
import com.xjhqre.iot.domain.entity.DeviceFile;
import com.xjhqre.iot.service.DeviceFileService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 设备文件管理接口
 * </p>
 *
 * @author xjhqre
 * @since 4月 18, 2023
 */
@Api(tags = "设备文件管理接口")
@RestController
@RequestMapping("/iot/deviceFile")
public class DeviceFileController {

    @Resource
    DeviceFileService deviceFileService;

    @ApiOperation(value = "分页查询设备列表")
    @PreAuthorize("@ss.hasPermission('iot:device:list')")
    @GetMapping("/find")
    public R<IPage<DeviceFile>> find(DeviceFile deviceFile, @RequestParam Integer pageNum,
        @RequestParam Integer pageSize) {
        return R.success(this.deviceFileService.find(deviceFile, pageNum, pageSize));
    }

    @ApiOperation(value = "添加文件")
    @PreAuthorize("@ss.hasPermission('iot:device:add')")
    @PostMapping("/add")
    public R<String> add(@RequestBody DeviceFile deviceFile) {
        this.deviceFileService.add(deviceFile);
        return R.success("上传文件成功");
    }

    @ApiOperation(value = "删除文件")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    @DeleteMapping("/delete/{fileIds}")
    public R<String> delete(@PathVariable List<Long> fileIds) {
        this.deviceFileService.delete(fileIds);
        return R.success("删除文件成功");
    }
}
