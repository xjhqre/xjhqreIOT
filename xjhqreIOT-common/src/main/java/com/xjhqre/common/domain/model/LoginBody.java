package com.xjhqre.common.domain.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * 用户登录注册对象
 * 
 * @author xjhqre
 */
@Data
public class LoginBody {
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名必须在2~20个字符之间")
    private String username;

    /**
     * 用户密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 5, max = 20, message = "密码长度必须在5~20个字符之间")
    private String password;

    /**
     * 验证码
     */
    @NotBlank(message = "邮箱验证码不能为空")
    private String code;

    /**
     * 唯一标识，作为redis存储的key
     */
    private String uuid;
}
