package com.xjhqre.iot.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import com.xjhqre.common.group.Insert;
import com.xjhqre.common.group.Update;
import com.xjhqre.iot.domain.entity.Product;
import com.xjhqre.iot.service.ProductService;

import io.swagger.annotations.Api;
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
    @PreAuthorize("@ss.hasPermission('iot:product:list')")
    @GetMapping("/find")
    public R<IPage<Product>> find(Product product, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
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

    @PreAuthorize("@ss.hasPermission('iot:product:query')")
    @RequestMapping(value = "/getByDeviceId", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("根据设备id获取产品")
    public R<Product> getByDeviceId(Long deviceId) {
        return R.success(this.productService.getByDeviceId(deviceId));
    }

    /**
     * 新增产品
     */
    @PreAuthorize("@ss.hasPermission('iot:product:add')")
    @Log(title = "产品", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ApiOperation("添加产品")

    public R<String> add(@Validated(Insert.class) @RequestBody Product product) {
        this.productService.add(product);
        return R.success("添加产品成功", "添加产品成功");
    }

    /**
     * 修改产品
     */
    @PreAuthorize("@ss.hasPermission('iot:product:update')")
    @Log(title = "产品", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    @ApiOperation("修改产品")
    public R<String> update(@Validated(Update.class) @RequestBody Product product) {
        this.productService.update(product);
        return R.success("修改产品成功", "修改产品成功");
    }

    /**
     * 发布产品
     */
    @PreAuthorize("@ss.hasPermission('iot:product:update')")
    @Log(title = "更新产品状态", businessType = BusinessType.UPDATE)
    @ApiOperation("更新产品状态")
    @PutMapping("/status")
    public R<String> changeProductStatus(@RequestParam Long productId, @RequestParam Integer status) {
        this.productService.changeProductStatus(productId, status);
        return R.success("更新产品状态成功", "更新产品状态成功");
    }

    /**
     * 删除产品
     */
    @PreAuthorize("@ss.hasPermission('iot:product:delete')")
    @Log(title = "产品", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/delete/{productIds}", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("批量删除产品")
    public R<String> delete(@PathVariable Long[] productIds) {
        this.productService.delete(productIds);
        return R.success("删除产品成功", "删除产品成功");
    }
}
