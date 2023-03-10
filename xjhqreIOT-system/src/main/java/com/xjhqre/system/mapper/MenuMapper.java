package com.xjhqre.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.common.domain.entity.Menu;

/**
 * 菜单表 数据层
 *
 * @author xjhqre
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 查询角色权限列表
     *
     */
    List<Menu> selectMenuListByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据角色ID查询权限
     * 
     * @param roleId
     *            角色ID
     * @return 权限列表
     */
    List<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId
     *            用户ID
     * @return 权限列表
     */
    List<String> selectMenuPermsByUserId(Long userId);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId
     *            菜单ID
     * @return 结果
     */
    int hasChildByMenuId(Long menuId);

    /**
     * 根据角色id查询权限
     * 
     * @param roleId
     * @return
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    List<Menu> selectMenuTreeByUserId(@Param("userId") Long userId);
}
