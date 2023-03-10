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
import com.xjhqre.common.enums.BusinessType;
import com.xjhqre.common.group.Insert;
import com.xjhqre.common.group.Update;
import com.xjhqre.system.domain.entity.Notice;
import com.xjhqre.system.service.NoticeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 公告 信息操作处理
 *
 * @author xjhqre
 */
@RestController
@Api(value = "公告操作接口", tags = "公告操作接口")
@RequestMapping("/system/notice")
public class NoticeController extends BaseController {

    @Resource
    private NoticeService noticeService;

    @ApiOperation(value = "分页查询公告信息")
    @PreAuthorize("@ss.hasPermission('system:notice:list')")
    @GetMapping("/find")
    public R<IPage<Notice>> find(Notice notice, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return R.success(this.noticeService.find(notice, pageNum, pageSize));
    }

    /**
     * 根据通知公告编号获取详细信息
     */
    @ApiOperation(value = "根据通知公告编号获取详细信息")
    @PreAuthorize("@ss.hasPermission('system:notice:query')")
    @GetMapping(value = "/getDetail")
    public R<Notice> getDetail(@RequestParam Long noticeId) {
        return R.success(this.noticeService.getDetail(noticeId));
    }

    /**
     * 新增通知公告
     */
    @ApiOperation(value = "新增通知公告")
    @PreAuthorize("@ss.hasPermission('system:notice:add')")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<String> add(@Validated(Insert.class) @RequestBody Notice notice) {
        this.noticeService.add(notice);
        return R.success("新增通知公告成功");
    }

    /**
     * 修改通知公告
     */
    @ApiOperation(value = "修改通知公告")
    @PreAuthorize("@ss.hasPermission('system:notice:update')")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public R<String> update(@Validated(Update.class) @RequestBody Notice notice) {
        this.noticeService.update(notice);
        return R.success("修改通知公告");
    }

    /**
     * 删除通知公告
     */
    @ApiOperation(value = "删除通知公告")
    @PreAuthorize("@ss.hasPermission('system:notice:delete')")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/delete/{noticeIds}", method = {RequestMethod.POST, RequestMethod.GET})
    public R<String> delete(@PathVariable List<Long> noticeIds) {
        this.noticeService.delete(noticeIds);
        return R.success("删除通知公告成功");
    }
}
