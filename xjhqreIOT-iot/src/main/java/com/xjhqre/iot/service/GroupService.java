package com.xjhqre.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.iot.domain.entity.Group;

/**
 * 分组Service接口
 * 
 * @author xjhqre
 * @since 2023-1-5
 */
public interface GroupService {

    /**
     * 分页查询分组列表
     */
    IPage<Group> find(Group group, Integer pageNum, Integer pageSize);

    /**
     * 查询分组详情
     */
    Group getDetail(Long groupId);

    /**
     * 获取分组下所有关联的设备ID数组
     */
    List<Long> getDeviceIds(Long groupId);

    /**
     * 新增分组
     */
    void add(Group group);

    /**
     * 更新分组下的关联设备
     */
    void updateDeviceGroups(Long groupId, List<Long> deviceIdList);

    /**
     * 修改分组
     *
     */
    void update(Group group);

    /**
     * 批量删除分组
     *
     */
    void delete(List<Long> groupIds);
}
