package com.xjhqre.iot.service;

import java.util.List;

import org.quartz.SchedulerException;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.exception.TaskException;
import com.xjhqre.iot.domain.dto.DeviceJobAddDTO;
import com.xjhqre.iot.domain.dto.DeviceJobUpdateDTO;
import com.xjhqre.iot.domain.entity.DeviceJob;
import com.xjhqre.iot.domain.vo.DeviceJobVO;

/**
 * DeviceJobService
 * 
 * @author xjhqre
 * @since 2023-1-6
 */
public interface DeviceJobService {

    /**
     * 分页查询设备定时任务
     */
    IPage<DeviceJobVO> find(DeviceJob deviceJob, Integer pageNum, Integer pageSize);

    /**
     * 获取设备定时任务详情
     *
     */
    DeviceJobVO getDetail(Long jobId);

    /**
     * 新增任务
     *
     */
    void add(DeviceJobAddDTO job) throws SchedulerException, TaskException;

    /**
     * 更新任务
     *
     */
    void update(DeviceJobUpdateDTO job) throws SchedulerException, TaskException;

    /**
     * 任务调度状态修改
     *
     */
    void changeStatus(Long jobId, Integer status) throws SchedulerException;

    /**
     * 立即运行任务
     *
     */
    void run(DeviceJob job);

    /**
     * 批量删除调度信息
     *
     */
    void delete(List<Long> jobIds);

    /**
     * 根据设备Ids批量删除调度信息
     *
     */
    void deleteJobByDeviceId(List<Long> deviceIds);
}
