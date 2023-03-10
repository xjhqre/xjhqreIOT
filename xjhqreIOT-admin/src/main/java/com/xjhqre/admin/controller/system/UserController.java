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
import com.xjhqre.common.domain.entity.Role;
import com.xjhqre.common.domain.entity.User;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.common.group.Insert;
import com.xjhqre.common.group.Update;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.system.service.RoleService;
import com.xjhqre.system.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 用户信息
 *
 * @author xjhqre
 */
@RestController
@Api(value = "用户操作接口", tags = "用户操作接口")
@RequestMapping("/system/user")
public class UserController extends BaseController {
    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;

    @ApiOperation(value = "分页查询用户列表")
    @GetMapping("/find")
    @PreAuthorize("@ss.hasPermission('system:user:list')")
    public R<IPage<User>> find(User user, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return R.success(this.userService.find(user, pageNum, pageSize));
    }

    /**
     * 根据用户编号获取详细信息
     */
    @ApiOperation(value = "根据用户编号获取详细信息")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    @RequestMapping(value = "/getDetail", method = {RequestMethod.POST, RequestMethod.GET})
    public R<User> getDetail(@RequestParam Long userId) {
        return R.success(this.userService.getDetail(userId));
    }

    /**
     * 新增用户
     */
    @ApiOperation(value = "添加用户")
    @PreAuthorize("@ss.hasPermission('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<String> add(@RequestBody @Validated(Insert.class) User user) {
        if (Constants.NOT_UNIQUE.equals(this.userService.checkUserNameUnique(user))) {
            return R.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getMobile())
            && Constants.NOT_UNIQUE.equals(this.userService.checkPhoneUnique(user))) {
            return R.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail())
            && Constants.NOT_UNIQUE.equals(this.userService.checkEmailUnique(user))) {
            return R.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(this.getUsername());
        user.setCreateTime(DateUtils.getNowDate());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        this.userService.insertUser(user);
        return R.success("新增用户'" + user.getUserName() + "'成功");
    }

    /**
     * 修改用户
     */
    @ApiOperation(value = "修改用户")
    @PreAuthorize("@ss.hasPermission('system:user:update')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public R<String> update(@RequestBody @Validated(Update.class) User user) {
        this.userService.checkUserAllowed(user.getUserId());
        if (Constants.NOT_UNIQUE.equals(this.userService.checkUserNameUnique(user))) {
            return R.error("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getMobile())
            && Constants.NOT_UNIQUE.equals(this.userService.checkPhoneUnique(user))) {
            return R.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail())
            && Constants.NOT_UNIQUE.equals(this.userService.checkEmailUnique(user))) {
            return R.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(this.getUsername());
        this.userService.updateUser(user);
        return R.success("修改用户'" + user.getUserName() + "'成功");
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermission('system:user:update')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public R<String> changeStatus(@RequestBody @Validated(Update.class) User user) {
        this.userService.checkUserAllowed(user.getUserId());
        user.setUpdateBy(this.getUsername());
        this.userService.changeStatus(user);
        return R.success("状态修改成功");
    }

    /**
     * 删除用户
     */
    @ApiOperation(value = "删除用户")
    @PreAuthorize("@ss.hasPermission('system:user:delete')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/delete/{userIds}", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> delete(@PathVariable("userIds") List<Long> userIds) {
        // 不能删除当前登陆用户
        if (userIds.contains(SecurityUtils.getUserId())) {
            return R.error("当前用户不能删除");
        }
        this.userService.deleteUserByIds(userIds);
        return R.success("删除用户成功");
    }

    /**
     * 根据用户编号获取角色
     */
    @ApiOperation(value = "根据用户id获取用户角色")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public R<List<Role>> authRole(@PathVariable("userId") Long userId) {
        User user = this.userService.getById(userId);
        List<Role> roles = this.roleService.selectRolesByUserId(userId);
        return R.success(roles).add("user", user);
    }
}
