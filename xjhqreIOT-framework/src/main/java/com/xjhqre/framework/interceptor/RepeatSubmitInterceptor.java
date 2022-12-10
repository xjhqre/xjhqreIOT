package com.xjhqre.framework.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.alibaba.fastjson2.JSON;
import com.xjhqre.common.annotation.RepeatSubmit;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.utils.ServletUtils;

/**
 * 防止重复提交拦截器
 *
 * @author xjhqre
 */
@Component
public abstract class RepeatSubmitInterceptor implements HandlerInterceptor {
    // 在业务处理器处理请求之前被调用。预处理，可以进行编码、安全控制、权限校验等处理；
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 是否为映射方法
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            Method method = handlerMethod.getMethod();
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
            if (annotation != null) {
                // 如果是重复提交
                if (this.isRepeatSubmit(request, annotation)) {
                    ServletUtils.renderString(response, JSON.toJSONString(R.error(annotation.message())));
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 验证是否重复提交由子类实现具体的防重复提交的规则
     *
     * @param request
     * @return
     * @throws Exception
     */
    public abstract boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation);
}
