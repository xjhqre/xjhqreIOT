package com.xjhqre.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.domain.entity.Role;
import com.xjhqre.common.exception.ServiceException;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.system.domain.entity.RoleMenu;
import com.xjhqre.system.mapper.RoleMapper;
import com.xjhqre.system.service.RoleMenuService;
import com.xjhqre.system.service.RoleService;
import com.xjhqre.system.service.UserRoleService;

/**
 * 角色 业务层处理
 * 
 * @author xjhqre
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private UserRoleService userRoleService;
    // @Autowired
    // PermissionService permissionService;
    // @Autowired
    // TokenService tokenService;

    /**
     * 根据条件查询角色列表
     * 
     * @param role
     *            角色信息
     * @return 角色数据集合信息
     */
    @Override
    public List<Role> selectRoleList(Role role) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(role.getRoleId() != null, Role::getRoleId, role.getRoleId())
            .like(role.getRoleName() != null, Role::getRoleName, role.getRoleName())
            .eq(role.getStatus() != null, Role::getStatus, role.getStatus())
            .like(role.getRoleKey() != null, Role::getRoleKey, role.getRoleKey());
        return this.roleMapper.selectList(queryWrapper);
    }

    /**
     * 分页查询角色列表
     * 
     * @param role
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public IPage<Role> findRole(Role role, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(role.getRoleId() != null, Role::getRoleId, role.getRoleId())
            .like(role.getRoleName() != null, Role::getRoleName, role.getRoleName())
            .eq(role.getStatus() != null, Role::getStatus, role.getStatus())
            .eq(role.getStatus() != null, Role::getStatus, role.getStatus())
            .like(role.getRoleKey() != null, Role::getRoleKey, role.getRoleKey());
        return this.roleMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
    }

    /**
     * 根据用户ID查询角色
     * 
     * @param userId
     *            用户ID
     * @return 角色列表
     */
    @Override
    public List<Role> selectRolesByUserId(Long userId) {
        return this.roleMapper.selectRolesByUserId(userId);
    }

    /**
     * 通过角色ID查询角色
     * 
     * @param roleId
     *            角色ID
     * @return 角色对象信息
     */
    @Override
    public Role selectRoleById(Long roleId) {
        return this.roleMapper.selectById(roleId);
    }

    /**
     * 校验角色名称是否唯一
     * 
     * @param role
     *            角色信息
     * @return 结果
     */
    @Override
    public Boolean checkRoleNameUnique(Role role) {
        long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        Role info = this.roleMapper.checkRoleNameUnique(role.getRoleName());
        if (StringUtils.isNotNull(info) && info.getRoleId() != roleId) {
            return Constants.NOT_UNIQUE;
        }
        return Constants.UNIQUE;
    }

    /**
     * 校验角色权限是否唯一
     * 
     * @param role
     *            角色信息
     * @return 结果
     */
    @Override
    public Boolean checkRoleKeyUnique(Role role) {
        long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        Role info = this.roleMapper.checkRoleKeyUnique(role.getRoleKey());
        if (StringUtils.isNotNull(info) && info.getRoleId() != roleId) {
            return Constants.NOT_UNIQUE;
        }
        return Constants.UNIQUE;
    }

    /**
     * 校验角色是否允许操作
     * 
     * @param roleId
     *            角色信息
     */
    @Override
    public void checkRoleAllowed(Long roleId) {
        if (StringUtils.isNotNull(roleId) && SecurityUtils.isSuperAdmin(roleId)) {
            throw new ServiceException("不允许操作超级管理员角色");
        }
    }

    /**
     * 新增保存角色信息
     * 
     * @param role
     *            角色信息
     * @return 结果
     */
    @Override
    public void insertRole(Role role) {
        // 新增角色信息
        role.setCreateBy(SecurityUtils.getUsername());
        role.setCreateTime(DateUtils.getNowDate());
        this.roleMapper.insert(role);
        // 新增用户与角色关联
        List<RoleMenu> list = new ArrayList<>();
        for (Long menuId : role.getMenuIds()) {
            RoleMenu rm = new RoleMenu();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (list.size() > 0) {
            this.roleMenuService.saveBatch(list);
        }
    }

    /**
     * 修改保存角色信息
     * 
     * @param role
     *            角色信息
     * @return 结果
     */
    @Override
    public void updateRole(Role role) {
        // 修改角色信息
        role.setUpdateBy(SecurityUtils.getUsername());
        role.setUpdateTime(DateUtils.getNowDate());
        this.roleMapper.updateById(role);
        // 先删除再关联
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, role.getRoleId());
        this.roleMenuService.remove(wrapper);
        // 新增角色与权限关联
        List<RoleMenu> list = new ArrayList<>();
        for (Long menuId : role.getMenuIds()) {
            RoleMenu rm = new RoleMenu();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (list.size() > 0) {
            this.roleMenuService.saveBatch(list);
        }

        //// 更新缓存用户权限
        // LoginUser loginUser = SecurityUtils.getLoginUser();
        // if (StringUtils.isNotNull(loginUser.getUser()) && !loginUser.getUser().isSuperAdmin()) {
        // loginUser.setPermissions(this.permissionService.getMenuPermission(loginUser.getUser()));
        // this.tokenService.setLoginUser(loginUser);
        // }
    }

    /**
     * 修改角色状态
     * 
     * @param role
     *            角色信息
     * @return 结果
     */
    @Override
    public int updateRoleStatus(Role role) {
        return this.roleMapper.updateById(role);
    }

    /**
     * 通过角色ID删除角色
     * 
     * @param roleId
     *            角色ID
     * @return 结果
     */
    @Override
    public void deleteRoleById(Long roleId) {
        if (this.userRoleService.countUserRoleByRoleId(roleId) > 0) {
            throw new ServiceException("该角色已分配,不能删除");
        }
        this.checkRoleAllowed(roleId);
        // 删除角色与菜单关联
        this.roleMenuService.deleteRoleMenuByRoleId(roleId);
        // 设置删除字段
        this.roleMapper.deleteById(roleId);
    }

    /**
     * 批量删除角色信息
     * 
     * @param roleIds
     *            需要删除的角色ID
     * @return 结果
     */
    @Override
    public void deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            this.checkRoleAllowed(roleId);
            Role role = this.selectRoleById(roleId);
            if (this.userRoleService.countUserRoleByRoleId(roleId) > 0) {
                throw new ServiceException(String.format("%1$s已分配,不能删除", role.getRoleName()));
            }
        }
        // 删除角色与菜单关联
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RoleMenu::getRoleId, Arrays.asList(roleIds));
        this.roleMenuService.remove(wrapper);
        // 删除角色
        this.roleMapper.deleteBatchIds(Arrays.asList(roleIds));
    }
}
