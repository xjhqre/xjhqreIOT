package com.xjhqre.system.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.system.domain.entity.UserRole;
import com.xjhqre.system.mapper.UserRoleMapper;
import com.xjhqre.system.service.UserRoleService;

/**
 * 角色 业务层处理
 * 
 * @author xjhqre
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Resource
    UserRoleMapper userRoleMapper;

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId
     *            角色ID
     * @return 结果
     */
    @Override
    public int countUserRoleByRoleId(Long roleId) {
        return this.userRoleMapper.countUserRoleByRoleId(roleId);
    }
}
