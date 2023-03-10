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
import com.xjhqre.iot.domain.dto.UpdateDeviceGroupsDTO;
import com.xjhqre.iot.domain.entity.Group;
import com.xjhqre.iot.service.GroupService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 分组接口
 *
 * @author xjhqre
 * @since 2023-1-5
 */
@Api(tags = "设备分组接口")
@RestController
@RequestMapping("/iot/group")
public class GroupController extends BaseController {
    @Resource
    private GroupService groupService;

    /**
     * 分页查询分组列表
     */
    @ApiOperation(value = "分页查询分组列表")
    @PreAuthorize("@ss.hasPermission('iot:group:list')")
    @GetMapping("/find")
    public R<IPage<Group>> find(Group group, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return R.success(this.groupService.find(group, pageNum, pageSize));
    }

    /**
     * 获取分组详细信息
     */
    @PreAuthorize("@ss.hasPermission('iot:group:query')")
    @GetMapping(value = "/{groupId}")
    @RequestMapping(value = "/getDetail", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("获取分组详情")
    public R<Group> getDetail(@RequestParam Long groupId) {
        return R.success(this.groupService.getDetail(groupId));
    }

    /**
     * 获取分组下的所有关联设备ID数组
     */
    @ApiOperation("获取分组下所有关联的设备ID数组")
    @PreAuthorize("@ss.hasPermission('iot:group:query')")
    @RequestMapping(value = "/getDeviceIds", method = {RequestMethod.POST, RequestMethod.GET})
    public R<List<Long>> getDeviceIds(@RequestParam Long groupId) {
        return R.success(this.groupService.getDeviceIds(groupId));
    }

    /**
     * 新增分组
     */
    @ApiOperation("添加分组")
    @PreAuthorize("@ss.hasPermission('iot:group:add')")
    @Log(title = "分组", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<String> add(@Validated(Insert.class) @RequestBody Group group) {
        this.groupService.add(group);
        return R.success("添加分组成功");
    }

    /**
     * 更新分组下的关联设备
     */
    @ApiOperation("更新分组下的关联设备")
    @PreAuthorize("@ss.hasPermission('iot:group:update')")
    @Log(title = "设备分组", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/updateDeviceGroups")
    public R<String> updateDeviceGroups(@RequestBody UpdateDeviceGroupsDTO dto) {
        this.groupService.updateDeviceGroups(dto);
        return R.success("更新成功");
    }

    /**
     * 修改分组
     */
    @ApiOperation("修改分组")
    @PreAuthorize("@ss.hasPermission('iot:group:update')")
    @Log(title = "分组", businessType = BusinessType.UPDATE)
    @PutMapping("/update")
    public R<String> update(@Validated(Update.class) @RequestBody Group group) {
        this.groupService.update(group);
        return R.success("修改成功");
    }

    /**
     * 删除设备分组
     */
    @PreAuthorize("@ss.hasPermission('iot:group:delete')")
    @Log(title = "分组", businessType = BusinessType.DELETE)
    @DeleteMapping("/delete/{groupIds}")
    @ApiOperation("批量删除设备分组")
    public R<String> delete(@PathVariable List<Long> groupIds) {
        this.groupService.delete(groupIds);
        return R.success("删除分组成功");
    }
}
