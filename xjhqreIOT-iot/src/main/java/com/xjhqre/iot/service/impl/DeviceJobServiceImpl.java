package com.xjhqre.iot.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjhqre.common.constant.ScheduleConstants;
import com.xjhqre.common.exception.TaskException;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.iot.domain.dto.DeviceJobAddDTO;
import com.xjhqre.iot.domain.dto.DeviceJobUpdateDTO;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.DeviceJob;
import com.xjhqre.iot.domain.entity.Product;
import com.xjhqre.iot.domain.entity.ThingsModel;
import com.xjhqre.iot.domain.vo.DeviceJobVO;
import com.xjhqre.iot.mapper.DeviceJobMapper;
import com.xjhqre.iot.quartz.ScheduleUtils;
import com.xjhqre.iot.service.DeviceJobService;
import com.xjhqre.iot.service.DeviceService;
import com.xjhqre.iot.service.ProductService;
import com.xjhqre.iot.service.ThingsModelService;

/**
 * 设备定时任务serviceImpl层
 * 
 * @author xjhqre
 * @since 2023-1-6
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DeviceJobServiceImpl implements DeviceJobService {
    @Resource
    private Scheduler scheduler;
    @Resource
    private DeviceService deviceService;
    @Resource
    private DeviceJobMapper deviceJobMapper;
    @Resource
    private ThingsModelService thingsModelService;
    @Resource
    private ProductService productService;

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
    public IPage<DeviceJobVO> find(DeviceJob deviceJob, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<DeviceJob> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(deviceJob.getDeviceId() != null, DeviceJob::getDeviceId, deviceJob.getDeviceId())
            .like(deviceJob.getDeviceNumber() != null && !"".equals(deviceJob.getDeviceNumber()),
                DeviceJob::getDeviceNumber, deviceJob.getDeviceNumber())
            .eq(deviceJob.getJobGroup() != null && !"".equals(deviceJob.getJobGroup()), DeviceJob::getJobGroup,
                deviceJob.getJobGroup())
            .eq(deviceJob.getStatus() != null, DeviceJob::getStatus, deviceJob.getStatus())
            .eq(deviceJob.getJobId() != null, DeviceJob::getJobId, deviceJob.getJobId())
            .like(deviceJob.getJobName() != null && !"".equals(deviceJob.getJobName()), DeviceJob::getJobName,
                deviceJob.getJobName());
        return this.deviceJobMapper.selectPage(new Page<>(pageNum, pageSize), wrapper).convert(item -> {
            DeviceJobVO deviceJobVO = new DeviceJobVO();
            BeanUtils.copyProperties(item, deviceJobVO);
            ThingsModel thingsModel = this.thingsModelService.getById(deviceJobVO.getModelId());
            deviceJobVO.setActionName(thingsModel.getModelName());
            return deviceJobVO;
        });

    }

    /**
     * 获取设备定时任务详情
     *
     */
    @Override
    public DeviceJobVO getDetail(Long jobId) {
        DeviceJob deviceJob = this.deviceJobMapper.selectById(jobId);
        Long modelId = deviceJob.getModelId();
        ThingsModel thingsModel = this.thingsModelService.getById(modelId);

        DeviceJobVO deviceJobVO = new DeviceJobVO();
        BeanUtils.copyProperties(deviceJob, deviceJobVO);

        deviceJobVO.setServiceModel(thingsModel);
        deviceJobVO.setType(thingsModel.getInputParam().getType());
        deviceJobVO.setEnumList(thingsModel.getInputParam().getSpecs().getEnumList());
        deviceJobVO.setTrueText(thingsModel.getInputParam().getSpecs().getTrueText());
        deviceJobVO.setFalseText(thingsModel.getInputParam().getSpecs().getFalseText());
        return deviceJobVO;
    }

    /**
     * 新增任务
     *
     * @param dto
     *            调度信息 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(DeviceJobAddDTO dto) throws SchedulerException, TaskException {
        Device device = this.deviceService.getById(dto.getDeviceId());
        Product product = this.productService.getById(device.getProductId());
        ThingsModel thingsModel = this.thingsModelService.getById(dto.getModelId());
        DeviceJob deviceJob = new DeviceJob();
        deviceJob.setJobName(dto.getJobName());
        deviceJob.setJobGroup("DEFAULT");
        deviceJob.setCronExpression(dto.getCronExpression());
        deviceJob.setMisfirePolicy("0");
        deviceJob.setConcurrent("1");
        deviceJob.setStatus(dto.getStatus());
        deviceJob.setDeviceId(device.getDeviceId());
        deviceJob.setDeviceNumber(device.getDeviceNumber());
        deviceJob.setProductId(product.getProductId());
        deviceJob.setProductKey(product.getProductKey());
        deviceJob.setModelId(dto.getModelId());
        deviceJob.setIdentifier(thingsModel.getIdentifier());
        deviceJob.setValue(dto.getValue());
        deviceJob.setCreateBy(SecurityUtils.getUsername());
        deviceJob.setCreateTime(new Date());
        this.deviceJobMapper.insert(deviceJob);
        ScheduleUtils.createScheduleJob(this.scheduler, deviceJob);
    }

    /**
     * 更新任务的时间表达式
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DeviceJobUpdateDTO job) throws SchedulerException, TaskException {
        ThingsModel thingsModel = this.thingsModelService.getById(job.getModelId());
        DeviceJob deviceJob = new DeviceJob();
        deviceJob.setJobId(job.getJobId());
        deviceJob.setJobName(job.getJobName());
        deviceJob.setCronExpression(job.getCronExpression());
        deviceJob.setStatus(job.getStatus());
        deviceJob.setModelId(job.getModelId());
        deviceJob.setValue(job.getValue());
        deviceJob.setIdentifier(thingsModel.getIdentifier());
        deviceJob.setUpdateBy(SecurityUtils.getUsername());
        deviceJob.setUpdateTime(new Date());

        this.deviceJobMapper.updateById(deviceJob);
        // 判断是否存在
        JobKey jobKey = ScheduleUtils.getJobKey(deviceJob.getJobId(), "DEFAULT");
        if (this.scheduler.checkExists(jobKey)) {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            this.scheduler.deleteJob(jobKey);
        }
        ScheduleUtils.createScheduleJob(this.scheduler, this.deviceJobMapper.selectById(deviceJob.getJobId()));
    }

    /**
     * 任务调度状态修改
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long jobId, Integer status) throws SchedulerException {
        DeviceJob deviceJob = this.deviceJobMapper.selectById(jobId);
        deviceJob.setUpdateTime(DateUtils.getNowDate());
        deviceJob.setUpdateBy(SecurityUtils.getUsername());
        deviceJob.setStatus(status);
        this.deviceJobMapper.updateById(deviceJob);
        String jobGroup = deviceJob.getJobGroup();
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
