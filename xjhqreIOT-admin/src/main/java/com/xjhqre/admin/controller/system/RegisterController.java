package com.xjhqre.admin.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.domain.model.RegisterBody;
import com.xjhqre.common.utils.redis.RedisCache;
import com.xjhqre.framework.security.service.RegisterService;
import com.xjhqre.system.service.ConfigService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 注册验证
 *
 * @author xjhqre
 */
@RestController
@Api(value = "用户注册接口", tags = "用户注册接口")
public class RegisterController extends BaseController {
    @Autowired
    private RegisterService registerService;

    @Autowired
    private ConfigService configService;
    @Autowired
    private RedisCache redisCache;

    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    public R<String> register(@RequestBody @Validated RegisterBody user) {
        this.registerService.register(user);
        return R.success("注册成功");
    }
}
