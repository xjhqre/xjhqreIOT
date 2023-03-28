package com.xjhqre.iot.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.domain.R;
import com.xjhqre.iot.domain.entity.ThingsModelValue;
import com.xjhqre.iot.service.ThingsModelValueService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 物模型操作接口
 * </p>
 *
 * @author xjhqre
 * @since 12月 20, 2022
 */
@RestController
@RequestMapping("/iot/modelValue")
@Api(tags = "设备物模型值接口")
public class ThingsModelValueController extends BaseController {
    @Resource
    private ThingsModelValueService thingsModelValueService;

    @ApiOperation(value = "分页查询设备物模型值列表")
    @PreAuthorize("@ss.hasPermission('iot:model:list')")
    @GetMapping("/list")
    public R<List<ThingsModelValue>> list(ThingsModelValue thingsModelValue, Integer dateRange) {
        return R.success(this.thingsModelValueService.list(thingsModelValue, dateRange));
    }
}
