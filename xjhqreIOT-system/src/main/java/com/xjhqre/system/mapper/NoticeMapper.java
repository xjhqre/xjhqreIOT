package com.xjhqre.system.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.system.domain.entity.Notice;

/**
 * 通知公告表 数据层
 * 
 * @author xjhqre
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {}
