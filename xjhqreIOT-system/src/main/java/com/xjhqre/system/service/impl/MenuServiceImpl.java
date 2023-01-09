package com.xjhqre.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.constant.UserConstants;
import com.xjhqre.common.domain.entity.Menu;
import com.xjhqre.common.domain.entity.User;
import com.xjhqre.common.domain.model.TreeSelect;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.system.domain.vo.MetaVo;
import com.xjhqre.system.domain.vo.RouterVo;
import com.xjhqre.system.mapper.MenuMapper;
import com.xjhqre.system.mapper.RoleMenuMapper;
import com.xjhqre.system.service.MenuService;

/**
 * 菜单 业务层处理
 *
 * @author ruoyi
 */
@Service
public class MenuServiceImpl implements MenuService {
    @Resource
    private MenuMapper menuMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Override
    public IPage<Menu> findMenu(Menu menu, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(menu.getMenuType() != null, Menu::getMenuType, menu.getMenuType())
            .eq(menu.getMenuId() != null, Menu::getMenuId, menu.getMenuId())
            .like(menu.getMenuName() != null, Menu::getMenuName, menu.getMenuName())
            .eq(menu.getParentId() != null, Menu::getParentId, menu.getParentId());
        return this.menuMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId
     *            用户ID
     * @return 菜单列表
     */
    @Override
    public List<Menu> selectMenuList(Long userId) {
        return this.selectMenuList(new Menu(), userId);
    }

    /**
     * 查询系统菜单列表
     *
     * @param menu
     *            菜单信息
     * @return 菜单列表
     */
    @Override
    public List<Menu> selectMenuList(Menu menu, Long userId) {
        List<Menu> menuList;
        // 管理员显示所有菜单信息
        if (User.isAdmin(userId)) {
            menuList = this.menuMapper.selectList(null);
        } else {
            menuList = this.menuMapper.selectMenuListByUserId(userId);
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
     * 根据角色ID查询权限
     *
     * @param roleId
     *            角色ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByRoleId(Long roleId) {
        List<String> perms = this.menuMapper.selectMenuPermsByRoleId(roleId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 根据用户ID查询菜单
     *
     * @param userId
     *            用户名称
     * @return 菜单列表
     */
    @Override
    public List<Menu> selectMenuTreeByUserId(Long userId) {
        List<Menu> menus;
        if (User.isAdmin(userId)) {
            menus = this.menuMapper.selectList(null);
        } else {
            menus = this.menuMapper.selectMenuTreeByUserId(userId);
        }
        return this.getChildPerms(menus, 0);
    }

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId
     *            角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        return this.menuMapper.selectMenuListByRoleId(roleId);
    }

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus
     *            菜单列表
     * @return 路由列表
     */
    @Override
    public List<RouterVo> buildMenus(List<Menu> menus) {
        List<RouterVo> routers = new LinkedList<>();
        for (Menu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden("0".equals(menu.getVisible()));
            router.setName(this.getRouteName(menu));
            router.setPath(this.getRouterPath(menu));
            router.setComponent(this.getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), 0 == menu.getIsCache(), menu.getPath()));
            List<Menu> cMenus = menu.getChildren();
            // 如果子菜单为 目录 类型
            if (!cMenus.isEmpty() && UserConstants.TYPE_DIR.equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(this.buildMenus(cMenus));
            }
            // 如果当前菜单为 外链
            else if (this.isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StringUtils.capitalize(menu.getPath()));
                children
                    .setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), 0 == menu.getIsCache(), menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            // 如果是 内链
            else if (menu.getParentId().intValue() == 0 && this.isInnerLink(menu)) {
                router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                String routerPath = this.innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(UserConstants.INNER_LINK);
                children.setName(StringUtils.capitalize(routerPath));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
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
        List<Long> tempList = new ArrayList<>();
        for (Menu dept : menus) {
            tempList.add(dept.getMenuId());
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
     * 查询菜单使用数量
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
        Long menuId = StringUtils.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getMenuName, menu.getMenuName()).eq(Menu::getParentId, menu.getParentId());
        Menu info = this.menuMapper.selectOne(wrapper);
        if (StringUtils.isNotNull(info) && !Objects.equals(info.getMenuId(), menuId)) {
            return Constants.NOT_UNIQUE;
        }
        return Constants.UNIQUE;
    }

    /**
     * 获取路由名称
     *
     * @param menu
     *            菜单信息
     * @return 路由名称
     */
    public String getRouteName(Menu menu) {
        String routerName = StringUtils.capitalize(menu.getPath());
        // 非外链并且是一级目录（类型为目录）
        if (this.isMenuFrame(menu)) {
            routerName = StringUtils.EMPTY;
        }
        return routerName;
    }

    /**
     * 获取路由地址
     *
     * @param menu
     *            菜单信息
     * @return 路由地址
     */
    public String getRouterPath(Menu menu) {
        String routerPath = menu.getPath();
        // 内链打开外网方式
        if (menu.getParentId() != 0 && this.isInnerLink(menu)) {
            routerPath = this.innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (0 == menu.getParentId() && UserConstants.TYPE_DIR.equals(menu.getMenuType())
            && UserConstants.NO_FRAME.equals(menu.getIsFrame())) {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (this.isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     *
     * @param menu
     *            菜单信息
     * @return 组件信息
     */
    public String getComponent(Menu menu) {
        String component = UserConstants.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !this.isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StringUtils.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != 0
            && this.isInnerLink(menu)) {
            component = UserConstants.INNER_LINK;
        } else if (StringUtils.isEmpty(menu.getComponent()) && this.isParentView(menu)) {
            component = UserConstants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * 是否为菜单内部跳转
     *
     * @param menu
     *            菜单信息
     * @return 结果
     */
    public boolean isMenuFrame(Menu menu) {
        return menu.getParentId().intValue() == 0 && UserConstants.TYPE_MENU.equals(menu.getMenuType())
            && menu.getIsFrame().equals(UserConstants.NO_FRAME);
    }

    /**
     * 是否为内链组件
     *
     * @param menu
     *            菜单信息
     * @return 结果
     */
    public boolean isInnerLink(Menu menu) {
        return menu.getIsFrame().equals(UserConstants.NO_FRAME) && StringUtils.ishttp(menu.getPath());
    }

    /**
     * 是否为parent_view组件
     *
     * @param menu
     *            菜单信息
     * @return 结果
     */
    public boolean isParentView(Menu menu) {
        return menu.getParentId() != 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType());
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
        List<Menu> returnList = new ArrayList<>();
        for (Menu t : list) {
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
     */
    private void recursionFn(List<Menu> list, Menu t) {
        // 得到子节点列表
        List<Menu> childList = this.getChildList(list, t);
        t.setChildren(childList);
        for (Menu tChild : childList) {
            if (this.hasChild(list, tChild)) {
                this.recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<Menu> getChildList(List<Menu> list, Menu t) {
        List<Menu> tlist = new ArrayList<>();
        for (Menu n : list) {
            if (n.getParentId().longValue() == t.getMenuId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<Menu> list, Menu t) {
        return this.getChildList(list, t).size() > 0;
    }

    /**
     * 内链域名特殊字符替换
     *
     */
    public String innerLinkReplaceEach(String path) {
        return StringUtils.replaceEach(path, new String[] {Constants.HTTP, Constants.HTTPS, Constants.WWW, "."},
            new String[] {"", "", "", "/"});
    }
}
