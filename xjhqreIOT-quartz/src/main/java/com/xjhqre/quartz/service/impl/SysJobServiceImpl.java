package com.xjhqre.quartz.service.impl;

import static com.xjhqre.common.utils.SecurityUtils.getUsername;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.constant.ScheduleConstants;
import com.xjhqre.common.exception.TaskException;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.quartz.domain.SysJob;
import com.xjhqre.quartz.mapper.SysJobMapper;
import com.xjhqre.quartz.service.SysJobService;
import com.xjhqre.quartz.util.CronUtils;
import com.xjhqre.quartz.util.ScheduleUtils;

/**
 * 定时任务调度信息 服务层
 * 
 * @author ruoyi
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob> implements SysJobService {
    @Resource
    private Scheduler scheduler;

    @Resource
    private SysJobMapper sysJobMapper;

    /**
     * 项目启动时，初始化定时器 主要是防止手动修改数据库导致未同步到定时任务处理（注：不能手动修改数据库ID和任务组名，否则会导致脏数据）
     */
    @PostConstruct
    public void init() throws SchedulerException, TaskException {
        this.scheduler.clear();
        List<SysJob> jobList = this.sysJobMapper.selectList(null);
        for (SysJob job : jobList) {
            ScheduleUtils.createScheduleJob(this.scheduler, job);
        }
    }

    /**
     * 分页查询定时任务列表
     */
    @Override
    public IPage<SysJob> find(SysJob sysJob, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<SysJob> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(sysJob.getJobId() != null, SysJob::getJobId, sysJob.getJobId())
            .like(sysJob.getJobName() != null && !"".equals(sysJob.getJobName()), SysJob::getJobName,
                sysJob.getJobName())
            .eq(sysJob.getJobGroup() != null && !"".equals(sysJob.getJobGroup()), SysJob::getJobGroup,
                sysJob.getJobGroup())
            .eq(sysJob.getStatus() != null && !"".equals(sysJob.getStatus()), SysJob::getStatus, sysJob.getStatus())
            .like(sysJob.getInvokeTarget() != null && !"".equals(sysJob.getInvokeTarget()), SysJob::getInvokeTarget,
                sysJob.getInvokeTarget());
        return this.sysJobMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 获取定时任务详情
     */
    @Override
    public SysJob getDetail(Long jobId) {
        return this.sysJobMapper.selectById(jobId);
    }

    /**
     * 新增任务
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(SysJob job) throws SchedulerException, TaskException {
        job.setCreateBy(SecurityUtils.getUsername());
        job.setCreateTime(DateUtils.getNowDate());
        this.sysJobMapper.insert(job);
        ScheduleUtils.createScheduleJob(this.scheduler, job);
    }

    /**
     * 更新任务的时间表达式
     *
     * @param job
     *            调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJob(SysJob job) throws SchedulerException, TaskException {
        job.setUpdateBy(getUsername());
        job.setUpdateTime(DateUtils.getNowDate());
        this.sysJobMapper.updateById(job);
        SysJob sysJob = this.sysJobMapper.selectById(job.getJobId());
        Long jobId = job.getJobId();
        String jobGroup = sysJob.getJobGroup();
        // 判断是否存在
        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
        if (this.scheduler.checkExists(jobKey)) {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            this.scheduler.deleteJob(jobKey);
        }
        ScheduleUtils.createScheduleJob(this.scheduler, job);
    }

    /**
     * 任务调度状态修改
     *
     * @param job
     *            调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(SysJob job) throws SchedulerException {
        this.sysJobMapper.updateById(job);
        String status = job.getStatus();
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        if (ScheduleConstants.Status.NORMAL.getValue().equals(status)) {
            this.scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        } else if (ScheduleConstants.Status.PAUSE.getValue().equals(status)) {
            this.scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
    }

    /**
     * 批量删除调度信息
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> jobIdList) throws SchedulerException {
        for (Long jobId : jobIdList) {
            SysJob sysJob = this.sysJobMapper.selectById(jobId);
            String jobGroup = sysJob.getJobGroup();
            this.sysJobMapper.deleteById(jobId);
            this.scheduler.deleteJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
    }

    /**
     * 立即运行任务
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        SysJob sysJob = this.sysJobMapper.selectById(job.getJobId());
        // 参数
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(ScheduleConstants.TASK_PROPERTIES, sysJob);
        this.scheduler.triggerJob(ScheduleUtils.getJobKey(jobId, jobGroup), dataMap);
    }

    /**
     * 校验cron表达式是否有效
     *
     */
    @Override
    public boolean checkCron(String cronExpression) {
        return CronUtils.isValid(cronExpression);
    }
}
