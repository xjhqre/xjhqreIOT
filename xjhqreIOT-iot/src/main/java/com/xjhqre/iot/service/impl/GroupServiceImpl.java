package com.xjhqre.iot.service.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjhqre.common.domain.model.LoginUser;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.iot.domain.dto.UpdateDeviceGroupsDTO;
import com.xjhqre.iot.domain.entity.Group;
import com.xjhqre.iot.mapper.GroupMapper;
import com.xjhqre.iot.service.GroupService;

/**
 * 设备分组Service业务层处理
 * 
 * @author xjhqre
 * @date 2021-12-16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GroupServiceImpl implements GroupService {
    @Resource
    private GroupMapper groupMapper;

    @Override
    public IPage<Group> find(Group group, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(group.getGroupId() != null, Group::getGroupId, group.getGroupId())
            .like(group.getGroupName() != null && !"".equals(group.getGroupName()), Group::getGroupName,
                group.getGroupName())
            .eq(group.getUserId() != null, Group::getUserId, group.getUserId())
            .like(group.getUserName() != null && !"".equals(group.getUserName()), Group::getUserName,
                group.getUserName());
        return this.groupMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 查询设备分组
     * 
     * @param groupId
     *            设备分组主键
     * @return 设备分组
     */
    @Override
    public Group getDetail(Long groupId) {
        return this.groupMapper.selectById(groupId);
    }

    /**
     * 获取分组下所有关联的设备ID数组
     */
    @Override
    public List<Long> getDeviceIds(Long groupId) {
        return this.groupMapper.getDeviceIds(groupId);
    }

    /**
     * 新增分组
     */
    @Override
    public void add(Group group) {
        LoginUser user = SecurityUtils.getLoginUser();
        group.setUserId(user.getUserId());
        group.setUserName(user.getUsername());
        group.setCreateTime(DateUtils.getNowDate());
        group.setCreateBy(user.getUsername());
        this.groupMapper.insert(group);
    }

    /**
     * 修改分组
     *
     */
    @Override
    public void update(Group group) {
        group.setUpdateTime(DateUtils.getNowDate());
        group.setUpdateBy(SecurityUtils.getUsername());
        this.groupMapper.updateById(group);
    }

    @Override
    public void updateDeviceGroups(UpdateDeviceGroupsDTO dto) {
        // 删除分组下的所有关联设备
        this.groupMapper.deleteDeviceGroupByGroupIds(Collections.singletonList(dto.getGroupId()));
        // 分组下添加关联设备
        this.groupMapper.insertDeviceGroups(dto.getGroupId(), dto.getDeviceIdList());
    }

    /**
     * 批量删除分组和设备分组
     */
    @Override
    public void delete(List<Long> groupIds) {
        // 删除设备分组
        this.groupMapper.deleteDeviceGroupByGroupIds(groupIds);
        // 删除分组
        this.groupMapper.deleteBatchIds(groupIds);
    }

}
