package com.xjhqre.admin.controller.system;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
import com.xjhqre.common.domain.R;
import com.xjhqre.common.domain.entity.DictData;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.common.group.Insert;
import com.xjhqre.common.group.Update;
import com.xjhqre.system.service.DictDataService;
import com.xjhqre.system.service.DictTypeService;

import io.swagger.annotations.Api;
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

    @Resource
    private DictDataService dictDataService;

    @Resource
    private DictTypeService dictTypeService;

    @ApiOperation(value = "分页查询字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    @GetMapping("/find")
    public R<IPage<DictData>> find(DictData dictData, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
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
    public R<List<DictData>> getByDictType(@PathVariable String dictType) {
        List<DictData> data = this.dictTypeService.getByDictType(dictType);
        return R.success(data);
    }

    /**
     * 新增字典数据
     */
    @ApiOperation(value = "新增字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:add')")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<String> add(@Validated(Insert.class) @RequestBody DictData dictData) {
        this.dictDataService.add(dictData);
        return R.success("新增字典数据成功");
    }

    /**
     * 修改保存字典数据
     */
    @ApiOperation(value = "修改保存字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:update')")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public R<String> update(@Validated(Update.class) @RequestBody DictData dictData) {
        this.dictDataService.update(dictData);
        return R.success("修改字典数据成功");
    }

    /**
     * 删除字典数据
     */
    @ApiOperation(value = "删除字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:delete')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/delete/{dictCodes}", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> delete(@PathVariable List<Long> dictCodes) {
        this.dictDataService.delete(dictCodes);
        return R.success("删除字典数据成功");
    }
}
