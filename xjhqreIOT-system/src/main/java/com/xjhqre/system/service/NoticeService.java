package com.xjhqre.system.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.system.domain.entity.Notice;

/**
 * 公告 服务层
 * 
 * @author xjhqre
 */
public interface NoticeService {
    /**
     * 查询公告详情
     *
     */
    Notice getDetail(Long noticeId);

    /**
     * 根据条件分页查询公告信息
     *
     */
    IPage<Notice> find(Notice notice, Integer pageNum, Integer pageSize);

    /**
     * 查询公告列表
     * 
     * @param notice
     *            公告信息
     * @return 公告集合
     */
    List<Notice> selectNoticeList(Notice notice);

    /**
     * 新增公告
     * 
     * @param notice
     *            公告信息
     * @return 结果
     */
    void add(Notice notice);

    /**
     * 修改公告
     * 
     * @param notice
     *            公告信息
     * @return 结果
     */
    void update(Notice notice);

    /**
     * 批量删除公告信息
     *
     */
    void delete(List<Long> noticeIds);

}
