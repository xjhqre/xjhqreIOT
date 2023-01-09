package com.xjhqre.quartz.service;

import java.util.List;

import org.quartz.SchedulerException;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.common.exception.TaskException;
import com.xjhqre.quartz.domain.SysJob;

/**
 * 定时任务调度信息信息 服务层
 * 
 * @author ruoyi
 */
public interface SysJobService extends IService<SysJob> {

    /**
     * 分页查询定时任务列表
     */
    IPage<SysJob> find(SysJob sysJob, Integer pageNum, Integer pageSize);

    /**
     * 获取定时任务详情
     */
    SysJob getDetail(Long jobId);

    /**
     * 新增任务
     *
     */
    void add(SysJob job) throws SchedulerException, TaskException;

    /**
     * 更新任务
     *
     */
    void updateJob(SysJob job) throws SchedulerException, TaskException;

    /**
     * 批量删除调度信息
     *
     */
    void delete(List<Long> jobIds) throws SchedulerException;

    /**
     * 任务调度状态修改
     * 
     * @param job
     *            调度信息
     * @return 结果
     */
    void changeStatus(SysJob job) throws SchedulerException;

    /**
     * 立即运行任务
     *
     */
    void run(SysJob job) throws SchedulerException;

    /**
     * 校验cron表达式是否有效
     * 
     * @param cronExpression
     *            表达式
     * @return 结果
     */
    boolean checkCron(String cronExpression);
}
