package com.xjhqre.admin.controller.system;

import java.util.List;

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
import com.xjhqre.common.domain.entity.DictType;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.system.service.DictTypeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
    @Autowired
    private DictTypeService dictTypeService;

    @ApiOperation(value = "分页查询字典类型")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "正整数，表示查询第几页", required = true, dataType = "int", example = "1"),
        @ApiImplicitParam(name = "pageSize", value = "正整数，表示每页几条记录", required = true, dataType = "int",
            example = "10")})
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    @GetMapping("findDictType/{pageNum}/{pageSize}")
    public R<IPage<DictType>> findDictType(DictType dictType, @PathVariable("pageNum") Integer pageNum,
        @PathVariable("pageSize") Integer pageSize) {
        return R.success(this.dictTypeService.findDictType(dictType, pageNum, pageSize));
    }

    /**
     * 查询字典类型详细
     */
    @ApiOperation(value = "查询字典类型详细")
    @PreAuthorize("@ss. hasPermission('system:dict:query')")
    @GetMapping(value = "/{dictId}")
    public R<DictType> getInfo(@PathVariable Long dictId) {
        DictType dictType = this.dictTypeService.selectDictTypeById(dictId);
        return R.success(dictType);
    }

    /**
     * 新增字典类型
     */
    @ApiOperation(value = "新增字典类型")
    @PreAuthorize("@ss. hasPermission('system:dict:add')")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PostMapping
    public R<String> add(@Validated @RequestBody DictType dict) {
        if (Constants.NOT_UNIQUE.equals(this.dictTypeService.checkDictTypeUnique(dict))) {
            return R.error("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setCreateBy(this.getUsername());
        this.dictTypeService.insertDictType(dict);
        return R.success("新增字典'" + dict.getDictName() + "'成功");
    }

    /**
     * 修改字典类型
     */
    @ApiOperation(value = "修改字典类型")
    @PreAuthorize("@ss. hasPermission('system:dict:edit')")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<String> edit(@Validated @RequestBody DictType dict) {
        if (Constants.NOT_UNIQUE.equals(this.dictTypeService.checkDictTypeUnique(dict))) {
            return R.error("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setUpdateBy(this.getUsername());
        this.dictTypeService.updateDictType(dict);
        return R.success("修改字典'" + dict.getDictName() + "'成功");
    }

    /**
     * 删除字典类型
     */
    @ApiOperation(value = "删除字典类型")
    @PreAuthorize("@ss. hasPermission('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictIds}")
    public R<String> remove(@PathVariable Long[] dictIds) {
        this.dictTypeService.deleteDictTypeByIds(dictIds);
        return R.success("删除字典类型成功");
    }

    /**
     * 刷新字典缓存
     */
    @ApiOperation(value = "刷新字典缓存")
    @PreAuthorize("@ss. hasPermission('system:dict:remove')")
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
