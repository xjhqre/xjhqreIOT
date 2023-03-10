package com.xjhqre.iot.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.annotation.Log;
import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.iot.domain.entity.Scene;
import com.xjhqre.iot.service.SceneService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 场景联动接口
 *
 * @author xjhqre
 * @since 2023-01-7
 */
@Api(tags = "场景联动接口")
@RestController
@RequestMapping("/iot/scene")
public class SceneController extends BaseController {

    @Resource
    private SceneService sceneService;

    @ApiOperation(value = "分页查询场景联动列表")
    @PreAuthorize("@ss.hasPermission('iot:scene:list')")
    @GetMapping("/find")
    public R<IPage<Scene>> find(Scene scene, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return R.success(this.sceneService.find(scene, pageNum, pageSize));
    }

    /**
     * 获取场景联动详情
     */
    @ApiOperation(value = "获取场景联动详情")
    @PreAuthorize("@ss.hasPermission('iot:scene:query')")
    @RequestMapping(value = "/getDetail", method = {RequestMethod.POST, RequestMethod.GET})
    public R<Scene> getDetail(@RequestParam Long sceneId) {
        return R.success(this.sceneService.getDetail(sceneId));
    }

    /**
     * 新增场景联动
     */
    @ApiOperation(value = "新增场景联动")
    @PreAuthorize("@ss.hasPermission('iot:scene:add')")
    @Log(title = "场景联动", businessType = BusinessType.INSERT)
    @RequestMapping(value = "/add", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> add(Scene scene) {
        this.sceneService.add(scene);
        return R.success("新增场景联动成功");
    }

    /**
     * 修改场景联动
     */
    @ApiOperation(value = "修改场景联动")
    @PreAuthorize("@ss.hasPermission('iot:scene:update')")
    @Log(title = "场景联动", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> update(@RequestBody Scene scene) {
        this.sceneService.update(scene);
        return R.success("修改场景联动成功");
    }

    /**
     * 删除场景联动
     */
    @ApiOperation(value = "删除场景联动")
    @PreAuthorize("@ss.hasPermission('iot:scene:delete')")
    @Log(title = "场景联动", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> remove(@RequestParam List<Long> sceneIdList) {
        this.sceneService.remove(sceneIdList);
        return R.success("删除场景联动成功");
    }
}
