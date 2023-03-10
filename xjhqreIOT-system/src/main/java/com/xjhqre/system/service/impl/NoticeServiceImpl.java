package com.xjhqre.system.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.system.domain.entity.Notice;
import com.xjhqre.system.mapper.NoticeMapper;
import com.xjhqre.system.service.NoticeService;

/**
 * 公告 服务层实现
 * 
 * @author xjhqre
 * @since 2022-12-26
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class NoticeServiceImpl implements NoticeService {
    @Resource
    private NoticeMapper noticeMapper;

    /**
     * 查询公告详情
     *
     */
    @Override
    public Notice getDetail(Long noticeId) {
        return this.noticeMapper.selectById(noticeId);
    }

    @Override
    public IPage<Notice> find(Notice notice, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(notice.getNoticeId() != null, Notice::getNoticeId, notice.getNoticeId())
            .like(notice.getNoticeTitle() != null && !"".equals(notice.getNoticeTitle()), Notice::getNoticeTitle,
                notice.getNoticeTitle())
            .eq(notice.getNoticeType() != null && !"".equals(notice.getNoticeType()), Notice::getNoticeType,
                notice.getNoticeType());
        return this.noticeMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 查询公告列表
     * 
     * @param notice
     *            公告信息
     * @return 公告集合
     */
    @Override
    public List<Notice> selectNoticeList(Notice notice) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(notice.getNoticeId() != null, Notice::getNoticeId, notice.getNoticeId())
            .like(notice.getNoticeTitle() != null && !"".equals(notice.getNoticeTitle()), Notice::getNoticeTitle,
                notice.getNoticeType())
            .eq(notice.getNoticeType() != null && !"".equals(notice.getNoticeType()), Notice::getNoticeType,
                notice.getNoticeType());
        return this.noticeMapper.selectList(wrapper);
    }

    /**
     * 新增公告
     * 
     */
    @Override
    public void add(Notice notice) {
        notice.setCreateBy(SecurityUtils.getUsername());
        notice.setCreateTime(DateUtils.getNowDate());
        this.noticeMapper.insert(notice);
    }

    /**
     * 修改公告
     *
     */
    @Override
    public void update(Notice notice) {
        notice.setUpdateBy(SecurityUtils.getUsername());
        notice.setUpdateTime(DateUtils.getNowDate());
        this.noticeMapper.updateById(notice);
    }

    /**
     * 批量删除公告信息
     *
     */
    @Override
    public void delete(List<Long> noticeIds) {
        this.noticeMapper.deleteBatchIds(noticeIds);
    }
}
