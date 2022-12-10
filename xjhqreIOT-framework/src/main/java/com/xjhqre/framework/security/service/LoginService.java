package com.xjhqre.framework.security.service;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.xjhqre.common.constant.CacheConstants;
import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.constant.ErrorCode;
import com.xjhqre.common.domain.entity.User;
import com.xjhqre.common.domain.model.LoginUser;
import com.xjhqre.common.exception.ServiceException;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.ServletUtils;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.common.utils.ip.IpUtils;
import com.xjhqre.common.utils.redis.RedisCache;
import com.xjhqre.framework.manager.AsyncFactory;
import com.xjhqre.framework.manager.AsyncManager;
import com.xjhqre.framework.security.context.AuthenticationContextHolder;
import com.xjhqre.system.service.ConfigService;
import com.xjhqre.system.service.UserService;

/**
 * 登录校验方法
 * 
 * @author xjhqre
 */
@Component
public class LoginService {
    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigService configService;

    /**
     * 登录验证
     *
     * @param username
     *            用户名
     * @param password
     *            密码
     * @param code
     *            验证码
     * @param uuid
     *            唯一标识
     * @return token
     */
    public String login(String username, String password, String code, String uuid) {
        boolean captchaEnabled = this.configService.selectCaptchaEnabled();
        // 验证码开关
        if (captchaEnabled) {
            this.validateCaptcha(code, uuid);
        }
        // 用户验证
        Authentication authentication = null;
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
            // 将authenticationToken存入上下文，便于loadUserByUsername里校验密码，authenticationToken里包含了输入的用户名和密码
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername， 返回校验后的用户信息和权限信息
            authentication = this.authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLoginInfo(username, Constants.LOGIN_FAIL, "用户不存在/密码错误"));
                throw new ServiceException(ErrorCode.WRONG_USER_NAME_OR_PASSWORD, "用户名或密码错误");
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLoginInfo(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        AsyncManager.me().execute(AsyncFactory.recordLoginInfo(username, Constants.LOGIN_SUCCESS, "登陆成功"));
        LoginUser loginUser = (LoginUser)authentication.getPrincipal();
        this.recordLoginInfo(loginUser.getUserId());
        // 生成token，并存入redis
        return this.tokenService.createToken(loginUser);
    }

    /**
     * 校验验证码
     *
     * @param code
     *            验证码
     * @param uuid
     *            唯一标识
     * @return 结果
     */
    public void validateCaptcha(String code, String uuid) {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = this.redisCache.getCacheObject(verifyKey);
        this.redisCache.deleteObject(verifyKey);
        if (captcha == null) {
            throw new ServiceException("验证码已失效");
        }
        if (!code.equalsIgnoreCase(captcha)) {
            throw new ServiceException("验证码错误");
        }
    }

    /**
     * 记录登录信息
     *
     * @param userId
     *            用户ID
     */
    public void recordLoginInfo(Long userId) {
        User user = new User();
        user.setUserId(userId);
        user.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        user.setLoginDate(DateUtils.getNowDate());
        this.userService.updateById(user);
    }
}
