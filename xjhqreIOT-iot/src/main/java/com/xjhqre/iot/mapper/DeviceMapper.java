package com.xjhqre.iot.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.iot.domain.entity.Device;

/**
 * 设备Mapper接口
 * 
 * @author kerwincui
 * @date 2021-12-16
 */
@Repository
public interface DeviceMapper extends BaseMapper<Device> {

    /**
     * 查询分组可添加设备分页列表
     *
     * @param device
     *            设备
     * @return 设备集合
     */
    IPage<Device> selectDeviceListByGroup(@Param("device") Device device);

    /**
     * 根据设备IDS删除设备分组
     *
     */
    int deleteDeviceGroupByDeviceId(@Param("deviceId") Long deviceId);

}
