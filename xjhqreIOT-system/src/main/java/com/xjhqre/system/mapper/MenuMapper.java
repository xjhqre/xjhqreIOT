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
     * 根据用户查询系统菜单列表
     *
     * @param menu
     *            菜单信息
     * @return 菜单列表
     */
    List<Menu> selectMenuListByUserId(Menu menu);

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
    List<Menu> selectMenuListByRoleId(@Param("roleId") Long roleId);
}
