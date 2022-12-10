package com.xjhqre.common.domain.model;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 用户注册对象
 * 
 * @author xjhqre
 */
@Data
public class RegisterBody extends LoginBody {

    @NotBlank(message = "邮箱地址不能为空")
    private String email;
}
