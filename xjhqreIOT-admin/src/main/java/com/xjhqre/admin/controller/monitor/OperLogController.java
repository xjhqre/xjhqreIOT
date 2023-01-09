package com.xjhqre.admin.controller.monitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.annotation.Log;
import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.system.domain.entity.OperLog;
import com.xjhqre.system.service.OperLogService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 操作日志记录
 *
 * @author xjhqre
 */
@RestController
@Api(value = "操作日志记录", tags = "操作日志记录")
@RequestMapping("/monitor/operLog")
public class OperLogController extends BaseController {
    @Autowired
    private OperLogService operLogService;

    @ApiOperation(value = "分页查询操作日志记录")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "正整数，表示查询第几页", required = true, dataType = "int", example = "1"),
        @ApiImplicitParam(name = "pageSize", value = "正整数，表示每页几条记录", required = true, dataType = "int",
            example = "10")})
    @PreAuthorize("@ss.hasPermission('monitor:operlog:list')")
    @GetMapping("list/{pageNum}/{pageSize}")
    public R<IPage<OperLog>> listLoginInfo(OperLog operLog, @PathVariable("pageNum") Integer pageNum,
        @PathVariable("pageSize") Integer pageSize) {
        return R.success(this.operLogService.findOperLog(operLog, pageNum, pageSize));
    }

    @ApiOperation(value = "查看操作日志详情")
    @PreAuthorize("@ss.hasPermission('monitor:operlog:query')")
    @GetMapping("/{operId}")
    public R<OperLog> getInfo(@PathVariable Long operId) {
        OperLog info = this.operLogService.getInfo(operId);
        return R.success(info);
    }

    @ApiOperation(value = "根据条件删除对应操作记录")
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermission('monitor:operlog:remove')")
    @DeleteMapping("/{operIds}")
    public R<String> remove(@PathVariable Long[] operIds) {
        this.operLogService.deleteOperLogByIds(operIds);
        return R.success("删除日志成功");
    }

    @ApiOperation(value = "清空操作日志")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("@ss.hasPermission('monitor:operlog:remove')")
    @DeleteMapping("/clean")
    public R<String> clean() {
        this.operLogService.cleanOperLog();
        return R.success("清空日志成功");
    }
}
