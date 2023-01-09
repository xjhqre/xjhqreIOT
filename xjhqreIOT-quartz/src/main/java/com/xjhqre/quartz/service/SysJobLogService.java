package com.xjhqre.quartz.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.quartz.domain.SysJobLog;

/**
 * 定时任务调度日志信息信息 服务层
 * 
 * @author ruoyi
 */
public interface SysJobLogService extends IService<SysJobLog> {

    /**
     * 分页查询定时任务调度日志列表
     */
    IPage<SysJobLog> find(SysJobLog sysJobLog, Integer pageNum, Integer pageSize);

    /**
     * 获取定时任务日志详情
     *
     */
    SysJobLog getDetail(Long jobLogId);

    /**
     * 删除定时任务日志
     *
     */
    void delete(List<Long> logIds);

    /**
     * 清空定时任务日志
     */
    void cleanJobLog();
}
