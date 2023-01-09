package com.xjhqre.admin.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.annotation.Log;
import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.system.domain.entity.Config;
import com.xjhqre.system.service.ConfigService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 参数配置 信息操作处理
 *
 * @author xjhqre
 */
@RestController
@RequestMapping("/system/config")
@Api(value = "配置操作接口", tags = "配置操作接口")
public class ConfigController extends BaseController {
    @Autowired
    private ConfigService configService;

    @ApiOperation(value = "分页查询配置信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "正整数，表示查询第几页", required = true, dataType = "int", example = "1"),
        @ApiImplicitParam(name = "pageSize", value = "正整数，表示每页几条记录", required = true, dataType = "int",
            example = "10")})
    @PreAuthorize("@ss.hasPermission('system:config:list')")
    @GetMapping("findConfig/{pageNum}/{pageSize}")
    public R<IPage<Config>> findConfig(Config config, @PathVariable("pageNum") Integer pageNum,
        @PathVariable("pageSize") Integer pageSize) {
        return R.success(this.configService.findConfig(config, pageNum, pageSize));
    }

    /**
     * 根据参数编号获取详细信息
     */
    @ApiOperation(value = "根据参数编号获取详细信息")
    @PreAuthorize("@ss.hasPermission('system:config:query')")
    @GetMapping(value = "/{configId}")
    public R<Config> getInfo(@PathVariable Long configId) {
        return R.success(this.configService.getById(configId));
    }

    /**
     * 根据参数键名查询参数值
     */
    @ApiOperation(value = "根据参数键名查询参数值")
    @GetMapping(value = "/configKey/{configKey}")
    public R<String> getConfigKey(@PathVariable String configKey) {
        return R.success(this.configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置
     */
    @ApiOperation(value = "新增参数配置")
    @PreAuthorize("@ss.hasPermission('system:config:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R<String> add(@Validated @RequestBody Config config) {
        if (Constants.NOT_UNIQUE.equals(this.configService.checkConfigKeyUnique(config.getConfigKey()))) {
            return R.error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setCreateBy(this.getUsername());
        this.configService.insertConfig(config);
        return R.success("新增参数成功");
    }

    /**
     * 修改参数配置
     */
    @ApiOperation(value = "修改参数配置")
    @PreAuthorize("@ss.hasPermission('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<String> edit(@Validated @RequestBody Config config) {
        if (Constants.NOT_UNIQUE.equals(this.configService.checkConfigKeyUnique(config.getConfigKey()))) {
            return R.error("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setUpdateBy(this.getUsername());
        this.configService.updateConfig(config);
        return R.success("修改参数成功");
    }

    /**
     * 删除参数配置
     */
    @ApiOperation(value = "删除参数配置")
    @PreAuthorize("@ss.hasPermission('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public R<String> remove(@PathVariable Long[] configIds) {
        this.configService.deleteConfigByIds(configIds);
        return R.success("删除参数成功");
    }

    /**
     * 刷新参数缓存
     */
    @ApiOperation(value = "刷新参数缓存")
    @PreAuthorize("@ss.hasPermission('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public R<String> refreshCache() {
        this.configService.resetConfigCache();
        return R.success("刷新参数缓存成功");
    }
}
