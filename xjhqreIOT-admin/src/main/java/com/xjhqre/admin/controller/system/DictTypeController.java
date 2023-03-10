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
import com.xjhqre.common.domain.entity.DictType;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.common.group.Insert;
import com.xjhqre.system.service.DictTypeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 数据字典信息
 *
 * @author xjhqre
 */
@RestController
@RequestMapping("/system/dictType")
@Api(value = "字典类型操作接口", tags = "字典类型操作接口")
public class DictTypeController extends BaseController {

    @Resource
    private DictTypeService dictTypeService;

    @ApiOperation(value = "分页查询字典类型")
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    @GetMapping("/find")
    public R<IPage<DictType>> find(DictType dictType, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return R.success(this.dictTypeService.find(dictType, pageNum, pageSize));
    }

    @ApiOperation(value = "查询字典类型列表")
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    @GetMapping("/list")
    public R<List<DictType>> list(DictType dictType) {
        return R.success(this.dictTypeService.list(dictType));
    }

    /**
     * 查询字典类型详细
     */
    @ApiOperation(value = "查询字典类型详细")
    @PreAuthorize("@ss. hasPermission('system:dict:query')")
    @GetMapping(value = "/getDetail")
    public R<DictType> getDetail(@RequestParam Long dictId) {
        DictType dictType = this.dictTypeService.getDetail(dictId);
        return R.success(dictType);
    }

    /**
     * 新增字典类型
     */
    @ApiOperation(value = "新增字典类型")
    @PreAuthorize("@ss. hasPermission('system:dict:add')")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<String> add(@Validated(Insert.class) @RequestBody DictType dictType) {
        if (Constants.NOT_UNIQUE.equals(this.dictTypeService.checkDictTypeUnique(dictType))) {
            return R.error("新增字典'" + dictType.getDictName() + "'失败，字典类型已存在");
        }
        this.dictTypeService.add(dictType);
        return R.success("新增字典'" + dictType.getDictName() + "'成功");
    }

    /**
     * 修改字典类型
     */
    @ApiOperation(value = "修改字典类型")
    @PreAuthorize("@ss. hasPermission('system:dict:update')")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public R<String> update(@Validated @RequestBody DictType dict) {
        if (Constants.NOT_UNIQUE.equals(this.dictTypeService.checkDictTypeUnique(dict))) {
            return R.error("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        this.dictTypeService.update(dict);
        return R.success("修改字典'" + dict.getDictName() + "'成功");
    }

    /**
     * 删除字典类型
     */
    @ApiOperation(value = "删除字典类型")
    @PreAuthorize("@ss. hasPermission('system:dict:delete')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/delete/{dictIds}", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> delete(@PathVariable List<Long> dictIds) {
        this.dictTypeService.delete(dictIds);
        return R.success("删除字典类型成功");
    }

    /**
     * 刷新字典缓存
     */
    @ApiOperation(value = "刷新字典缓存")
    @PreAuthorize("@ss. hasPermission('system:dict:delete')")
    @Log(title = "字典类型", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public R<String> refreshCache() {
        this.dictTypeService.resetDictCache();
        return R.success("刷新字典缓存成功");
    }

    /**
     * 获取字典选择框列表
     */
    @ApiOperation(value = "获取字典选择框列表")
    @GetMapping("/optionSelect")
    public R<List<DictType>> optionSelect() {
        List<DictType> dictTypes = this.dictTypeService.selectDictTypeAll();
        return R.success(dictTypes);
    }
}
