package com.xjhqre.iot.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.iot.domain.entity.DeviceFile;
import com.xjhqre.iot.mapper.DeviceFileMapper;
import com.xjhqre.iot.service.DeviceFileService;

/**
 * <p>
 * DeviceFileServiceImpl
 * </p>
 *
 * @author xjhqre
 * @since 4月 18, 2023
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DeviceFileServiceImpl extends ServiceImpl<DeviceFileMapper, DeviceFile> implements DeviceFileService {

    @Resource
    DeviceFileMapper deviceFileMapper;

    @Override
    public IPage<DeviceFile> find(DeviceFile deviceFile, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<DeviceFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(deviceFile.getDeviceId() != null, DeviceFile::getDeviceId, deviceFile.getDeviceId())
            .eq(deviceFile.getFileName() != null && !"".equals(deviceFile.getFileName()), DeviceFile::getFileName,
                deviceFile.getFileName())
            .eq(deviceFile.getFileId() != null, DeviceFile::getFileId, deviceFile.getFileId());
        return this.deviceFileMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public void add(DeviceFile deviceFile) {
        deviceFile.setCreateBy(SecurityUtils.getUsername());
        deviceFile.setCreateTime(new Date());
        this.deviceFileMapper.insert(deviceFile);
    }

    @Override
    public void delete(List<Long> fileIds) {
        this.deviceFileMapper.deleteBatchIds(fileIds);
    }
}
