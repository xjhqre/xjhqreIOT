package com.xjhqre.admin.controller.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjhqre.common.annotation.Log;
import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.constant.CacheConstants;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.domain.UserOnline;
import com.xjhqre.common.domain.model.LoginUser;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.common.utils.redis.RedisCache;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 在线用户监控
 * 
 * @author xjhqre
 */
@RestController
@Api(value = "在线用户监控", tags = "在线用户监控")
@RequestMapping("/monitor/online")
public class UserOnlineController extends BaseController {

    @Autowired
    private RedisCache redisCache;

    @ApiOperation(value = "分页查询在线用户列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "正整数，表示查询第几页", required = true, dataType = "int", example = "1"),
        @ApiImplicitParam(name = "pageSize", value = "正整数，表示每页几条记录", required = true, dataType = "int",
            example = "20")})
    @PreAuthorize("@ss.hasPermission('monitor:online:list')")
    @GetMapping("list/{pageNum}/{pageSize}")
    public R<IPage<UserOnline>> listUserOnline(String ipaddr, String userName, @PathVariable("pageNum") Integer pageNum,
        @PathVariable("pageSize") Integer pageSize) {
        // 获取所有以 login_tokens: 开头的键
        Collection<String> keys = this.redisCache.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
        List<UserOnline> userOnlineList = new ArrayList<>();
        for (String key : keys) {
            // 获取对应键的 LoginUser
            LoginUser user = this.redisCache.getCacheObject(key);
            if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName)) {
                if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername())) {
                    userOnlineList.add(this.loginUserToUserOnline(user));
                }
            } else if (StringUtils.isNotEmpty(ipaddr)) {
                if (StringUtils.equals(ipaddr, user.getIpaddr())) {
                    userOnlineList.add(this.loginUserToUserOnline(user));
                }
            } else if (StringUtils.isNotEmpty(userName) && StringUtils.isNotNull(user.getUser())) {
                if (StringUtils.equals(userName, user.getUsername())) {
                    userOnlineList.add(this.loginUserToUserOnline(user));
                }
            } else {
                // 添加在线用户信息
                userOnlineList.add(this.loginUserToUserOnline(user));
            }
        }
        // Collections.reverse(userOnlineList);
        // userOnlineList.removeAll(Collections.singleton(null));
        IPage<UserOnline> userOnlineIPage = new Page<>(pageNum, pageSize);
        userOnlineIPage.setRecords(userOnlineList);
        userOnlineIPage.setTotal(userOnlineList.size());
        return R.success(userOnlineIPage);
    }

    /**
     * 强退用户
     */
    @ApiOperation(value = "强制退出用户")
    @PreAuthorize("@ss.hasPermission('monitor:online:forceLogout')")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public R<String> forceLogout(@PathVariable String tokenId) {
        this.redisCache.deleteObject(CacheConstants.LOGIN_TOKEN_KEY + tokenId);
        return R.success("强制退出用户成功");
    }

    /**
     * 设置在线用户信息
     *
     * @param user
     *            用户信息
     * @return 在线用户
     */
    public UserOnline loginUserToUserOnline(LoginUser user) {
        if (StringUtils.isNull(user) || StringUtils.isNull(user.getUser())) {
            return null;
        }
        UserOnline sysUserOnline = new UserOnline();
        sysUserOnline.setTokenId(user.getToken());
        sysUserOnline.setUserName(user.getUsername());
        sysUserOnline.setIpaddr(user.getIpaddr());
        sysUserOnline.setLoginLocation(user.getLoginLocation());
        sysUserOnline.setBrowser(user.getBrowser());
        sysUserOnline.setOs(user.getOs());
        sysUserOnline.setLoginTime(user.getLoginTime());
        return sysUserOnline;
    }
}
