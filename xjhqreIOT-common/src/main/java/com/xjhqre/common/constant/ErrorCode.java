package com.xjhqre.common.constant;

/**
 * <p>
 * ErrorCode
 * </p>
 *
 * @author xjhqre
 * @since 10月 03, 2022
 */
public class ErrorCode {

    /**
     * 系统错误
     */
    public static final int UNKNOWN_EXCEPTION = 10000; // 系统未知异常
    public static final int VALID_EXCEPTION = 10001; // 参数格式校验失败
    public static final int NULL_EXCEPTION = 10002; // 不允许传入空值

    /**
     * 用户错误
     */
    public static final int EMAIL_DUPLICATE = 20000; // 邮箱已存在
    public static final int USERNAME_DUPLICATE = 20001; // 用户名已存在
    public static final int WRONG_USER_NAME_OR_PASSWORD = 20002; // 用户名或密码错误
    public static final int ROLE_DOES_NOT_EXIST = 20003; // 该用户不存在角色
}
