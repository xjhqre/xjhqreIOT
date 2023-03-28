package com.xjhqre.iot.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.Product;
import com.xjhqre.iot.domain.entity.ThingsModel;
import com.xjhqre.iot.domain.entity.ThingsModelValue;
import com.xjhqre.iot.mapper.ThingsModelValueMapper;
import com.xjhqre.iot.service.AlertLogService;
import com.xjhqre.iot.service.AlertService;
import com.xjhqre.iot.service.DeviceService;
import com.xjhqre.iot.service.ProductService;
import com.xjhqre.iot.service.ThingsModelService;
import com.xjhqre.iot.service.ThingsModelValueService;

/**
 * <p>
 * ThingsModelValueLogServiceImpl
 * </p>
 *
 * @author xjhqre
 * @since 3月 13, 2023
 */
@Service
public class ThingsModelValueServiceImpl extends ServiceImpl<ThingsModelValueMapper, ThingsModelValue>
    implements ThingsModelValueService {

    @Resource
    private DeviceService deviceService;
    @Resource
    private ProductService productService;
    @Resource
    private ThingsModelService thingsModelService;
    @Resource
    private AlertService alertService;
    @Resource
    private AlertLogService alertLogService;
    @Resource
    private ThingsModelValueService thingsModelValueService;

    /**
     * 查询设备物模型值列表
     * 
     * @param thingsModelValue
     * @return
     */
    @Override
    public List<ThingsModelValue> list(ThingsModelValue thingsModelValue, Integer dateRange) {
        LambdaQueryWrapper<ThingsModelValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(thingsModelValue.getModelId() != null, ThingsModelValue::getModelId, thingsModelValue.getModelId())
            .eq(thingsModelValue.getModelName() != null && !"".equals(thingsModelValue.getModelName()),
                ThingsModelValue::getModelName, thingsModelValue.getModelName())
            .eq(thingsModelValue.getIdentifier() != null && !"".equals(thingsModelValue.getIdentifier()),
                ThingsModelValue::getIdentifier, thingsModelValue.getIdentifier())
            .eq(thingsModelValue.getDeviceId() != null, ThingsModelValue::getDeviceId, thingsModelValue.getDeviceId())
            .eq(thingsModelValue.getDeviceName() != null && !"".equals(thingsModelValue.getDeviceName()),
                ThingsModelValue::getDeviceName, thingsModelValue.getDeviceName());
        if (dateRange != null) {
            if (dateRange == 1) {
                wrapper.last("and create_time > date_sub(date_add(now(), interval 8 hour), interval 1 hour) ");
            } else if (dateRange == 2) {
                wrapper.last("and create_time > date_sub(date_add(now(), interval 8 hour), interval 1 day) ");
            } else if (dateRange == 3) {
                wrapper.last("and create_time > date_sub(date_add(now(), interval 8 hour), interval 7 day) ");
            }
        }
        return this.list(wrapper);
    }

    /**
     * 添加物模型值
     * 
     * @param productKey
     * @param deviceNum
     * @param message
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(String productKey, String deviceNum, String message) {

        // 根据设备编号查询设备信息
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getDeviceNumber, deviceNum);
        Device device = this.deviceService.getOne(wrapper);

        LambdaQueryWrapper<Product> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(Product::getProductKey, productKey);
        Product product = this.productService.getOne(wrapper1);

        List<ThingsModelValue> thingsModelValues = JSON.parseArray(message, ThingsModelValue.class);
        thingsModelValues = thingsModelValues.stream().filter(vo -> StringUtils.isNotBlank(vo.getValue())).peek(vo -> {
            String identifier = vo.getIdentifier();
            LambdaQueryWrapper<ThingsModel> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(ThingsModel::getProductId, product.getProductId()).eq(ThingsModel::getIdentifier, identifier);
            ThingsModel thingsModel = this.thingsModelService.getOne(wrapper2);
            vo.setModelId(thingsModel.getModelId());
            vo.setModelName(thingsModel.getModelName());
            vo.setDeviceId(device.getDeviceId());
            vo.setDeviceName(device.getDeviceName());
            vo.setProductId(product.getProductId());
            vo.setProductName(product.getProductName());
            vo.setType(1);
            vo.setCreateBy(null);
            vo.setUpdateBy(null);
            vo.setUpdateTime(null);
            // vo.setMessage(message);
            vo.setCreateTime(DateUtils.getNowDate());
        }).collect(Collectors.toList());

        // 保存物模型值
        this.saveBatch(thingsModelValues);

        // TODO 场景联动、告警规则匹配处理
    }
}