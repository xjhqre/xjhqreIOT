package com.xjhqre.admin.controller.system;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.annotation.Log;
import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.common.group.Insert;
import com.xjhqre.common.group.Update;
import com.xjhqre.system.domain.entity.Config;
import com.xjhqre.system.service.ConfigService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 参数配置 信息操作处理
 *
 * @author xjhqre
 * @since 2022-12-16
 */
@RestController
@RequestMapping("/system/config")
@Api(value = "配置操作接口", tags = "配置操作接口")
public class ConfigController extends BaseController {

    @Resource
    private ConfigService configService;

    @ApiOperation(value = "分页查询配置信息")
    @PreAuthorize("@ss.hasPermission('system:config:list')")
    @GetMapping("/find")
    public R<IPage<Config>> find(Config config, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return R.success(this.configService.find(config, pageNum, pageSize));
    }

    /**
     * 根据参数编号获取详细信息
     */
    @ApiOperation(value = "根据参数编号获取详细信息")
    @PreAuthorize("@ss.hasPermission('system:config:query')")
    @GetMapping(value = "/getDetail")
    public R<Config> getDetail(@RequestParam Long configId) {
        return R.success(this.configService.getById(configId));
    }

    /**
     * 根据参数键名查询参数值
     */
    @ApiOperation(value = "根据参数键名查询参数值")
    @GetMapping(value = "/getByConfigKey")
    public R<String> getByConfigKey(@RequestParam String configKey) {
        return R.success(this.configService.getByConfigKey(configKey));
    }

    /**
     * 新增参数配置
     */
    @ApiOperation(value = "新增参数配置")
    @PreAuthorize("@ss.hasPermission('system:config:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<String> add(@Validated(Insert.class) @RequestBody Config config) {
        if (Constants.NOT_UNIQUE
            .equals(this.configService.checkConfigKeyUnique(config.getConfigKey(), config.getConfigId()))) {
            return R.error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        this.configService.add(config);
        return R.success("新增参数成功");
    }

    /**
     * 修改参数配置
     */
    @ApiOperation(value = "修改参数配置")
    @PreAuthorize("@ss.hasPermission('system:config:update')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public R<String> update(@Validated(Update.class) @RequestBody Config config) {
        if (Constants.NOT_UNIQUE
            .equals(this.configService.checkConfigKeyUnique(config.getConfigKey(), config.getConfigId()))) {
            return R.error("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        this.configService.update(config);
        return R.success("修改参数成功");
    }

    /**
     * 删除参数配置
     */
    @ApiOperation(value = "删除参数配置")
    @PreAuthorize("@ss.hasPermission('system:config:delete')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/delete/{configIds}", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> delete(@PathVariable List<Long> configIds) {
        this.configService.delete(configIds);
        return R.success("删除参数成功");
    }

    /**
     * 刷新参数缓存
     */
    @ApiOperation(value = "刷新参数缓存")
    @PreAuthorize("@ss.hasPermission('system:config:delete')")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public R<String> refreshCache() {
        this.configService.resetConfigCache();
        return R.success("刷新参数缓存成功");
    }
}
