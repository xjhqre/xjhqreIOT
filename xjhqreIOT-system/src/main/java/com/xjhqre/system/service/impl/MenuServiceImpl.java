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
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.system.domain.vo.MetaVo;
import com.xjhqre.system.domain.vo.RouterVo;
import com.xjhqre.system.mapper.MenuMapper;
import com.xjhqre.system.mapper.RoleMenuMapper;
import com.xjhqre.system.service.MenuService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 菜单 业务层处理
 *
 * @author ruoyi
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MenuServiceImpl implements MenuService {
    @Resource
    private MenuMapper menuMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Override
    public IPage<Menu> find(Menu menu, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(menu.getMenuType() != null && !"".equals(menu.getMenuType()), Menu::getMenuType, menu.getMenuType())
            .eq(menu.getMenuId() != null, Menu::getMenuId, menu.getMenuId())
            .like(menu.getMenuName() != null && !"".equals(menu.getMenuName()), Menu::getMenuName, menu.getMenuName())
            .eq(menu.getParentId() != null, Menu::getParentId, menu.getParentId())
            .eq(menu.getStatus() != null, Menu::getStatus, menu.getStatus());
        return this.menuMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<Menu> list(Menu menu) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(menu.getMenuType() != null && !"".equals(menu.getMenuType()), Menu::getMenuType, menu.getMenuType())
            .eq(menu.getMenuId() != null, Menu::getMenuId, menu.getMenuId())
            .like(menu.getMenuName() != null && !"".equals(menu.getMenuName()), Menu::getMenuName, menu.getMenuName())
            .eq(menu.getParentId() != null, Menu::getParentId, menu.getParentId())
            .eq(menu.getStatus() != null, Menu::getStatus, menu.getStatus());
        return this.menuMapper.selectList(wrapper);
    }

    /**
     * 查询角色权限列表
     *
     */
    @Override
    public List<Menu> selectMenuListByRoleId(Long roleId) {
        List<Menu> menuList;
        // 管理员显示所有菜单信息
        if (User.isAdmin(roleId)) {
            menuList = this.menuMapper.selectList(null);
        } else {
            menuList = this.menuMapper.selectMenuListByRoleId(roleId);
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
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Menu::getParentId).orderByAsc(Menu::getOrderNum);
        if (User.isAdmin(userId)) {
            menus = this.menuMapper.selectList(wrapper);
        } else {
            menus = this.menuMapper.selectMenuTreeByUserId(userId);
        }
        return this.buildMenuTree(menus);
    }

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId
     *            角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<Long> selectMenuIdsByRoleId(Long roleId) {
        return this.menuMapper.selectMenuIdsByRoleId(roleId);
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
    // @Override
    // public List<Menu> buildMenuTree(List<Menu> menus) {
    // List<Menu> returnList = new ArrayList<>();
    // for (Menu menu : menus) {
    // // 如果是顶级节点
    // if (menu.getParentId().intValue() == 0) {
    // this.recursionFn(menus, menu);
    // returnList.add(menu);
    // }
    // }
    // if (returnList.isEmpty()) {
    // returnList = menus;
    // }
    // return returnList;
    // }

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
    public Menu getDetail(Long menuId) {
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
    public int add(Menu menu) {
        Integer count = this.menuMapper.getMaxId(menu.getParentId());
        menu.setMenuId(menu.getParentId() * 100 + count + 1);
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
    public int update(Menu menu) {
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
    public int delete(Long menuId) {
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
     * 使用递归方法建树
     */
    @Override
    public List<Menu> buildMenuTree(List<Menu> treeNodes) {
        List<Menu> trees = new ArrayList<>();
        for (Menu treeNode : treeNodes) {
            if (treeNode.getParentId() == 0) {
                Menu children = this.findChildren(treeNode, treeNodes);
                trees.add(children);
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     *
     */
    public Menu findChildren(Menu treeNode, List<Menu> treeNodes) {
        for (Menu item : treeNodes) {
            if (treeNode.getMenuId().equals(item.getParentId())) {
                if (treeNode.getChildren() == null) {
                    treeNode.setChildren(new ArrayList<>());
                }
                treeNode.getChildren().add(this.findChildren(item, treeNodes));
            }
        }
        return treeNode;
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
