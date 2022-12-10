package com.xjhqre.system.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.system.domain.entity.RoleMenu;

/**
 * 角色与菜单关联表 数据层
 * 
 * @author xjhqre
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    /**
     * 通过角色ID删除角色和菜单关联
     * 
     * @param roleId
     *            角色ID
     * @return 结果
     */
    int deleteRoleMenuByRoleId(Long roleId);

    /**
     * 查询菜单使用数量
     *
     * @param menuId
     *            菜单ID
     * @return 结果
     */
    int checkMenuExistRole(Long menuId);
}
