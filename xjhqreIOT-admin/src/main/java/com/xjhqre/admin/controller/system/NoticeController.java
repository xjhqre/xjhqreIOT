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
import com.xjhqre.common.domain.R;
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.system.domain.entity.Notice;
import com.xjhqre.system.service.NoticeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 公告 信息操作处理
 *
 * @author xjhqre
 */
@RestController
@Api(value = "公告操作接口", tags = "公告操作接口")
@RequestMapping("/admin/system/notice")
public class NoticeController extends BaseController {
    @Autowired
    private NoticeService noticeService;

    @ApiOperation(value = "分页查询公告信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "正整数，表示查询第几页", required = true, dataType = "int", example = "1"),
        @ApiImplicitParam(name = "pageSize", value = "正整数，表示每页几条记录", required = true, dataType = "int",
            example = "20")})
    @GetMapping("list/{pageNum}/{pageSize}")
    @PreAuthorize("@ss.hasPermission('system:notice:list')")
    public R<IPage<Notice>> findNotice(Notice notice, @PathVariable("pageNum") Integer pageNum,
        @PathVariable("pageSize") Integer pageSize) {
        return R.success(this.noticeService.findNotice(notice, pageNum, pageSize));
    }

    /**
     * 根据通知公告编号获取详细信息
     */
    @ApiOperation(value = "根据通知公告编号获取详细信息")
    @PreAuthorize("@ss.hasPermission('system:notice:query')")
    @GetMapping(value = "/{noticeId}")
    public R<Notice> getInfo(@PathVariable Long noticeId) {
        return R.success(this.noticeService.selectNoticeById(noticeId));
    }

    /**
     * 新增通知公告
     */
    @ApiOperation(value = "新增通知公告")
    @PreAuthorize("@ss.hasPermission('system:notice:add')")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping
    public R<String> add(@Validated @RequestBody Notice notice) {
        notice.setCreateBy(this.getUsername());
        this.noticeService.insertNotice(notice);
        return R.success("新增通知公告成功");
    }

    /**
     * 修改通知公告
     */
    @ApiOperation(value = "修改通知公告")
    @PreAuthorize("@ss.hasPermission('system:notice:edit')")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<String> edit(@Validated @RequestBody Notice notice) {
        notice.setUpdateBy(this.getUsername());
        this.noticeService.updateNotice(notice);
        return R.success("修改通知公告");
    }

    /**
     * 删除通知公告
     */
    @ApiOperation(value = "删除通知公告")
    @PreAuthorize("@ss.hasPermission('system:notice:remove')")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    public R<String> remove(@PathVariable Long[] noticeIds) {
        this.noticeService.deleteNoticeByIds(noticeIds);
        return R.success("删除通知公告成功");
    }
}
