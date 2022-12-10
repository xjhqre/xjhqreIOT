package com.xjhqre.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.domain.entity.Menu;
import com.xjhqre.common.domain.entity.User;
import com.xjhqre.common.domain.model.TreeSelect;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.system.mapper.MenuMapper;
import com.xjhqre.system.mapper.RoleMenuMapper;
import com.xjhqre.system.service.MenuService;

/**
 * 菜单 业务层处理
 *
 * @author xjhqre
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    public static final String PREMISSION_STRING = "perms[\"{0}\"]";

    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;

    /**
     * 分页查询菜单权限
     * 
     * @param menu
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public IPage<Menu> findMenu(Menu menu, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(menu.getMenuId() != null, Menu::getMenuId, menu.getMenuId())
            .like(menu.getMenuName() != null, Menu::getMenuName, menu.getMenuName())
            .eq(menu.getMenuType() != null, Menu::getMenuType, menu.getMenuType())
            .eq(menu.getParentId() != null, Menu::getParentId, menu.getParentId());
        return this.menuMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
    }

    /**
     * 查询用户权限信息
     *
     * @param menu
     *            菜单信息
     * @return 菜单列表
     */
    @Override
    public List<Menu> selectMenuList(Menu menu, Long userId) {
        List<Menu> menuList = null;
        // 管理员显示所有菜单信息
        if (User.isSuperAdmin(userId)) {
            menuList = this.menuMapper.selectList(null);
        } else {
            menu.getParams().put("userId", userId);
            menuList = this.menuMapper.selectMenuListByUserId(menu);
        }
        return menuList;
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId
     *            用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        List<String> perms = this.menuMapper.selectMenuPermsByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 根据角色ID查询权限，用于存储在 LoginUser 里
     *
     * @param roleId
     *            角色ID
     * @return 权限列表 {system:menu:list, system:dict:list}
     */
    @Override
    public Set<String> selectMenuPermSetByRoleId(Long roleId) {
        List<String> perms = this.menuMapper.selectMenuPermsByRoleId(roleId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    @Override
    public List<Menu> selectMenuListByRoleId(Long roleId) {
        return this.menuMapper.selectMenuListByRoleId(roleId);
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus
     *            菜单列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<Menu> menus) {
        List<Menu> menuTrees = this.buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 构建前端所需要树结构
     *
     * @param menus
     *            菜单列表
     * @return 树结构列表
     */
    @Override
    public List<Menu> buildMenuTree(List<Menu> menus) {
        List<Menu> returnList = new ArrayList<>();
        List<Long> tempList = new ArrayList<>(); // 存放所有权限的id
        for (Menu menu : menus) {
            tempList.add(menu.getMenuId());
        }
        for (Menu menu : menus) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(menu.getParentId())) {
                this.recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId
     *            菜单ID
     * @return 菜单信息
     */
    @Override
    public Menu selectMenuById(Long menuId) {
        return this.menuMapper.selectById(menuId);
    }

    /**
     * 是否存在菜单子节点
     *
     * @param menuId
     *            菜单ID
     * @return 结果
     */
    @Override
    public boolean hasChildByMenuId(Long menuId) {
        int result = this.menuMapper.hasChildByMenuId(menuId);
        return result > 0;
    }

    /**
     * 查询权限是否被使用，用于删除权限时校验
     *
     * @param menuId
     *            菜单ID
     * @return 结果
     */
    @Override
    public boolean checkMenuExistRole(Long menuId) {
        int result = this.roleMenuMapper.checkMenuExistRole(menuId);
        return result > 0;
    }

    /**
     * 新增保存菜单信息
     *
     * @param menu
     *            菜单信息
     * @return 结果
     */
    @Override
    public int insertMenu(Menu menu) {
        menu.setCreateBy(SecurityUtils.getUsername());
        menu.setCreateTime(DateUtils.getNowDate());
        return this.menuMapper.insert(menu);
    }

    /**
     * 修改保存菜单信息
     *
     * @param menu
     *            菜单信息
     * @return 结果
     */
    @Override
    public int updateMenu(Menu menu) {
        menu.setUpdateBy(SecurityUtils.getUsername());
        menu.setUpdateTime(DateUtils.getNowDate());
        return this.menuMapper.updateById(menu);
    }

    /**
     * 删除菜单管理信息
     *
     * @param menuId
     *            菜单ID
     * @return 结果
     */
    @Override
    public int deleteMenuById(Long menuId) {
        return this.menuMapper.deleteById(menuId);
    }

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu
     *            菜单信息
     * @return 结果
     */
    @Override
    public Boolean checkMenuNameUnique(Menu menu) {
        long menuId = StringUtils.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getParentId, menu.getParentId()).eq(Menu::getMenuName, menu.getMenuName());
        Menu info = this.menuMapper.selectOne(queryWrapper);
        return !StringUtils.isNotNull(info) || info.getMenuId() == menuId;
    }

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list
     *            分类表
     * @param parentId
     *            传入的父节点ID
     * @return String
     */
    public List<Menu> getChildPerms(List<Menu> list, int parentId) {
        List<Menu> returnList = new ArrayList<Menu>();
        for (Iterator<Menu> iterator = list.iterator(); iterator.hasNext();) {
            Menu t = iterator.next();
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId() == parentId) {
                this.recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param allMenuList
     *            权限列表
     * @param parent
     *            上一级权限
     */
    private void recursionFn(List<Menu> allMenuList, Menu parent) {
        // 得到 t 的直接子节点列表
        List<Menu> childList = this.getChildList(allMenuList, parent);
        parent.setChildren(childList);
        for (Menu tChild : childList) {
            // 判断 tChild 是否还有子节点
            if (this.getChildList(allMenuList, tChild).size() > 0) {
                // 递归
                this.recursionFn(allMenuList, tChild);
            }
        }
    }

    /**
     * 得到当前节点 t 的直接子节点列表
     */
    private List<Menu> getChildList(List<Menu> list, Menu t) {
        List<Menu> tlist = new ArrayList<>();
        for (Menu child : list) {
            if (child.getParentId().longValue() == t.getMenuId().longValue()) {
                tlist.add(child);
            }
        }
        return tlist;
    }

}
