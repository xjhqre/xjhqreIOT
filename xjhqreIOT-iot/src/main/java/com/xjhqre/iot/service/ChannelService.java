package com.xjhqre.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.iot.domain.entity.Channel;

/**
 * <p>
 * ChannelService
 * </p>
 *
 * @author xjhqre
 * @since 4月 11, 2023
 */
public interface ChannelService extends IService<Channel> {
    IPage<Channel> find(Channel channel, Integer pageNum, Integer pageSize);

    void add(Channel channel);

    void delete(List<Long> channelIds);

    Channel getDetail(Long channelId);

    List<Channel> listByDeviceId(Long deviceId);
}
