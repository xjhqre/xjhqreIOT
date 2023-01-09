package com.xjhqre.iot.service.impl;

import static com.xjhqre.common.utils.SecurityUtils.getLoginUser;
import static com.xjhqre.common.utils.SecurityUtils.getUsername;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.domain.entity.User;
import com.xjhqre.common.domain.model.LoginUser;
import com.xjhqre.common.utils.AssertUtils;
import com.xjhqre.common.utils.BaiduMapUtils;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.common.utils.http.HttpUtils;
import com.xjhqre.common.utils.ip.IpUtils;
import com.xjhqre.common.utils.uuid.RandomUtils;
import com.xjhqre.iot.domain.entity.AlertLog;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.DeviceLog;
import com.xjhqre.iot.domain.entity.Product;
import com.xjhqre.iot.domain.entity.ThingsModel;
import com.xjhqre.iot.domain.model.DeviceStatistic;
import com.xjhqre.iot.domain.model.thingsModelItem.ArrayModel;
import com.xjhqre.iot.domain.model.thingsModelItem.BoolModel;
import com.xjhqre.iot.domain.model.thingsModelItem.DoubleModel;
import com.xjhqre.iot.domain.model.thingsModelItem.EnumItem;
import com.xjhqre.iot.domain.model.thingsModelItem.EnumModel;
import com.xjhqre.iot.domain.model.thingsModelItem.IntegerModel;
import com.xjhqre.iot.domain.model.thingsModelItem.ReadOnlyModelOutput;
import com.xjhqre.iot.domain.model.thingsModelItem.StringModel;
import com.xjhqre.iot.domain.model.thingsModelItem.ThingsModelItemBase;
import com.xjhqre.iot.domain.model.thingsModels.ModelIdAndValue;
import com.xjhqre.iot.domain.model.thingsModels.ThingsModelShadow;
import com.xjhqre.iot.domain.vo.DeviceVO;
import com.xjhqre.iot.mapper.DeviceMapper;
import com.xjhqre.iot.mqtt.EmqxService;
import com.xjhqre.iot.service.AlertLogService;
import com.xjhqre.iot.service.DeviceLogService;
import com.xjhqre.iot.service.DeviceService;
import com.xjhqre.iot.service.ProductService;

import lombok.extern.slf4j.Slf4j;

/**
 * 设备Service业务层处理
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {

    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private ThingsModelServiceImpl thingsModelService;
    @Resource
    private DeviceJobServiceImpl deviceJobService;
    @Resource
    private DeviceLogService deviceLogService;
    @Resource
    private ProductService productService;
    @Resource
    private EmqxService emqxService;
    @Resource
    private AlertLogService alertLogService;

    @Override
    public IPage<DeviceVO> find(Device device, Integer pageNum, Integer pageSize) {
        LoginUser user = getLoginUser();
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(device.getDeviceId() != null, Device::getDeviceId, device.getDeviceId())
            .eq(device.getDeviceNumber() != null, Device::getDeviceNumber, device.getDeviceNumber())
            .like(device.getDeviceName() != null, Device::getDeviceName, device.getDeviceName())
            .eq(device.getProductId() != null, Device::getProductId, device.getProductId())
            .like(device.getProductName() != null, Device::getProductName, device.getProductName());

        if (!SecurityUtils.isAdmin(user.getUserId())) {
            wrapper.eq(Device::getUserId, SecurityUtils.getUserId());
        }

        Page<Device> devicePage = this.deviceMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return devicePage.convert(item -> {
            DeviceVO deviceVO = new DeviceVO();
            BeanUtils.copyProperties(item, deviceVO);
            // 解析物模型值JSON为集合
            this.setThingsModelValue(deviceVO, false);
            return deviceVO;
        });
    }

    /**
     * 查询设备列表
     *
     * @param device
     *            设备
     * @return 设备
     */
    @Override
    public List<Device> selectDeviceList(Device device) {
        LoginUser user = getLoginUser();
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(device.getDeviceId() != null, Device::getDeviceId, device.getDeviceId())
            .eq(device.getDeviceNumber() != null, Device::getDeviceNumber, device.getDeviceNumber())
            .like(device.getDeviceName() != null, Device::getDeviceName, device.getDeviceName())
            .eq(device.getProductId() != null, Device::getProductId, device.getProductId())
            .like(device.getProductName() != null, Device::getProductName, device.getProductName());

        if (!SecurityUtils.isAdmin(user.getUserId())) {
            wrapper.eq(Device::getUserId, SecurityUtils.getUserId());
        }
        return this.deviceMapper.selectList(wrapper);
    }

    @Override
    public IPage<Device> findByGroup(Device device, Integer pageNum, Integer pageSize) {
        Long userId = SecurityUtils.getUserId();
        boolean isAdmin = SecurityUtils.isAdmin(userId);

        if (!isAdmin) {
            device.setUserId(userId);
        }
        return this.deviceMapper.selectDeviceListByGroup(device);
    }

    /**
     * 查询所有设备信息
     */
    @Override
    public IPage<Device> selectAllDevice(Device device, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(device.getDeviceId() != null, Device::getDeviceId, device.getDeviceId())
            .eq(device.getDeviceNumber() != null, Device::getDeviceNumber, device.getDeviceNumber())
            .like(device.getDeviceName() != null, Device::getDeviceName, device.getDeviceName())
            .eq(device.getProductId() != null, Device::getProductId, device.getProductId())
            .like(device.getProductName() != null, Device::getProductName, device.getProductName());
        return this.deviceMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 查询设备详情
     *
     */
    @Override
    public DeviceVO getDetail(Long deviceId) {
        Device device = this.deviceMapper.selectById(deviceId);
        DeviceVO deviceVO = new DeviceVO();
        BeanUtils.copyProperties(device, deviceVO);
        // 物模型转换为对象中的不同类别集合
        this.setThingsModelValue(deviceVO, false);
        return deviceVO;
    }

    /**
     * 根据设备编号查询设备
     */
    @Override
    public Device getByDeviceNumber(String deviceNumber) {
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getDeviceNumber, deviceNumber);
        return this.deviceMapper.selectOne(wrapper);
    }

    /**
     * 查询设备统计信息，管理员显示所有用户的设备信息
     *
     * @return 设备
     */
    @Override
    public DeviceStatistic getStatisticInfo() {
        User user = getLoginUser().getUser();
        boolean isAdmin = SecurityUtils.isAdmin(user.getUserId());

        // 查询产品数量
        LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
        if (!isAdmin) {
            productWrapper.eq(Product::getUserId, user.getUserId());
        }
        int productCount = this.productService.count(productWrapper);

        // 查询设备数量
        LambdaQueryWrapper<Device> deviceWrapper = new LambdaQueryWrapper<>();
        if (!isAdmin) {
            deviceWrapper.eq(Device::getUserId, user.getUserId());
        }
        Integer deviceCount = this.deviceMapper.selectCount(deviceWrapper);

        // 查询告警数量
        LambdaQueryWrapper<AlertLog> alertLogWrapper = new LambdaQueryWrapper<>();
        if (!isAdmin) {
            alertLogWrapper.eq(AlertLog::getUserId, user.getUserId());
        }
        int alertLogCount = this.alertLogService.count(alertLogWrapper);

        // 获取属性上报数量
        LambdaQueryWrapper<DeviceLog> propertiesWrapper = new LambdaQueryWrapper<>();
        propertiesWrapper.eq(DeviceLog::getLogType, 1);
        if (!isAdmin) {
            propertiesWrapper.eq(DeviceLog::getUserId, user.getUserId());
        }
        int propertiesCount = this.deviceLogService.count(propertiesWrapper);

        // 获取功能上报数量
        LambdaQueryWrapper<DeviceLog> functionWrapper = new LambdaQueryWrapper<>();
        functionWrapper.eq(DeviceLog::getLogType, 2);
        if (!isAdmin) {
            functionWrapper.eq(DeviceLog::getUserId, user.getUserId());
        }
        int functionCount = this.deviceLogService.count(functionWrapper);

        // 获取事件上报数量
        LambdaQueryWrapper<DeviceLog> eventWrapper = new LambdaQueryWrapper<>();
        eventWrapper.eq(DeviceLog::getLogType, 3);
        if (!isAdmin) {
            eventWrapper.eq(DeviceLog::getUserId, user.getUserId());
        }
        int eventCount = this.deviceLogService.count(eventWrapper);

        // 获取监测数据上报数量
        LambdaQueryWrapper<DeviceLog> monitorWrapper = new LambdaQueryWrapper<>();
        monitorWrapper.eq(DeviceLog::getLogType, 1).eq(DeviceLog::getIsMonitor, 1);
        if (!isAdmin) {
            monitorWrapper.eq(DeviceLog::getUserId, user.getUserId());
        }
        int monitorCount = this.deviceLogService.count(monitorWrapper);

        DeviceStatistic statistic = new DeviceStatistic();
        statistic.setProductCount(productCount);
        statistic.setDeviceCount(deviceCount);
        statistic.setAlertCount(alertLogCount);
        statistic.setPropertyCount(propertiesCount);
        statistic.setFunctionCount(functionCount);
        statistic.setEventCount(eventCount);
        statistic.setMonitorCount(monitorCount);
        return statistic;
    }

    /**
     * 新增设备
     *
     * @param device
     *            设备
     * @return 结果
     */
    @Override

    public void add(Device device) {
        // 设备编号唯一检查
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getDeviceNumber, device.getDeviceNumber());
        Device existDevice = this.deviceMapper.selectOne(wrapper);
        AssertUtils.isNull(existDevice, "设备编号：" + device.getDeviceNumber() + "已经存在了，新增设备失败");

        User sysUser = getLoginUser().getUser();
        // 添加设备
        device.setCreateTime(DateUtils.getNowDate());
        device.setCreateBy(getUsername());
        device.setDevicePassword(RandomUtils.randomString(16)); // 32位设备密钥
        // 设置功能和属性的默认值
        /* ThingsModelValue:
        [
          {"identifier": "", "modelName": "", "value": "", "isTop": "", "isMonitor": "", "type": "", "datatype": "", "shadow": ""},
          {"identifier": "", "modelName": "", "value": "", "isTop": "", "isMonitor": "", "type": "", "datatype": "", "shadow": ""},
          {"identifier": "", "modelName": "", "value": "", "isTop": "", "isMonitor": "", "type": "", "datatype": "", "shadow": ""}
        ]
         */
        device.setThingsModelValue(
            JSONObject.toJSONString(this.getThingsModelListWithDefaultValue(device.getProductId())));
        device.setUserId(sysUser.getUserId());
        device.setUserName(sysUser.getUserName());
        device.setRssi(0);
        Product product = this.productService.getById(device.getProductId());
        device.setImgUrl(product.getImgUrl());
        User user = getLoginUser().getUser();
        device.setNetworkIp(user.getLoginIp());
        // 定位方式, 1=ip自动定位，2=设备上报定位，3=自定义
        Integer locationWay = device.getLocationWay();
        if (locationWay == 1) {
            // 根据用户ip地址定位
            this.setLocation(user.getLoginIp(), device);
        } else if (locationWay == 2) {
            // 设备上报经纬度定位，不在此设置位置
            device.setAddress(null);
            device.setLatitude(null);
            device.setLongitude(null);
        } else {
            // 用户自定义经纬度，根据经纬度设置地址
            device.setAddress(BaiduMapUtils.getCity(device.getLatitude(), device.getLongitude()));
        }
        this.deviceMapper.insert(device);
    }

    /**
     * 修改设备
     */
    @Override
    public void update(Device device) {
        // 设备编号唯一检查
        Device oldDevice = this.deviceMapper.selectById(device.getDeviceId());
        // 若修改了设备编号
        if (!oldDevice.getDeviceNumber().equals(device.getDeviceNumber())) {
            LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Device::getDeviceNumber, device.getDeviceNumber());
            Device existDevice = this.deviceMapper.selectOne(wrapper);
            AssertUtils.isNull(existDevice, "设备编号：" + device.getDeviceNumber() + " 已经存在，新增设备失败");
        }
        device.setUpdateTime(DateUtils.getNowDate());
        device.setUpdateBy(getUsername());
        // 未激活状态,可以修改产品以及物模型
        if (device.getStatus() == 1) {
            device.setThingsModelValue(
                JSONObject.toJSONString(this.getThingsModelListWithDefaultValue(device.getProductId())));
        } else {
            device.setProductId(null);
            device.setProductName(null);
        }
        this.deviceMapper.updateById(device);
        // 设备取消禁用，原设备状态为禁用，修改为离线
        if (oldDevice.getStatus() == 2 && device.getStatus() == 4) {
            // 发布设备信息
            this.emqxService.publishInfo(oldDevice.getProductId(), oldDevice.getDeviceNumber());
        }
    }

    /**
     * 重置设备状态，在线状态重置为离线
     */
    @Override
    public void resetDeviceStatus(String deviceNum) {
        // -- 设备状态（1-未激活，2-禁用，3-在线，4-离线）
        LambdaUpdateWrapper<Device> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Device::getDeviceNumber, deviceNum).eq(Device::getStatus, 3).set(Device::getStatus, 4);
        this.deviceMapper.update(null, wrapper);
    }

    /**
     * 删除设备
     *
     */
    @Override
    public void delete(List<Long> deviceIds) {
        List<Device> devices = this.deviceMapper.selectBatchIds(deviceIds);
        for (Device device : devices) {
            // 删除设备分组。 租户、管理员和设备所有者
            this.deviceMapper.deleteDeviceGroupByDeviceId(device.getDeviceId());
            // 删除定时任务
            this.deviceJobService.deleteJobByDeviceId(Collections.singletonList(device.getDeviceId()));
            // 批量删除设备日志
            // this.logService.deleteDeviceLogByDeviceNumber(device.getDeviceNumber());
            this.deviceLogService.deleteDeviceLogByDeviceNumber(device.getDeviceNumber());
            // 删除设备告警记录
            this.alertLogService.deleteByDeviceId(device.getDeviceId());
            // 删除设备
            this.deviceMapper.deleteById(device.getDeviceId());
        }
    }

    /**
     * 生成设备编号
     */
    @Override
    public String generationDeviceNum() {
        // 设备编号：D + userId + 10位随机字母和数字
        User user = getLoginUser().getUser();
        String number = "D" + user.getUserId().toString() + RandomUtils.randomString(10);
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getDeviceNumber, number);
        Integer count = this.deviceMapper.selectCount(wrapper);
        if (count == 0) {
            return number;
        } else {
            return this.generationDeviceNum();
        }
    }

    /**
     * 更新设备的物模型
     */
    @Override
    public void reportDeviceThingsModelValue(Long productId, String deviceNum,
        List<ThingsModelItemBase> newThingsModelItemBaseList, int type, boolean isShadow) {
        // 根据设备编号查询设备信息
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getDeviceNumber, deviceNum);
        Device device = this.deviceMapper.selectOne(wrapper);

        // 设备原物模型值
        List<ThingsModelItemBase> oldThingsModelItemBaseList =
            JSON.parseArray(device.getThingsModelValue(), ThingsModelItemBase.class);

        // newThingsModelItemBaseList：设备最新上报的物模型值
        for (ThingsModelItemBase newThingsModelItemBase : newThingsModelItemBaseList) {
            for (ThingsModelItemBase oldThingsModelItemBase : oldThingsModelItemBaseList) {
                // 根据id找到对应的物模型值进行更新
                if (Objects.equals(newThingsModelItemBase.getModelId(), oldThingsModelItemBase.getModelId())) {
                    // 影子模式只更新影子值
                    if (!isShadow) {
                        oldThingsModelItemBase.setValue(newThingsModelItemBase.getValue());
                    }
                    oldThingsModelItemBase.setShadow(newThingsModelItemBase.getValue());

                    // TODO 场景联动、告警规则匹配处理

                    // 添加到设备日志，每更新一个物模型值就添加日志
                    DeviceLog deviceLog = new DeviceLog();
                    deviceLog.setDeviceId(device.getDeviceId());
                    deviceLog.setDeviceNumber(device.getDeviceNumber());
                    deviceLog.setDeviceName(device.getDeviceName());
                    deviceLog.setLogValue(newThingsModelItemBase.getValue());
                    deviceLog.setRemark(newThingsModelItemBase.getRemark());
                    deviceLog.setModelId(newThingsModelItemBase.getModelId());
                    deviceLog.setIsMonitor(
                        newThingsModelItemBase.getIsMonitor() == null ? 0 : newThingsModelItemBase.getIsMonitor());
                    deviceLog.setLogType(type);
                    deviceLog.setUserId(device.getUserId());
                    deviceLog.setUserName(device.getUserName());
                    deviceLog.setCreateTime(DateUtils.getNowDate());
                    // 1=影子模式，2=在线模式，3=其他
                    deviceLog.setMode(isShadow ? 1 : 2);
                    this.deviceLogService.save(deviceLog);
                    break;
                }
            }
        }
        // 更新数据库中设备的物模型值
        LambdaUpdateWrapper<Device> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Device::getDeviceId, device.getDeviceId()).set(Device::getThingsModelValue,
            JSONObject.toJSONString(oldThingsModelItemBaseList));
        this.deviceMapper.update(null, updateWrapper);
    }

    /**
     * Json物模型集合转换为对象中的分类集合
     *
     * @param device
     *            设备
     * @param isOnlyRead
     *            是否设置为只读
     */
    // @Async
    public void setThingsModelValue(DeviceVO device, boolean isOnlyRead) {
        List<ThingsModelItemBase> thingsModelItemBases =
            JSON.parseArray(device.getThingsModelValue(), ThingsModelItemBase.class);
        for (ThingsModelItemBase thingsModelItemBase : thingsModelItemBases) {
            String datatype = thingsModelItemBase.getDatatype();
            JSONObject specsJson = JSONObject.parseObject(thingsModelItemBase.getSpecs());
            if ("double".equals(datatype)) {
                DoubleModel doubleModel = new DoubleModel();
                BeanUtils.copyProperties(thingsModelItemBase, doubleModel);
                doubleModel.setMax(specsJson.getDouble("max"));
                doubleModel.setMin(specsJson.getDouble("min"));
                doubleModel.setStep(specsJson.getDouble("step"));
                doubleModel.setUnit(specsJson.getString("unit"));
                if (doubleModel.getIsMonitor() == 1 || isOnlyRead) {
                    ReadOnlyModelOutput readonlyModel = new ReadOnlyModelOutput();
                    BeanUtils.copyProperties(doubleModel, readonlyModel);
                    device.getReadOnlyList().add(readonlyModel);
                } else {
                    device.getDoubleList().add(doubleModel);
                }
            } else if ("integer".equals(datatype)) {
                IntegerModel integerModel = new IntegerModel();
                BeanUtils.copyProperties(thingsModelItemBase, integerModel);
                integerModel.setMax(specsJson.getInteger("max"));
                integerModel.setMin(specsJson.getInteger("min"));
                integerModel.setStep(specsJson.getInteger("step"));
                integerModel.setUnit(specsJson.getString("unit"));
                if (integerModel.getIsMonitor() == 1 || isOnlyRead) {
                    ReadOnlyModelOutput readonlyModel = new ReadOnlyModelOutput();
                    BeanUtils.copyProperties(integerModel, readonlyModel);
                    device.getReadOnlyList().add(readonlyModel);
                } else {
                    device.getIntegerList().add(integerModel);
                }
            } else if ("bool".equals(datatype)) {
                BoolModel boolModel = new BoolModel();
                BeanUtils.copyProperties(thingsModelItemBase, boolModel);
                boolModel.setFalseText(specsJson.getString("falseText"));
                boolModel.setTrueText(specsJson.getString("trueText"));
                if (boolModel.getIsMonitor() == 1 || isOnlyRead) {
                    ReadOnlyModelOutput readonlyModel = new ReadOnlyModelOutput();
                    BeanUtils.copyProperties(boolModel, readonlyModel);
                    device.getReadOnlyList().add(readonlyModel);
                } else {
                    device.getBoolList().add(boolModel);
                }
            } else if ("string".equals(datatype)) {
                StringModel stringModel = new StringModel();
                BeanUtils.copyProperties(thingsModelItemBase, stringModel);
                stringModel.setMaxLength(specsJson.getInteger("maxLength"));
                if (stringModel.getIsMonitor() == 1 || isOnlyRead) {
                    ReadOnlyModelOutput readonlyModel = new ReadOnlyModelOutput();
                    BeanUtils.copyProperties(stringModel, readonlyModel);
                    device.getReadOnlyList().add(readonlyModel);
                } else {
                    device.getStringList().add(stringModel);
                }
            } else if ("array".equals(datatype)) {
                ArrayModel arrayModel = new ArrayModel();
                BeanUtils.copyProperties(thingsModelItemBase, arrayModel);
                arrayModel.setArrayType(specsJson.getString("arrayType"));
                if (arrayModel.getIsMonitor() == 1 || isOnlyRead) {
                    ReadOnlyModelOutput readonlyModel = new ReadOnlyModelOutput();
                    BeanUtils.copyProperties(arrayModel, readonlyModel);
                    device.getReadOnlyList().add(readonlyModel);
                } else {
                    device.getArrayList().add(arrayModel);
                }
            } else if ("enum".equals(datatype)) {
                EnumModel enumModel = new EnumModel();
                BeanUtils.copyProperties(thingsModelItemBase, enumModel);
                List<EnumItem> enumItemList = JSON.parseArray(specsJson.getString("enumList"), EnumItem.class);
                enumModel.setEnumList(enumItemList);
                if (enumModel.getIsMonitor() == 1 || isOnlyRead) {
                    ReadOnlyModelOutput readonlyModel = new ReadOnlyModelOutput();
                    BeanUtils.copyProperties(enumModel, readonlyModel);
                    device.getReadOnlyList().add(readonlyModel);
                } else {
                    device.getEnumList().add(enumModel);
                }
            }

            // 排序
            device.setReadOnlyList(device.getReadOnlyList().stream()
                .sorted(Comparator.comparing(ThingsModelItemBase::getIsMonitor).reversed())
                .collect(Collectors.toList()));
        }
    }

    /**
     * 获取产品物模型值列表
     */
    private List<ThingsModelItemBase> getThingsModelListWithDefaultValue(Long productId) {
        /* thingsModelsJson: 
        [
        {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""},
        {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""},
        {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""},
        {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""},
        {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""}
        ]
         */
        // 获取物模型,设置默认值
        String thingsModelsJson = this.thingsModelService.getThingsModelCache(productId);
        List<ThingsModel> thingsModels = JSON.parseArray(thingsModelsJson, ThingsModel.class);
        return thingsModels.stream().map(thingsModel -> {
            ThingsModelItemBase thingsModelItemBase = new ThingsModelItemBase();
            BeanUtils.copyProperties(thingsModel, thingsModelItemBase);
            thingsModelItemBase.setValue("");
            thingsModelItemBase.setShadow("");
            return thingsModelItemBase;
        }).collect(Collectors.toList());
    }

    /**
     * 获取设备影子
     */
    @Override
    public ThingsModelShadow getDeviceShadowThingsModel(Device device) {
        // 产品物模型
        String thingsModelsJson = this.thingsModelService.getThingsModelCache(device.getProductId());
        List<ThingsModel> thingsModelsList = JSON.parseArray(thingsModelsJson, ThingsModel.class);

        List<ThingsModel> properties = new ArrayList<>();
        List<ThingsModel> functions = new ArrayList<>();

        for (ThingsModel thingsModel : thingsModelsList) {
            if (thingsModel.getType() == 1) {
                properties.add(thingsModel);
            } else if (thingsModel.getType() == 2) {
                functions.add(thingsModel);
            }
        }

        // 设备物模型值
        /*
        [
        {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""},
        {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""},
        {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""},
        {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""},
        {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""}
        ]
         */
        List<ThingsModelItemBase> thingsModelItemBaseList =
            JSON.parseArray(device.getThingsModelValue(), ThingsModelItemBase.class);

        // 查询出设置的影子值
        List<ThingsModelItemBase> shadowList = new ArrayList<>();
        for (ThingsModelItemBase thingsModelItemBase : thingsModelItemBaseList) {
            // 如果设备的影子值和物模型值不一样
            if (!thingsModelItemBase.getValue().equals(thingsModelItemBase.getShadow())) {
                shadowList.add(thingsModelItemBase);
            }
        }
        ThingsModelShadow shadow = new ThingsModelShadow();
        for (ThingsModelItemBase shadowItem : shadowList) {
            boolean isGetValue = false;
            for (ThingsModel property : properties) {
                if (property.getIsMonitor() == 0 && property.getModelId().equals(shadowItem.getModelId())) {
                    ModelIdAndValue item = new ModelIdAndValue(shadowItem.getModelId(), shadowItem.getShadow());
                    shadow.getProperties().add(item);
                    System.out.println("添加影子属性：" + item.getModelId());
                    isGetValue = true;
                    break;
                }
            }
            if (!isGetValue) {
                for (ThingsModel function : functions) {
                    if (function.getModelId().equals(shadowItem.getModelId())) {
                        ModelIdAndValue item = new ModelIdAndValue(shadowItem.getModelId(), shadowItem.getShadow());
                        shadow.getFunctions().add(item);
                        System.out.println("添加影子功能：" + item.getModelId());
                        break;
                    }
                }
            }
        }
        return shadow;
    }

    /**
     * @param device
     *            设备状态和定位更新
     * @return 结果
     */
    @Override

    public void updateDeviceStatusAndLocation(Device device, String ipAddress) {
        // 设置自动定位和状态
        if (!"".equals(ipAddress)) {
            if (device.getActiveTime() == null) {
                device.setActiveTime(DateUtils.getNowDate());
            }
            // 定位方式(1=ip自动定位，2=设备定位，3=自定义)
            if (device.getLocationWay() == 1) {
                device.setNetworkIp(ipAddress);
                this.setLocation(ipAddress, device);
            } else if (device.getLocationWay() == 2) {
                // 设备上报经纬度定位，不在此设置位置
                device.setAddress(null);
                device.setLatitude(null);
                device.setLongitude(null);
            } else {
                // 用户自定义经纬度，根据经纬度设置地址
                device.setAddress(BaiduMapUtils.getCity(device.getLatitude(), device.getLongitude()));
            }
        }
        this.deviceMapper.updateById(device);

        // 添加到设备日志
        DeviceLog deviceLog = new DeviceLog();
        deviceLog.setDeviceId(device.getDeviceId());
        deviceLog.setDeviceName(device.getDeviceName());
        deviceLog.setDeviceNumber(device.getDeviceNumber());
        deviceLog.setIsMonitor(0);
        deviceLog.setUserId(device.getUserId());
        deviceLog.setUserName(device.getUserName());
        deviceLog.setCreateTime(DateUtils.getNowDate());
        deviceLog.setCreateBy(getUsername());
        // 日志模式 1=影子模式，2=在线模式，3=其他
        deviceLog.setMode(3);
        if (device.getStatus() == 3) {
            deviceLog.setLogValue("1");
            deviceLog.setRemark("设备上线");
            deviceLog.setLogType(5);
        } else if (device.getStatus() == 4) {
            deviceLog.setLogValue("0");
            deviceLog.setRemark("设备离线");
            deviceLog.setLogType(6);
        }
        this.deviceLogService.save(deviceLog);
    }

    /**
     * 根据IP获取地址
     *
     */
    private void setLocation(String ip, Device device) {
        String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";
        // 内网不查询
        if (IpUtils.internalIp(ip)) {
            device.setAddress("内网IP");
        }
        try {
            String rspStr = HttpUtils.sendGet(IP_URL, "ip=" + ip + "&json=true", Constants.GBK);
            if (!StringUtils.isEmpty(rspStr)) {
                JSONObject obj = JSONObject.parseObject(rspStr);
                device.setAddress(obj.getString("addr"));
                log.info(device.getDeviceNumber() + "- 设置地址：" + obj.getString("addr"));
                // 设置经纬度
                Map<String, Double> lngAndLat = BaiduMapUtils.getLngAndLat(obj.getString("city"));
                device.setLongitude(lngAndLat.get("lng")); // 经度
                device.setLatitude(lngAndLat.get("lat")); // 维度
                log.info(device.getDeviceNumber() + "- 设置经度：" + lngAndLat.get("lng") + "，设置纬度：" + lngAndLat.get("lat"));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 上报设备信息
     */
    @Override

    public void reportDevice(Device newDevice, Device oldDevice) {
        // 未采用设备定位则清空定位，定位方式(1=ip自动定位，2=设备定位，3=自定义)
        if (oldDevice.getLocationWay() != 2) {
            newDevice.setLatitude(null);
            newDevice.setLongitude(null);
        }
        newDevice.setUpdateTime(DateUtils.getNowDate());
        newDevice.setUpdateBy(getUsername());
        // 更新激活时间
        if (oldDevice.getActiveTime() == null) {
            newDevice.setActiveTime(DateUtils.getNowDate());
        }
        // 不更新物模型
        newDevice.setThingsModelValue(null);
        // 不更新用户
        newDevice.setUserId(null);
        newDevice.setUserName(null);
        LambdaUpdateWrapper<Device> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Device::getDeviceNumber, newDevice.getDeviceNumber());
        this.deviceMapper.update(newDevice, wrapper);
    }
}
