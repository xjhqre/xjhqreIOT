package com.xjhqre.framework.advise;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.xjhqre.common.constant.HttpStatus;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.exception.ServiceException;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: xjhqre
 * @DateTime: 2022/4/23 20:04
 */
@Slf4j
@Component
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {

    // @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    // public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
    // log.error(ex.getMessage());
    //
    // if (ex.getMessage().contains("Duplicate entry")) {
    // String[] split = ex.getMessage().split(" ");
    // String msg = split[2] + "已存在";
    // return R.error(msg);
    // }
    //
    // return R.error("未知错误");
    // }

    /**
     * 统一处理参数校验异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R<String> handleValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return R.error(message);
    }

    /**
     * 我的服务异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = ServiceException.class)
    public R<String> handleMyServiceException(ServiceException e) {
        log.error("系统出现异常，异常编号：{}， 异常信息：{}", e.getCode(), e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 权限校验异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public R<String> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
        return R.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权");
    }

    // @ExceptionHandler(Exception.class)
    // public R<String> exceptionHandler(Exception ex) {
    // log.error(ex.getMessage());
    // return R.error("系统未知异常");
    // }
}