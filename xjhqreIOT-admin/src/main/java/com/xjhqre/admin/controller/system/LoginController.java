package com.xjhqre.admin.controller.system;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.domain.entity.Menu;
import com.xjhqre.common.domain.entity.User;
import com.xjhqre.common.domain.model.LoginBody;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.framework.security.service.LoginService;
import com.xjhqre.framework.security.service.PermissionService;
import com.xjhqre.system.domain.vo.RouterVo;
import com.xjhqre.system.service.MenuService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 登录验证
 *
 * @author xjhqre
 */
@RestController
@Api(value = "用户登陆接口", tags = "用户登陆接口")
public class LoginController extends BaseController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private PermissionService permissionService;
    @Resource
    MenuService menuService;

    /**
     * 登录方法
     *
     * @param loginBody
     *            登录信息
     * @return token
     */
    @ApiOperation(value = "登陆方法")
    @PostMapping(value = "/login")
    public R<String> login(@RequestBody LoginBody loginBody) {
        // 生成令牌
        String token = this.loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
            loginBody.getUuid());
        return R.success("登陆成功").add(Constants.TOKEN, token);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @ApiOperation(value = "获取用户信息")
    @GetMapping("/getInfo")
    public R<String> getInfo() {
        User user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = this.permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = this.permissionService.getMenuPermission(user);
        return R.success("获取信息成功").add("user", user).add("roles", roles).add("permissions", permissions);
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public R<List<RouterVo>> getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<Menu> menus = this.menuService.selectMenuTreeByUserId(userId);
        return R.success(this.menuService.buildMenus(menus));
    }
}
