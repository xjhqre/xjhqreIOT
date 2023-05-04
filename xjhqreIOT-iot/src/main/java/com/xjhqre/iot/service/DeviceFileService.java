package com.xjhqre.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.iot.domain.entity.DeviceFile;

/**
 * <p>
 * DeviceFileService
 * </p>
 *
 * @author xjhqre
 * @since 4月 18, 2023
 */
public interface DeviceFileService extends IService<DeviceFile> {
    IPage<DeviceFile> find(DeviceFile deviceFile, Integer pageNum, Integer pageSize);

    void add(DeviceFile deviceFile);

    void delete(List<Long> fileIds);
}
