package com.xjhqre.iot.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.iot.domain.entity.Channel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * ChannelMapper
 * </p>
 *
 * @author xjhqre
 * @since 4月 11, 2023
 */
@Mapper
public interface ChannelMapper extends BaseMapper<Channel> {
    List<Channel> listByDeviceId(@Param("deviceId") Long deviceId);
}
