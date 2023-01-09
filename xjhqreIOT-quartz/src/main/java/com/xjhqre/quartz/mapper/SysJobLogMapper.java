package com.xjhqre.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.quartz.domain.SysJobLog;

/**
 * 调度任务日志信息 数据层
 * 
 * @author ruoyi
 */
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {

    /**
     * 清空任务日志
     */
    void cleanJobLog();
}
