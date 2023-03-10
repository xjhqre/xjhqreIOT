package com.xjhqre.system.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.system.domain.entity.RoleMenu;
import com.xjhqre.system.mapper.RoleMenuMapper;
import com.xjhqre.system.service.RoleMenuService;

/**
 * 角色 业务层处理
 * 
 * @author xjhqre
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

    @Resource
    RoleMenuMapper roleMenuMapper;

    @Override
    public void deleteRoleMenuByRoleId(Long roleId) {
        this.roleMenuMapper.deleteRoleMenuByRoleId(roleId);
    }
}
