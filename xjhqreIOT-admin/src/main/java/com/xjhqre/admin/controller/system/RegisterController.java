package com.xjhqre.admin.controller.system;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.domain.model.RegisterBody;
import com.xjhqre.framework.security.service.RegisterService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 注册验证
 *
 * @author xjhqre
 * @since 2022-11-3
 */
@RestController
@Api(value = "用户注册接口", tags = "用户注册接口")
public class RegisterController extends BaseController {

    @Resource
    private RegisterService registerService;

    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    public R<String> register(@RequestBody @Validated RegisterBody user) {
        this.registerService.register(user);
        return R.success("注册成功");
    }
}
