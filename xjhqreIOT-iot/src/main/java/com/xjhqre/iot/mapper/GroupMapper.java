package com.xjhqre.iot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.iot.domain.entity.Group;

/**
 * 设备分组Mapper接口
 * 
 * @author kerwincui
 * @date 2021-12-16
 */
@Mapper
public interface GroupMapper extends BaseMapper<Group> {

    /**
     * 获取分组下所有关联的设备ID数组
     */
    List<Long> getDeviceIds(Long groupId);

    /**
     * 分组下批量增加设备分组
     */
    void insertDeviceGroups(@Param("deviceIdList") List<Long> deviceIdList);

    /**
     * 批量删除分组中的设备
     */
    int deleteDeviceGroupByGroupIds(@Param("groupIds") List<Long> groupIds);

}
