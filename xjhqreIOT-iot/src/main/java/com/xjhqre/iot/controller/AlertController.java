package com.xjhqre.iot.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.xjhqre.iot.domain.entity.Alert;
import com.xjhqre.iot.service.AlertService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 产品告警设置接口
 *
 * @author xjhqre
 * @since 2023-1-6
 */
@Api(tags = "产品告警设置接口")
@RestController
@RequestMapping("/iot/alert")
public class AlertController extends BaseController {
    @Resource
    private AlertService alertService;

    @ApiOperation(value = "分页查询产品告警设置列表")
    @PreAuthorize("@ss.hasPermission('iot:alert:list')")
    @GetMapping("/find")
    public R<IPage<Alert>> find(Alert alert, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return R.success(this.alertService.find(alert, pageNum, pageSize));
    }

    /**
     * 获取产品告警设置详情
     */
    @PreAuthorize("@ss.hasPermission('iot:alert:query')")
    @RequestMapping(value = "/getDetail", method = {RequestMethod.POST, RequestMethod.GET})
    public R<Alert> getDetail(@RequestParam Long alertId) {
        return R.success(this.alertService.getDetail(alertId));
    }

    /**
     * 添加产品告警设置
     */
    @PreAuthorize("@ss.hasPermission('iot:alert:add')")
    @Log(title = "产品告警", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<String> add(@Validated(Insert.class) @RequestBody Alert alert) {
        this.alertService.add(alert);
        return R.success("添加产品告警设置成功");
    }

    /**
     * 修改产品告警设置
     */
    @PreAuthorize("@ss.hasPermission('iot:alert:update')")
    @Log(title = "产品告警", businessType = BusinessType.UPDATE)
    @PutMapping("/update")
    public R<String> update(@Validated(Update.class) @RequestBody Alert alert) {
        this.alertService.update(alert);
        return R.success("修改产品告警设置成功");
    }

    /**
     * 删除产品告警设置
     */
    @PreAuthorize("@ss.hasPermission('iot:alert:delete')")
    @Log(title = "产品告警", businessType = BusinessType.DELETE)
    @DeleteMapping(value = "/delete/{alertIds}")
    public R<String> delete(@PathVariable List<Long> alertIds) {
        this.alertService.delete(alertIds);
        return R.success("删除产品告警设置成功");
    }
}
