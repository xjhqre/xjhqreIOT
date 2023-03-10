package com.xjhqre.system.service;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.domain.entity.Menu;
import com.xjhqre.common.domain.model.TreeSelect;
import com.xjhqre.system.domain.vo.RouterVo;

/**
 * 菜单 业务层
 *
 * @author ruoyi
 */
public interface MenuService {

    /**
     * 分页查询
     *
     */
    IPage<Menu> find(Menu menu, Integer pageNum, Integer pageSize);

    /**
     * 查询所有权限列表
     */
    List<Menu> list(Menu menu);

    /**
     * 查询角色权限列表
     *
     */
    List<Menu> selectMenuListByRoleId(Long userId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId
     *            用户ID
     * @return 权限列表
     */
    Set<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据角色ID查询权限
     *
     * @param roleId
     *            角色ID
     * @return 权限列表
     */
    Set<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 根据用户ID查询菜单树信息
     *
     * @param userId
     *            用户ID
     * @return 菜单列表
     */
    List<Menu> selectMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId
     *            角色ID
     * @return 选中菜单列表
     */
    List<Long> selectMenuIdsByRoleId(Long roleId);

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus
     *            菜单列表
     * @return 路由列表
     */
    List<RouterVo> buildMenus(List<Menu> menus);

    /**
     * 构建前端所需要树结构
     *
     * @param menus
     *            菜单列表
     * @return 树结构列表
     */
    List<Menu> buildMenuTree(List<Menu> menus);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus
     *            菜单列表
     * @return 下拉树结构列表
     */
    List<TreeSelect> buildMenuTreeSelect(List<Menu> menus);

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId
     *            菜单ID
     * @return 菜单信息
     */
    Menu getDetail(Long menuId);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId
     *            菜单ID
     * @return 结果 true 存在 false 不存在
     */
    boolean hasChildByMenuId(Long menuId);

    /**
     * 查询菜单是否存在角色
     *
     * @param menuId
     *            菜单ID
     * @return 结果 true 存在 false 不存在
     */
    boolean checkMenuExistRole(Long menuId);

    /**
     * 新增保存菜单信息
     *
     * @param menu
     *            菜单信息
     * @return 结果
     */
    int add(Menu menu);

    /**
     * 修改保存菜单信息
     *
     * @param menu
     *            菜单信息
     * @return 结果
     */
    int update(Menu menu);

    /**
     * 删除菜单管理信息
     *
     * @param menuId
     *            菜单ID
     * @return 结果
     */
    int delete(Long menuId);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu
     *            菜单信息
     * @return 结果
     */
    Boolean checkMenuNameUnique(Menu menu);
}
