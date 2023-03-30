package com.xjhqre.iot.service.impl;

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
import com.xjhqre.common.constant.ScheduleConstants;
import com.xjhqre.common.exception.TaskException;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.DeviceJob;
import com.xjhqre.iot.mapper.DeviceJobMapper;
import com.xjhqre.iot.quartz.ScheduleUtils;
import com.xjhqre.iot.service.DeviceJobService;
import com.xjhqre.iot.service.DeviceService;
import com.xjhqre.iot.service.ProductService;

/**
 * 设备定时任务serviceImpl层
 * 
 * @author xjhqre
 * @since 2023-1-6
 */
@Service
public class DeviceJobServiceImpl implements DeviceJobService {
    @Resource
    private Scheduler scheduler;
    @Resource
    private DeviceService deviceService;
    @Resource
    private ProductService productService;
    @Resource
    private DeviceJobMapper deviceJobMapper;

    /**
     * 项目启动时，初始化定时器 主要是防止手动修改数据库导致未同步到定时任务处理（注：不能手动修改数据库ID和任务组名，否则会导致脏数据）
     */
    @PostConstruct
    public void init() throws SchedulerException, TaskException {
        this.scheduler.clear();
        List<DeviceJob> jobList = this.deviceJobMapper.selectList(null);
        for (DeviceJob deviceJob : jobList) {
            ScheduleUtils.createScheduleJob(this.scheduler, deviceJob);
        }
    }

    /**
     * 分页查询设备定时任务
     */
    @Override
    public IPage<DeviceJob> find(DeviceJob deviceJob, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<DeviceJob> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(deviceJob.getDeviceId() != null, DeviceJob::getDeviceId, deviceJob.getDeviceId())
            .like(deviceJob.getDeviceName() != null && !"".equals(deviceJob.getDeviceName()), DeviceJob::getDeviceName,
                deviceJob.getDeviceName())
            .eq(deviceJob.getJobGroup() != null && !"".equals(deviceJob.getJobGroup()), DeviceJob::getJobGroup,
                deviceJob.getJobGroup())
            .eq(deviceJob.getStatus() != null && !"".equals(deviceJob.getStatus()), DeviceJob::getStatus,
                deviceJob.getStatus())
            .eq(deviceJob.getJobId() != null, DeviceJob::getJobId, deviceJob.getJobId())
            .like(deviceJob.getJobName() != null && !"".equals(deviceJob.getJobName()), DeviceJob::getJobName,
                deviceJob.getJobName());
        return this.deviceJobMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 获取设备定时任务详情
     *
     */
    @Override
    public DeviceJob getDetail(Long jobId) {
        return this.deviceJobMapper.selectById(jobId);
    }

    /**
     * 新增任务
     *
     * @param deviceJob
     *            调度信息 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(DeviceJob deviceJob) throws SchedulerException, TaskException {
        Device device = this.deviceService.getById(deviceJob.getDeviceId());
        deviceJob.setDeviceNumber(device.getDeviceNumber());
        deviceJob.setProductId(device.getProductId());
        deviceJob.setProductName(device.getProductName());
        deviceJob.setCreateBy(SecurityUtils.getUsername());
        deviceJob.setCreateTime(DateUtils.getNowDate());
        this.deviceJobMapper.insert(deviceJob);
        ScheduleUtils.createScheduleJob(this.scheduler, deviceJob);
    }

    /**
     * 更新任务的时间表达式
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DeviceJob job) throws SchedulerException, TaskException {
        job.setUpdateBy(SecurityUtils.getUsername());
        job.setUpdateTime(DateUtils.getNowDate());
        this.deviceJobMapper.updateById(job);
        DeviceJob deviceJob = this.deviceJobMapper.selectById(job.getJobId());
        Long jobId = deviceJob.getJobId();
        String jobGroup = deviceJob.getJobGroup();
        // 判断是否存在
        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
        if (this.scheduler.checkExists(jobKey)) {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            this.scheduler.deleteJob(jobKey);
        }
        ScheduleUtils.createScheduleJob(this.scheduler, deviceJob);
    }

    /**
     * 任务调度状态修改
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(DeviceJob job) throws SchedulerException {
        job.setUpdateTime(DateUtils.getNowDate());
        job.setUpdateBy(SecurityUtils.getUsername());
        this.deviceJobMapper.updateById(job);
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
     * 立即运行任务
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(DeviceJob job) {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        DeviceJob deviceJob = this.deviceJobMapper.selectById(job.getJobId());
        // 参数
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(ScheduleConstants.TASK_PROPERTIES, deviceJob);
        try {
            this.scheduler.triggerJob(ScheduleUtils.getJobKey(jobId, jobGroup), dataMap);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量删除调度信息
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> jobIds) {
        for (Long jobId : jobIds) {
            DeviceJob job = this.deviceJobMapper.selectById(jobId);
            String jobGroup = job.getJobGroup();
            this.deviceJobMapper.deleteById(jobId);
            try {
                this.scheduler.deleteJob(ScheduleUtils.getJobKey(jobId, jobGroup));
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 根据设备Ids批量删除调度信息
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobByDeviceId(List<Long> deviceIds) {
        List<DeviceJob> deviceJobs = this.deviceJobMapper.selectBatchIds(deviceIds);

        // 批量删除job
        this.deviceJobMapper.deleteBatchIds(deviceIds);

        // 批量删除调度器
        for (DeviceJob job : deviceJobs) {
            try {
                this.scheduler.deleteJob(ScheduleUtils.getJobKey(job.getJobId(), job.getJobGroup()));
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
