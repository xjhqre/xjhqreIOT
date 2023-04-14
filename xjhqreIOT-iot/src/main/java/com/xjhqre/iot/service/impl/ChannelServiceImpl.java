package com.xjhqre.iot.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.utils.AssertUtils;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.iot.domain.entity.Channel;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.mapper.ChannelMapper;
import com.xjhqre.iot.service.ChannelService;
import com.xjhqre.iot.service.DeviceService;

/**
 * <p>
 * ChannelServiceImpl
 * </p>
 *
 * @author xjhqre
 * @since 4月 11, 2023
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, Channel> implements ChannelService {

    @Resource
    private ChannelMapper channelMapper;
    @Resource
    private DeviceService deviceService;

    @Override
    public IPage<Channel> find(Channel channel, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Channel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(channel.getDeviceName() != null && !"".equals(channel.getDeviceName()), Channel::getDeviceName,
            channel.getDeviceName()).eq(channel.getDeviceId() != null, Channel::getDeviceId, channel.getDeviceId());
        Page<Channel> channelPage = this.channelMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        for (Channel record : channelPage.getRecords()) {
            Long deviceId = record.getDeviceId();
            Device device = this.deviceService.getById(deviceId);
            record.setStatus(device.getStatus());
        }
        return channelPage;
    }

    @Override
    public void add(Channel channel) {
        Long deviceId = channel.getDeviceId();
        String streamCode = channel.getStreamCode();
        AssertUtils.notNull(deviceId, "设备id不能为空");
        AssertUtils.notEmpty(streamCode, "推流码不能为空");

        Device device = this.deviceService.getById(deviceId);
        AssertUtils.notNull(device, "没有id为" + deviceId + "的设备数据");

        channel.setCreateBy(SecurityUtils.getUsername());
        channel.setCreateTime(DateUtils.getNowDate());
        channel.setDeviceName(device.getDeviceName());
        channel.setProductId(device.getProductId());
        channel.setProductName(device.getProductName());
        this.channelMapper.insert(channel);
    }

    @Override
    public void delete(List<Long> channelIds) {
        this.channelMapper.deleteBatchIds(channelIds);
    }

    @Override
    public Channel getDetail(Long channelId) {
        return this.channelMapper.selectById(channelId);
    }
}
