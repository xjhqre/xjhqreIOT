package com.xjhqre.admin.controller.system;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.annotation.Log;
import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.domain.entity.Menu;
import com.xjhqre.common.domain.model.TreeSelect;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.common.group.Insert;
import com.xjhqre.common.group.Update;
import com.xjhqre.system.service.MenuService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 菜单信息
 *
 * @author xjhqre
 */
@RestController
@RequestMapping("/system/menu")
@Api(value = "菜单操作接口", tags = "菜单操作接口")
public class MenuController extends BaseController {
    @Resource
    private MenuService menuService;

    @ApiOperation(value = "分页查询菜单列表")
    @GetMapping("/find")
    @PreAuthorize("@ss.hasPermission('system:menu:list')")
    public R<IPage<Menu>> find(Menu menu, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return R.success(this.menuService.find(menu, pageNum, pageSize));
    }

    @ApiOperation(value = "查询菜单列表")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('system:menu:list')")
    public R<List<Menu>> list(Menu menu) {
        return R.success(this.menuService.list(menu));
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @ApiOperation(value = "根据菜单编号获取详细信息")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    @GetMapping(value = "/getDetail")
    public R<Menu> getDetail(@RequestParam Long menuId) {
        Menu menu = this.menuService.getDetail(menuId);
        return R.success(menu);
    }

    /**
     * 查询所有菜单权限
     */
    @ApiOperation(value = "查询所有菜单权限")
    @GetMapping("/treeSelect")
    public R<List<TreeSelect>> treeSelect() {
        List<Menu> menus = this.menuService.selectMenuTreeByUserId(this.getUserId());
        List<TreeSelect> treeSelects = this.menuService.buildMenuTreeSelect(menus);
        return R.success(treeSelects);
    }

    /**
     * 加载对应角色菜单列表树
     */
    @ApiOperation(value = "查询角色权限树")
    @GetMapping(value = "/roleMenuTreeSelect/{roleId}")
    public R<String> roleMenuTreeSelect(@PathVariable("roleId") Long roleId) {
        List<Menu> menus = this.menuService.selectMenuListByRoleId(roleId);
        return R.success("加载对应角色菜单列表树成功").add("menuIds", this.menuService.selectMenuIdsByRoleId(roleId)).add("menus",
            this.menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 新增菜单
     */
    @ApiOperation(value = "新增菜单")
    @PreAuthorize("@ss.hasPermission('system:menu:add')")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<String> add(@Validated(Insert.class) @RequestBody Menu menu) {
        if (Constants.NOT_UNIQUE.equals(this.menuService.checkMenuNameUnique(menu))) {
            return R.error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        this.menuService.add(menu);
        return R.success("新增菜单成功");
    }

    /**
     * 修改菜单
     */
    @ApiOperation(value = "修改菜单")
    @PreAuthorize("@ss.hasPermission('system:menu:update')")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public R<String> update(@Validated(Update.class) @RequestBody Menu menu) {
        if (Constants.NOT_UNIQUE.equals(this.menuService.checkMenuNameUnique(menu))) {
            return R.error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (menu.getMenuId().equals(menu.getParentId())) {
            return R.error("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        this.menuService.update(menu);
        return R.success("修改菜单");
    }

    /**
     * 删除菜单
     */
    @ApiOperation(value = "删除菜单")
    @PreAuthorize("@ss.hasPermission('system:menu:delete')")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> delete(@RequestParam Long menuId) {
        if (this.menuService.hasChildByMenuId(menuId)) {
            return R.error("存在子菜单,不允许删除");
        }
        if (this.menuService.checkMenuExistRole(menuId)) {
            return R.error("菜单已分配,不允许删除");
        }
        this.menuService.delete(menuId);
        return R.success("删除菜单成功");
    }
}