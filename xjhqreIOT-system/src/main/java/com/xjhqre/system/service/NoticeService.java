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
     * 查询公告信息
     * 
     * @param noticeId
     *            公告ID
     * @return 公告信息
     */
    Notice selectNoticeById(Long noticeId);

    /**
     * 根据条件分页查询公告信息
     * 
     * @param notice
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<Notice> findNotice(Notice notice, Integer pageNum, Integer pageSize);

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
    int insertNotice(Notice notice);

    /**
     * 修改公告
     * 
     * @param notice
     *            公告信息
     * @return 结果
     */
    int updateNotice(Notice notice);

    /**
     * 删除公告信息
     * 
     * @param noticeId
     *            公告ID
     * @return 结果
     */
    int deleteNoticeById(Long noticeId);

    /**
     * 批量删除公告信息
     * 
     * @param noticeIds
     *            需要删除的公告ID
     * @return 结果
     */
    int deleteNoticeByIds(Long[] noticeIds);

}
