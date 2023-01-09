package com.xjhqre.iot.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.annotation.Log;
import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.common.group.Insert;
import com.xjhqre.common.group.Update;
import com.xjhqre.iot.domain.entity.ThingsModel;
import com.xjhqre.iot.service.ThingsModelService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
@RequestMapping("/iot/model")
@Api(tags = "产品物模型接口")
public class ThingsModelController extends BaseController {
    @Resource
    private ThingsModelService thingsModelService;

    @ApiOperation(value = "分页查询产品物模型列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "正整数，表示查询第几页", required = true, dataType = "int", example = "1"),
        @ApiImplicitParam(name = "pageSize", value = "正整数，表示每页几条记录", required = true, dataType = "int",
            example = "10")})
    @PreAuthorize("@ss.hasPermission('iot:model:list')")
    @GetMapping("find/{pageNum}/{pageSize}")
    public R<IPage<ThingsModel>> find(ThingsModel thingsModel, @PathVariable("pageNum") Integer pageNum,
        @PathVariable("pageSize") Integer pageSize) {
        return R.success(this.thingsModelService.find(thingsModel, pageNum, pageSize));
    }

    @PreAuthorize("@ss.hasPermission('iot:model:list')")
    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("查询产品物模型列表")
    public R<List<ThingsModel>> list(ThingsModel thingsModel) {
        return R.success(this.thingsModelService.list(thingsModel));
    }

    /**
     * 获取物模型详细信息
     */
    @PreAuthorize("@ss.hasPermission('iot:model:query')")
    @RequestMapping(value = "/getDetail", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("查询产品物模型详情")
    public R<ThingsModel> getInfo(@RequestParam Long modelId) {
        return R.success(this.thingsModelService.getDetail(modelId));
    }

    /**
     * 添加产品物模型
     */
    @PreAuthorize("@ss.hasPermission('iot:model:add')")
    @Log(title = "物模型", businessType = BusinessType.INSERT)
    @RequestMapping(value = "/add", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("添加产品物模型")
    public R<String> add(@Validated(Insert.class) ThingsModel thingsModel) {
        this.thingsModelService.add(thingsModel);
        return R.success("添加产品物模型成功");
    }

    /**
     * 修改物模型
     */
    @PreAuthorize("@ss.hasPermission('iot:model:update')")
    @Log(title = "物模型", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("修改产品物模型")
    public R<String> update(@Validated(Update.class) ThingsModel thingsModel) {
        this.thingsModelService.update(thingsModel);
        return R.success("修改产品物模型成功");
    }

    /**
     * 删除物模型
     */
    @PreAuthorize("@ss.hasPermission('iot:model:delete')")
    @Log(title = "物模型", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("批量删除产品物模型")
    public R<String> delete(@RequestParam List<Long> modelIds) {
        this.thingsModelService.delete(modelIds);
        return R.success("删除产品物模型成功");
    }

    /**
     * 获取缓存的JSON物模型
     */
    @PreAuthorize("@ss.hasPermission('iot:model:query')")
    @RequestMapping(value = "/getCache", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("获取缓存的产品物模型(JSON格式)")
    public R<String> getThingsModelCache(@RequestParam Long productId) {
        return R.success(this.thingsModelService.getThingsModelCache(productId));
    }
}
