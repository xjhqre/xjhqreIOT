package com.xjhqre.admin.controller.system;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.annotation.Log;
import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.domain.entity.DictData;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.system.service.DictDataService;
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
@RequestMapping("/system/dictData")
@Api(value = "字典数据操作接口", tags = "字典数据操作接口")
public class DictDataController extends BaseController {
    @Autowired
    private DictDataService dictDataService;

    @Autowired
    private DictTypeService dictTypeService;

    @ApiOperation(value = "分页查询字典数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "正整数，表示查询第几页", required = true, dataType = "int", example = "1"),
        @ApiImplicitParam(name = "pageSize", value = "正整数，表示每页几条记录", required = true, dataType = "int",
            example = "10")})
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    @GetMapping("findDictData/{pageNum}/{pageSize}")
    public R<IPage<DictData>> find(DictData dictData, @PathVariable("pageNum") Integer pageNum,
        @PathVariable("pageSize") Integer pageSize) {
        return R.success(this.dictDataService.find(dictData, pageNum, pageSize));
    }

    @ApiOperation(value = "查询字典数据列表")
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    public R<List<DictData>> list(DictData dictData) {
        return R.success(this.dictDataService.list(dictData));
    }

    /**
     * 查询字典数据详细
     */
    @ApiOperation(value = "查询字典数据详情")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    @RequestMapping(value = "/getDetail", method = {RequestMethod.POST, RequestMethod.GET})
    public R<DictData> getDetail(@RequestParam Long dictCode) {
        return R.success(this.dictDataService.getDetail(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @ApiOperation(value = "根据字典类型查询字典数据信息")
    @GetMapping(value = "/{dictType}")
    public R<List<DictData>> dictType(@PathVariable String dictType) {
        List<DictData> data = this.dictTypeService.selectDictDataByType(dictType);
        if (StringUtils.isNull(data)) {
            data = new ArrayList<>();
        }
        return R.success(data);
    }

    /**
     * 新增字典数据
     */
    @ApiOperation(value = "新增字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:add')")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public R<String> add(@Validated @RequestBody DictData dict) {
        dict.setCreateBy(this.getUsername());
        this.dictDataService.insertDictData(dict);
        return R.success("新增字典数据成功");
    }

    /**
     * 修改保存字典数据
     */
    @ApiOperation(value = "修改保存字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:edit')")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<String> edit(@Validated @RequestBody DictData dict) {
        dict.setUpdateBy(this.getUsername());
        this.dictDataService.updateDictData(dict);
        return R.success("修改字典数据成功");
    }

    /**
     * 删除字典数据
     */
    @ApiOperation(value = "删除字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public R<String> remove(@PathVariable Long[] dictCodes) {
        this.dictDataService.deleteDictDataByIds(dictCodes);
        return R.success("删除字典数据成功");
    }
}
