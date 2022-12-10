package com.xjhqre.system.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.common.domain.entity.Role;

/**
 * 角色业务层
 * 
 * @author xjhqre
 */
public interface RoleService extends IService<Role> {
    /**
     * 根据条件分页查询角色数据
     * 
     * @param role
     *            角色信息
     * @return 角色数据集合信息
     */
    List<Role> selectRoleList(Role role);

    /**
     * 根据条件分页查询角色数据
     * 
     * @param role
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<Role> findRole(Role role, Integer pageNum, Integer pageSize);

    /**
     * 根据用户ID查询角色列表
     * 
     * @param userId
     *            用户ID
     * @return 角色列表
     */
    List<Role> selectRolesByUserId(Long userId);

    /**
     * 通过角色ID查询角色
     * 
     * @param roleId
     *            角色ID
     * @return 角色对象信息
     */
    Role selectRoleById(Long roleId);

    /**
     * 校验角色名称是否唯一
     * 
     * @param role
     *            角色信息
     * @return 结果
     */
    Boolean checkRoleNameUnique(Role role);

    /**
     * 校验角色权限是否唯一
     * 
     * @param role
     *            角色信息
     * @return 结果
     */
    Boolean checkRoleKeyUnique(Role role);

    /**
     * 校验角色是否允许操作
     * 
     * @param roleId
     *            角色信息
     */
    void checkRoleAllowed(Long roleId);

    /**
     * 新增保存角色信息
     * 
     * @param role
     *            角色信息
     * @return 结果
     */
    void insertRole(Role role);

    /**
     * 修改保存角色信息
     * 
     * @param role
     *            角色信息
     * @return 结果
     */
    void updateRole(Role role);

    /**
     * 修改角色状态
     * 
     * @param role
     *            角色信息
     * @return 结果
     */
    int updateRoleStatus(Role role);

    /**
     * 通过角色ID删除角色
     * 
     * @param roleId
     *            角色ID
     * @return 结果
     */
    void deleteRoleById(Long roleId);

    /**
     * 批量删除角色信息
     * 
     * @param roleIds
     *            需要删除的角色ID
     * @return 结果
     */
    void deleteRoleByIds(Long[] roleIds);

}
