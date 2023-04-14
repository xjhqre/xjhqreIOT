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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.annotation.Log;
import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.iot.domain.entity.Channel;
import com.xjhqre.iot.service.ChannelService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * ChannelController
 * </p>
 *
 * @author xjhqre
 * @since 4月 11, 2023
 */
@Api(tags = "视频通道接口")
@RestController
@RequestMapping("/iot/channel")
public class ChannelController extends BaseController {

    @Resource
    private ChannelService channelService;

    @ApiOperation(value = "分页查询视频通道列表")
    @PreAuthorize("@ss.hasPermission('iot:channel:list')")
    @GetMapping("/find")
    public R<IPage<Channel>> find(Channel channel, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return R.success(this.channelService.find(channel, pageNum, pageSize));
    }

    @ApiOperation(value = "查询通道详情")
    @PreAuthorize("@ss.hasPermission('iot:channel:query')")
    @RequestMapping(value = "/getDetail", method = {RequestMethod.POST, RequestMethod.GET})
    public R<Channel> getDetail(@RequestParam Long channelId) {
        return R.success(this.channelService.getDetail(channelId));
    }

    @ApiOperation(value = "新增视频通道")
    @PreAuthorize("@ss.hasPermission('iot:channel:add')")
    @Log(title = "视频通道", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<String> add(@RequestBody Channel channel) {
        this.channelService.add(channel);
        return R.success("新增设备告警成功");
    }

    @ApiOperation(value = "修改视频通道")
    @PreAuthorize("@ss.hasPermission('iot:channel:update')")
    @Log(title = "视频通道", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/update")
    public R<String> update(@RequestBody Channel channel) {
        channel.setUpdateBy(SecurityUtils.getUsername());
        channel.setUpdateTime(DateUtils.getNowDate());
        this.channelService.updateById(channel);
        return R.success("修改设备告警成功");
    }

    @ApiOperation(value = "删除视频通道")
    @PreAuthorize("@ss.hasPermission('iot:channel:delete')")
    @Log(title = "视频通道", businessType = BusinessType.DELETE)
    @DeleteMapping(value = "/delete/{channelIds}")
    public R<String> delete(@PathVariable List<Long> channelIds) {
        this.channelService.delete(channelIds);
        return R.success("删除设备告警成功");
    }
}
