package com.xjhqre.framework.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.xjhqre.common.constant.CacheConstants;
import com.xjhqre.common.domain.entity.User;
import com.xjhqre.common.domain.model.LoginUser;
import com.xjhqre.common.enums.UserStatus;
import com.xjhqre.common.exception.ServiceException;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.common.utils.redis.RedisCache;
import com.xjhqre.system.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户验证处理
 *
 * @author xjhqre
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RedisCache redisCache;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userService.selectUserByUserName(username);
        if (StringUtils.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException("登录用户：" + username + " 不存在");
        } else if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            log.info("登录用户：{} 已被删除.", username);
            throw new ServiceException("对不起，您的账号：" + username + " 已被删除");
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new ServiceException("对不起，您的账号：" + username + " 已被禁用，请联系管理员");
        }

        Integer userStatus = this.redisCache.getCacheObject(CacheConstants.USER_STATUS + username);
        if (userStatus != null && userStatus == 2) {
            long expire = this.redisCache.getExpire(CacheConstants.USER_STATUS + username);
            throw new ServiceException("输入错误次数太多，账户已被锁定，请在" + expire / 60 + "分钟后尝试");
        }

        // 自定义密码校验
        this.passwordService.validate(user);

        return new LoginUser(user.getUserId(), user, this.permissionService.getMenuPermission(user));
    }
}
