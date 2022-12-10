package com.xjhqre.framework.security.handle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.alibaba.fastjson2.JSON;
import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.constant.HttpStatus;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.domain.model.LoginUser;
import com.xjhqre.common.utils.ServletUtils;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.framework.manager.AsyncFactory;
import com.xjhqre.framework.manager.AsyncManager;
import com.xjhqre.framework.security.service.TokenService;

/**
 * 自定义退出处理类 返回成功
 * 
 * @author xjhqre
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Autowired
    private TokenService tokenService;

    /**
     * 退出处理
     * 
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        // 获取当前登陆用户
        LoginUser loginUser = this.tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            String userName = loginUser.getUsername();
            // 删除redis里的用户缓存记录
            this.tokenService.delLoginUser(loginUser.getToken());
            // 记录用户退出日志
            AsyncManager.me().execute(AsyncFactory.recordLoginInfo(userName, Constants.LOGOUT, "退出成功"));
            ServletUtils.renderString(response, JSON.toJSONString(R.error(HttpStatus.SUCCESS, "退出成功")));
        } else {
            ServletUtils.renderString(response, JSON.toJSONString(R.error(HttpStatus.SUCCESS, "退出失败")));
        }
    }
}
