package com.xjhqre.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.common.domain.entity.Role;

/**
 * 角色表 数据层
 * 
 * @author xjhqre
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据用户ID查询角色
     * 
     * @param userId
     *            用户ID
     * @return 角色列表
     */
    List<Role> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 校验角色名称是否唯一
     * 
     * @param roleName
     *            角色名称
     * @return 角色信息
     */
    Role checkRoleNameUnique(String roleName);

    /**
     * 校验角色权限是否唯一
     * 
     * @param roleKey
     *            角色权限
     * @return 角色信息
     */
    Role checkRoleKeyUnique(String roleKey);
}
