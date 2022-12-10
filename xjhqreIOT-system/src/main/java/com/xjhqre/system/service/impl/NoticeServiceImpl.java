package com.xjhqre.system.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjhqre.system.domain.entity.Notice;
import com.xjhqre.system.mapper.NoticeMapper;
import com.xjhqre.system.service.NoticeService;

/**
 * 公告 服务层实现
 * 
 * @author xjhqre
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class NoticeServiceImpl implements NoticeService {
    @Autowired
    private NoticeMapper noticeMapper;

    /**
     * 查询公告信息
     * 
     * @param noticeId
     *            公告ID
     * @return 公告信息
     */
    @Override
    public Notice selectNoticeById(Long noticeId) {
        return this.noticeMapper.selectById(noticeId);
    }

    @Override
    public IPage<Notice> findNotice(Notice notice, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(notice.getNoticeId() != null, Notice::getNoticeId, notice.getNoticeId())
            .like(notice.getNoticeTitle() != null, Notice::getNoticeTitle, notice.getNoticeType())
            .eq(notice.getNoticeType() != null, Notice::getNoticeType, notice.getNoticeType());
        return this.noticeMapper.selectPage(new Page<Notice>(pageNum, pageSize), wrapper);
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
            .like(notice.getNoticeTitle() != null, Notice::getNoticeTitle, notice.getNoticeType())
            .eq(notice.getNoticeType() != null, Notice::getNoticeType, notice.getNoticeType());
        return this.noticeMapper.selectList(wrapper);
    }

    /**
     * 新增公告
     * 
     * @param notice
     *            公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(Notice notice) {
        return this.noticeMapper.insert(notice);
    }

    /**
     * 修改公告
     * 
     * @param notice
     *            公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(Notice notice) {
        return this.noticeMapper.updateById(notice);
    }

    /**
     * 删除公告对象
     * 
     * @param noticeId
     *            公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeById(Long noticeId) {
        return this.noticeMapper.deleteById(noticeId);
    }

    /**
     * 批量删除公告信息
     * 
     * @param noticeIds
     *            需要删除的公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeByIds(Long[] noticeIds) {
        return this.noticeMapper.deleteBatchIds(Arrays.asList(noticeIds));
    }
}
