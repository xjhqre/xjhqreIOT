package com.xjhqre.iot.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.Firmware;
import com.xjhqre.iot.domain.entity.Product;
import com.xjhqre.iot.mapper.FirmwareMapper;
import com.xjhqre.iot.service.DeviceService;
import com.xjhqre.iot.service.FirmwareService;
import com.xjhqre.iot.service.ProductService;

/**
 * 产品固件Service业务层处理
 * 
 * @author xjhqre
 * @date 2021-12-16
 */
@Service
public class FirmwareServiceImpl extends ServiceImpl<FirmwareMapper, Firmware> implements FirmwareService {
    @Resource
    private FirmwareMapper firmwareMapper;
    @Resource
    private ProductService productService;
    @Resource
    private DeviceService deviceService;

    /**
     * 产品固件分页列表
     */
    @Override
    public IPage<Firmware> find(Firmware firmware, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Firmware> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(firmware.getFirmwareId() != null, Firmware::getFirmwareId, firmware.getFirmwareId())
            .like(firmware.getFirmwareName() != null && !"".equals(firmware.getFirmwareName()),
                Firmware::getFirmwareName, firmware.getFirmwareName())
            .eq(firmware.getProductId() != null, Firmware::getProductId, firmware.getProductId())
            .like(firmware.getProductName() != null && !"".equals(firmware.getProductName()), Firmware::getProductName,
                firmware.getProductName());
        return this.firmwareMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 产品固件列表
     */
    @Override
    public List<Firmware> list(Firmware firmware) {
        LambdaQueryWrapper<Firmware> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(firmware.getFirmwareId() != null, Firmware::getFirmwareId, firmware.getFirmwareId())
            .like(firmware.getFirmwareName() != null && !"".equals(firmware.getFirmwareName()),
                Firmware::getFirmwareName, firmware.getFirmwareName())
            .eq(firmware.getProductId() != null, Firmware::getProductId, firmware.getProductId())
            .like(firmware.getProductName() != null && !"".equals(firmware.getProductName()), Firmware::getProductName,
                firmware.getProductName());
        return this.firmwareMapper.selectList(wrapper);
    }

    @Override
    public Firmware getDetail(Long firmwareId) {
        return this.firmwareMapper.selectById(firmwareId);
    }

    /**
     * 新增产品固件
     */
    @Override
    public void add(Firmware firmware) {
        firmware.setCreateBy(SecurityUtils.getUsername());
        firmware.setCreateTime(DateUtils.getNowDate());
        this.firmwareMapper.insert(firmware);
    }

    /**
     * 更新产品固件
     */
    @Override
    public void update(Firmware firmware) {
        firmware.setUpdateBy(SecurityUtils.getUsername());
        firmware.setUpdateTime(DateUtils.getNowDate());
        this.firmwareMapper.updateById(firmware);
    }

    /**
     * 批量删除产品固件
     */
    @Override
    public void delete(Long[] firmwareIds) {
        this.firmwareMapper.deleteBatchIds(Arrays.asList(firmwareIds));
    }

    @Override
    public List<Device> listDeviceByFirmwareId(Long firmwareId) {
        Firmware firmware = this.firmwareMapper.selectById(firmwareId);
        Long productId = firmware.getProductId();
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getProductId, productId);
        List<Device> deviceList = this.deviceService.list(wrapper);
        for (Device device : deviceList) {
            Product product = this.productService.getById(device.getProductId());
            device.setProductKey(product.getProductKey());
        }
        return deviceList;
    }
}
