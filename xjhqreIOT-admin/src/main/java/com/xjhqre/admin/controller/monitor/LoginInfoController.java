package com.xjhqre.admin.controller.monitor;

import javax.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.annotation.Log;
import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.framework.security.service.PasswordService;
import com.xjhqre.system.domain.entity.LoginInfo;
import com.xjhqre.system.service.LoginInfoService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 系统访问记录
 *
 * @author xjhqre
 */
@RestController
@Api(value = "系统访问记录", tags = "系统访问记录")
@RequestMapping("/monitor/loginInfo")
public class LoginInfoController extends BaseController {
    @Resource
    private LoginInfoService loginInfoService;

    @Resource
    private PasswordService passwordService;

    @ApiOperation(value = "分页查询系统访问记录")
    @PreAuthorize("@ss.hasPermission('monitor:logininfor:list')")
    @GetMapping("/find")
    public R<IPage<LoginInfo>> find(LoginInfo loginInfo, @RequestParam Integer pageNum,
        @RequestParam Integer pageSize) {
        return R.success(this.loginInfoService.find(loginInfo, pageNum, pageSize));
    }

    /**
     * 根据条件删除登陆日志
     *
     * @param infoIds
     * @return
     */
    @ApiOperation(value = "根据条件删除登陆日志")
    @PreAuthorize("@ss.hasPermission('monitor:logininfor:delete')")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/delete/{infoIds}")
    public R<String> delete(@PathVariable Long[] infoIds) {
        this.loginInfoService.delete(infoIds);
        return R.success("删除日志成功");
    }

    /**
     * 清空所有登陆日志
     *
     * @return
     */
    @ApiOperation(value = "清空所有登陆日志")
    @PreAuthorize("@ss.hasPermission('monitor:logininfor:delete')")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public R<String> clean() {
        this.loginInfoService.cleanLoginInfo();
        return R.success("清空登陆日志成功");
    }

    /**
     * 解锁账户
     *
     * @param userName
     * @return
     */
    @ApiOperation(value = "解锁账户")
    @PreAuthorize("@ss.hasPermission('monitor:logininfor:unlock')")
    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @GetMapping("/unlock/{userName}")
    public R<String> unlock(@PathVariable("userName") String userName) {
        this.passwordService.clearLoginRecordCache(userName);
        return R.success("解锁账户成功");
    }
}
