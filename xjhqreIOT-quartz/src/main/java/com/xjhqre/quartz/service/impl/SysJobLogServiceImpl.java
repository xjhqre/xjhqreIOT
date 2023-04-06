package com.xjhqre.quartz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.quartz.domain.SysJobLog;
import com.xjhqre.quartz.mapper.SysJobLogMapper;
import com.xjhqre.quartz.service.SysJobLogService;
import org.springframework.transaction.annotation.Transactional;

/**
 * SysJobLogServiceImpl
 * 
 * @author xjhqre
 * @since 2023-1-7
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog> implements SysJobLogService {
    @Resource
    private SysJobLogMapper jobLogMapper;

    /**
     * 分页查询定时任务调度日志列表
     */
    @Override
    public IPage<SysJobLog> find(SysJobLog sysJobLog, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<SysJobLog> wrapper = new LambdaQueryWrapper<>();
        wrapper
            .eq(sysJobLog.getJobGroup() != null && !"".equals(sysJobLog.getJobGroup()), SysJobLog::getJobGroup,
                sysJobLog.getJobGroup())
            .like(sysJobLog.getJobName() != null && !"".equals(sysJobLog.getJobName()), SysJobLog::getJobName,
                sysJobLog.getJobName())
            .eq(sysJobLog.getStatus() != null && !"".equals(sysJobLog.getStatus()), SysJobLog::getStatus,
                sysJobLog.getStatus())
            .like(sysJobLog.getInvokeTarget() != null && !"".equals(sysJobLog.getInvokeTarget()),
                SysJobLog::getInvokeTarget, sysJobLog.getInvokeTarget());
        return this.jobLogMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 获取定时任务日志
     *
     */
    @Override
    public SysJobLog getDetail(Long jobLogId) {
        return this.jobLogMapper.selectById(jobLogId);
    }

    /**
     * 删除定时任务日志信息
     *
     */
    @Override
    public void delete(List<Long> logIdList) {
        this.jobLogMapper.deleteBatchIds(logIdList);
    }

    /**
     * 清空任务日志
     */
    @Override
    public void cleanJobLog() {
        this.jobLogMapper.cleanJobLog();
    }
}
