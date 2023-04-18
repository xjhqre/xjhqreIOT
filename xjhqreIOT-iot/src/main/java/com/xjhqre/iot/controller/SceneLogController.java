package com.xjhqre.iot.controller;

import javax.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.domain.R;
import com.xjhqre.iot.domain.entity.SceneLog;
import com.xjhqre.iot.service.SceneLogService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 场景联动日志接口
 *
 * @author xjhqre
 * @since 2023-04-16
 */
@Api(tags = "场景联动日志接口")
@RestController
@RequestMapping("/iot/sceneLog")
public class SceneLogController extends BaseController {
    @Resource
    private SceneLogService sceneLogService;

    @ApiOperation(value = "分页查询设备告警日志列表")
    @PreAuthorize("@ss.hasPermission('iot:alert:list')")
    @GetMapping("/find")
    public R<IPage<SceneLog>> find(SceneLog sceneLog, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return R.success(this.sceneLogService.find(sceneLog, pageNum, pageSize));
    }
}
