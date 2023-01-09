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
import com.xjhqre.iot.domain.entity.Product;
import com.xjhqre.iot.service.ProductService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 产品操作接口
 * </p>
 *
 * @author xjhqre
 * @since 12月 19, 2022
 */
@Api(tags = "产品操作接口")
@RestController
@RequestMapping("/iot/product")
public class ProductController extends BaseController {

    @Resource
    private ProductService productService;

    @ApiOperation(value = "产品分页列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "正整数，表示查询第几页", required = true, dataType = "int", example = "1"),
        @ApiImplicitParam(name = "pageSize", value = "正整数，表示每页几条记录", required = true, dataType = "int",
            example = "10")})
    @PreAuthorize("@ss.hasPermission('iot:product:list')")
    @GetMapping("find/{pageNum}/{pageSize}")
    public R<IPage<Product>> find(Product product, @PathVariable("pageNum") Integer pageNum,
        @PathVariable("pageSize") Integer pageSize) {
        return R.success(this.productService.find(product, pageNum, pageSize));
    }

    @PreAuthorize("@ss.hasPermission('iot:product:list')")
    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("产品列表查询")
    public R<List<Product>> list(Product product) {
        return R.success(this.productService.list(product));
    }

    /**
     * 获取产品详细信息
     */
    @PreAuthorize("@ss.hasPermission('iot:product:query')")
    @RequestMapping(value = "/getDetail", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("获取产品详情")
    public R<Product> getDetail(@RequestParam Long productId) {
        return R.success(this.productService.getDetail(productId));
    }

    /**
     * 新增产品
     */
    @PreAuthorize("@ss.hasPermission('iot:product:add')")
    @Log(title = "产品", businessType = BusinessType.INSERT)
    @RequestMapping(value = "/add", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("添加产品")
    public void add(@Validated Product product) {
        this.productService.add(product);
    }

    /**
     * 修改产品
     */
    @PreAuthorize("@ss.hasPermission('iot:product:update')")
    @Log(title = "产品", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("修改产品")
    public void update(@Validated Product product) {
        this.productService.update(product);
    }

    /**
     * 发布产品
     */
    @PreAuthorize("@ss.hasPermission('iot:product:update')")
    @Log(title = "更新产品状态", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/status", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("更新产品状态")
    public void changeProductStatus(@RequestParam Long productId, @RequestParam Integer status) {
        this.productService.changeProductStatus(productId, status);
    }

    /**
     * 删除产品
     */
    @PreAuthorize("@ss.hasPermission('iot:product:delete')")
    @Log(title = "产品", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("批量删除产品")
    public void delete(@RequestParam Long[] productIds) {
        this.productService.delete(productIds);
    }
}
