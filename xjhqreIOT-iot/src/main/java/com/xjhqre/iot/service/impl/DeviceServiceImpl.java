package com.xjhqre.iot.service.impl;

import static com.xjhqre.common.utils.SecurityUtils.getLoginUser;
import static com.xjhqre.common.utils.SecurityUtils.getUsername;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.domain.entity.User;
import com.xjhqre.common.domain.model.LoginUser;
import com.xjhqre.common.utils.BaiduMapUtils;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.common.utils.http.HttpUtils;
import com.xjhqre.common.utils.ip.IpUtils;
import com.xjhqre.common.utils.uuid.RandomUtils;
import com.xjhqre.iot.domain.entity.Alert;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.DeviceLog;
import com.xjhqre.iot.domain.entity.Product;
import com.xjhqre.iot.domain.entity.Scene;
import com.xjhqre.iot.domain.entity.ThingsModel;
import com.xjhqre.iot.domain.entity.ThingsModelValue;
import com.xjhqre.iot.domain.model.DeviceStatistic;
import com.xjhqre.iot.domain.model.thingsModelItem.ThingsModelItemBase;
import com.xjhqre.iot.domain.vo.DeviceVO;
import com.xjhqre.iot.mapper.DeviceMapper;
import com.xjhqre.iot.mqtt.EmqxService;
import com.xjhqre.iot.service.AlertLogService;
import com.xjhqre.iot.service.AlertService;
import com.xjhqre.iot.service.DeviceLogService;
import com.xjhqre.iot.service.DeviceService;
import com.xjhqre.iot.service.ProductService;
import com.xjhqre.iot.service.SceneService;
import com.xjhqre.iot.service.ThingsModelValueService;

import lombok.extern.slf4j.Slf4j;

/**
 * 设备Service业务层处理
 *
 * @author xjhqre
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
    @Resource
    private AlertService alertService;
    @Resource
    private ThingsModelValueService thingsModelValueService;
    @Resource
    private SceneService sceneService;

    @Override
    public IPage<DeviceVO> find(Device device, Integer pageNum, Integer pageSize) {
        LoginUser user = getLoginUser();
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(device.getDeviceId() != null, Device::getDeviceId, device.getDeviceId())
            .eq(device.getDeviceNumber() != null && !"".equals(device.getDeviceNumber()), Device::getDeviceNumber,
                device.getDeviceNumber())
            .like(device.getDeviceName() != null && !"".equals(device.getDeviceName()), Device::getDeviceName,
                device.getDeviceName())
            .eq(device.getProductId() != null, Device::getProductId, device.getProductId())
            .like(device.getProductName() != null && !"".equals(device.getProductName()), Device::getProductName,
                device.getProductName());

        if (!SecurityUtils.isAdmin(user.getUserId())) {
            wrapper.eq(Device::getUserId, SecurityUtils.getUserId());
        }

        Page<Device> devicePage = this.deviceMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return devicePage.convert(item -> {
            DeviceVO deviceVO = new DeviceVO();
            BeanUtils.copyProperties(item, deviceVO);
            // 解析物模型值JSON为集合
            // this.setThingsModelValue(deviceVO, false);
            return deviceVO;
        });
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
            .eq(device.getDeviceNumber() != null && !"".equals(device.getDeviceNumber()), Device::getDeviceNumber,
                device.getDeviceNumber())
            .like(device.getDeviceName() != null && !"".equals(device.getDeviceName()), Device::getDeviceName,
                device.getDeviceName())
            .eq(device.getProductId() != null, Device::getProductId, device.getProductId())
            .like(device.getProductName() != null && !"".equals(device.getProductName()), Device::getProductName,
                device.getProductName());
        return this.deviceMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<Device> list(Device device) {
        LoginUser user = getLoginUser();
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(device.getDeviceId() != null, Device::getDeviceId, device.getDeviceId())
            .eq(device.getDeviceNumber() != null && !"".equals(device.getDeviceNumber()), Device::getDeviceNumber,
                device.getDeviceNumber())
            .like(device.getDeviceName() != null && !"".equals(device.getDeviceName()), Device::getDeviceName,
                device.getDeviceName())
            .eq(device.getProductId() != null, Device::getProductId, device.getProductId())
            .like(device.getProductName() != null && !"".equals(device.getProductName()), Device::getProductName,
                device.getProductName());

        if (!SecurityUtils.isAdmin(user.getUserId())) {
            wrapper.eq(Device::getUserId, SecurityUtils.getUserId());
        }
        return this.deviceMapper.selectList(wrapper);
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
        Long productId = device.getProductId();
        Product product = this.productService.getById(productId);
        deviceVO.setProductKey(product.getProductKey());
        deviceVO.setNetworkMethod(product.getNetworkMethod());
        // 物模型转换为对象中的不同类别集合
        // this.setThingsModelValue(deviceVO, false);
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

        // 已发布产品数量
        productWrapper = new LambdaQueryWrapper<>();
        productWrapper.eq(Product::getStatus, 2);
        int pulishedProductCount = this.productService.count(productWrapper);

        // 查询设备数量
        LambdaQueryWrapper<Device> deviceWrapper = new LambdaQueryWrapper<>();
        if (!isAdmin) {
            deviceWrapper.eq(Device::getUserId, user.getUserId());
        }
        Integer deviceCount = this.deviceMapper.selectCount(deviceWrapper);

        // 在线设备数量
        deviceWrapper = new LambdaQueryWrapper<>();
        deviceWrapper.eq(Device::getStatus, 3);
        Integer onlineDeviceCount = this.deviceMapper.selectCount(deviceWrapper);

        // 离线设备数量
        deviceWrapper = new LambdaQueryWrapper<>();
        deviceWrapper.eq(Device::getStatus, 4);
        Integer offlineDeviceCount = this.deviceMapper.selectCount(deviceWrapper);

        // 查询告警配置数量
        int alertCount = this.alertService.count();

        // 正常告警配置数量
        LambdaQueryWrapper<Alert> alertLambdaQueryWrapper = new LambdaQueryWrapper<>();
        alertLambdaQueryWrapper.eq(Alert::getStatus, 1);
        int enableAlertCount = this.alertService.count(alertLambdaQueryWrapper);

        // 禁用告警配置数量
        int disableAlertCount = alertCount - enableAlertCount;

        // 查询今日告警日志数量
        int dayAlertLogCount = this.alertLogService.getTodayLogCount();

        // 查询当月告警日志数量
        int monthAlertLogCount = this.alertLogService.getMonthLogCount();

        // 场景联动数量
        int sceneCount = this.sceneService.count();

        // 正常场景联动
        LambdaQueryWrapper<Scene> sceneLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sceneLambdaQueryWrapper.eq(Scene::getStatus, 1);
        int enableSceneCount = this.sceneService.count(sceneLambdaQueryWrapper);

        // 禁用场景联动数量
        int disableSceneCount = sceneCount - enableSceneCount;

        DeviceStatistic statistic = new DeviceStatistic();
        statistic.setProductCount(productCount);
        statistic.setPublishedProductCount(pulishedProductCount);
        statistic.setUnPublishedProductCount(productCount - pulishedProductCount);
        statistic.setDeviceCount(deviceCount);
        statistic.setAlertCount(alertCount);
        statistic.setEnableAlertCount(enableAlertCount);
        statistic.setDisableAlertCount(disableAlertCount);
        statistic.setOnlineDeviceCount(onlineDeviceCount);
        statistic.setOfflineDeviceCount(offlineDeviceCount);
        statistic.setDayAlertLogCount(dayAlertLogCount);
        statistic.setMonthAlertLogCount(monthAlertLogCount);
        statistic.setSceneCount(sceneCount);
        statistic.setEnableSceneCount(enableSceneCount);
        statistic.setDisableSceneCount(disableSceneCount);
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
        User sysUser = getLoginUser().getUser();
        // 添加设备
        device.setCreateTime(DateUtils.getNowDate());
        device.setCreateBy(getUsername());
        device.setDeviceNumber(RandomUtils.randomString(16)); // 16位设备编号
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
        device.setUpdateTime(DateUtils.getNowDate());
        device.setUpdateBy(getUsername());
        device.setProductId(null);
        device.setProductName(null);
        this.deviceMapper.updateById(device);
    }

    /**
     * 启用/禁用设备
     *
     */
    @Override
    public void updateDeviceStatus(String deviceId, Integer status) {
        Device device = this.deviceMapper.selectById(deviceId);
        if (status == 2) { // 禁用设备
            device.setStatus(2);
        } else { // 启动设备
            if (device.getActiveTime() != null) { // 已经激活过，设置为离线状态
                device.setStatus(3);
            } else { // 未激活
                device.setStatus(1); // 设置未激活状态
            }
        }
        this.deviceMapper.updateById(device);
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
        // TODO 校验场景联动是否使用
        for (Device device : devices) {
            // 删除设备分组。 租户、管理员和设备所有者
            this.deviceMapper.deleteDeviceGroupByDeviceId(device.getDeviceId());
            // 删除定时任务
            this.deviceJobService.deleteJobByDeviceId(Collections.singletonList(device.getDeviceId()));
            // 批量删除设备日志
            // this.logService.deleteDeviceLogByDeviceNumber(device.getDeviceNumber());
            this.deviceLogService.deleteDeviceLogByDeviceId(device.getDeviceId());
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
     * 查询设备属性记录
     * 
     * @param deviceId
     * @return
     */
    @Override
    public List<ThingsModel> listPropertiesWithLastValue(Long deviceId, String modelName) {
        Device device = this.getById(deviceId);
        Product product = this.productService.getById(device.getProductId());
        LambdaQueryWrapper<ThingsModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ThingsModel::getProductId, product.getProductId()).eq(ThingsModel::getType, 1)
            .like(modelName != null && !"".equals(modelName), ThingsModel::getModelName, modelName);
        List<ThingsModel> thingsModelList = this.thingsModelService.list(wrapper);

        return thingsModelList.stream().peek(thingsModel -> {
            // 设置最新的物模型值
            String value = this.deviceMapper.getLastModelValue(thingsModel.getModelId(), thingsModel.getProductId());
            thingsModel.setLastValue(value);
        }).collect(Collectors.toList());

    }

    /**
     * 获取各种状态设备数量
     * 
     * @return
     */
    @Override
    public Map<String, Integer> getDeviceCount() {
        // 设备总数
        int deviceTotalCount = this.count();

        // 激活设备
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(Device::getStatus, 1);
        int activateDeviceCount = this.count(wrapper);

        // 在线设备
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getStatus, 3);
        int onlineDeviceCount = this.count(wrapper);

        // 禁用设备
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getStatus, 2);
        int disableDeviceCount = this.count(wrapper);

        // 离线设备
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getStatus, 4);
        int offlineDeviceCount = this.count(wrapper);

        Map<String, Integer> retMap = new HashMap<>();
        retMap.put("deviceTotalCount", deviceTotalCount);
        retMap.put("activateDeviceCount", activateDeviceCount);
        retMap.put("onlineDeviceCount", onlineDeviceCount);
        retMap.put("disableDeviceCount", disableDeviceCount);
        retMap.put("offlineDeviceCount", offlineDeviceCount);
        return retMap;
    }

    /**
     * 依据订阅的消息更新设备属性
     *
     * @param productKey
     *            产品key
     * @param deviceNum
     *            设备编号
     * @param thingsModelValues
     *            设备物模型值
     * @param type
     *            日志类型（1=属性上报，2=事件上报，3=调用功能，4=设备升级，5=设备上线，6=设备离线）
     * @param message
     *            消息
     */
    @Override
    public void reportDeviceThingsModelValue(String productKey, String deviceNum,
        List<ThingsModelValue> thingsModelValues, int type, String message) {
        // 根据设备编号查询设备信息
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getDeviceNumber, deviceNum);
        Device device = this.deviceMapper.selectOne(wrapper);

        LambdaQueryWrapper<Product> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(Product::getProductKey, productKey);
        Product product = this.productService.getOne(wrapper1);

        thingsModelValues = thingsModelValues.stream().peek(vo -> {
            String identifier = vo.getIdentifier();
            LambdaQueryWrapper<ThingsModel> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(ThingsModel::getProductId, product.getProductId()).eq(ThingsModel::getIdentifier, identifier);
            ThingsModel thingsModel = this.thingsModelService.getOne(wrapper2);
            vo.setModelId(thingsModel.getModelId());
            vo.setModelName(thingsModel.getModelName());
            vo.setType(1);
            vo.setMessage(message);
            vo.setCreateTime(DateUtils.getNowDate());
        }).collect(Collectors.toList());

        // 保存物模型值
        this.thingsModelValueService.saveBatch(thingsModelValues);

        //// 更新数据库中设备的物模型值
        // LambdaUpdateWrapper<Device> updateWrapper = new LambdaUpdateWrapper<>();
        // updateWrapper.eq(Device::getDeviceId, device.getDeviceId()).set(Device::getThingsModelValue,
        // JSONObject.toJSONString(oldThingsModelItemBaseList));
        // this.deviceMapper.update(null, updateWrapper);
        //
        //// 添加到设备日志，每更新一个物模型值就添加日志
        // this.recordDeviceLog(type, message, device, product);
        //
        //// 告警规则匹配
        // this.alarmRuleMatching(message, device, product, oldThingsModelItemBaseList);
    }

    /**
     * 记录设备日志
     * 
     * @param type
     * @param message
     * @param device
     * @param product
     */
    private void recordDeviceLog(int type, String message, Device device, Product product) {
        DeviceLog deviceLog = new DeviceLog();
        deviceLog.setDeviceId(device.getDeviceId());
        deviceLog.setDeviceName(device.getDeviceName());
        deviceLog.setProductId(product.getProductId());
        deviceLog.setProductName(product.getProductName());
        deviceLog.setLogValue(message);
        deviceLog.setLogType(type);
        deviceLog.setUserId(device.getUserId());
        deviceLog.setUserName(device.getUserName());
        deviceLog.setCreateTime(DateUtils.getNowDate());
        this.deviceLogService.save(deviceLog);
    }

    /// **
    // * Json物模型集合转换为对象中的分类集合
    // *
    // * @param device
    // * 设备
    // * @param isOnlyRead
    // * 是否设置为只读
    // */
    //// @Async
    // public void setThingsModelValue(DeviceVO device, boolean isOnlyRead) {
    // List<ThingsModelItemBase> thingsModelItemBases =
    // JSON.parseArray(device.getThingsModelValue(), ThingsModelItemBase.class);
    // for (ThingsModelItemBase thingsModelItemBase : thingsModelItemBases) {
    // JSONObject dataTypeJson = JSONObject.parseObject(thingsModelItemBase.getDataType());
    // String type = dataTypeJson.getString("type"); // 物模型类型（double， Integer ...）
    // JSONObject specs = dataTypeJson.getJSONObject("specs");
    // if ("double".equals(type)) {
    // DoubleModel doubleModel = new DoubleModel();
    // BeanUtils.copyProperties(thingsModelItemBase, doubleModel);
    // doubleModel.setMax(specs.getDouble("max"));
    // doubleModel.setMin(specs.getDouble("min"));
    // doubleModel.setStep(specs.getDouble("step"));
    // doubleModel.setUnit(specs.getString("unit"));
    // if (doubleModel.getIsMonitor() == 1 || isOnlyRead) {
    // ReadOnlyModelOutput readonlyModel = new ReadOnlyModelOutput();
    // BeanUtils.copyProperties(doubleModel, readonlyModel);
    // device.getReadOnlyList().add(readonlyModel);
    // } else {
    // device.getDoubleList().add(doubleModel);
    // }
    // } else if ("integer".equals(type)) {
    // IntegerModel integerModel = new IntegerModel();
    // BeanUtils.copyProperties(thingsModelItemBase, integerModel);
    // integerModel.setMax(specs.getInteger("max"));
    // integerModel.setMin(specs.getInteger("min"));
    // integerModel.setStep(specs.getInteger("step"));
    // integerModel.setUnit(specs.getString("unit"));
    // if (integerModel.getIsMonitor() == 1 || isOnlyRead) {
    // ReadOnlyModelOutput readonlyModel = new ReadOnlyModelOutput();
    // BeanUtils.copyProperties(integerModel, readonlyModel);
    // device.getReadOnlyList().add(readonlyModel);
    // } else {
    // device.getIntegerList().add(integerModel);
    // }
    // } else if ("bool".equals(type)) {
    // BoolModel boolModel = new BoolModel();
    // BeanUtils.copyProperties(thingsModelItemBase, boolModel);
    // boolModel.setFalseText(specs.getString("falseText"));
    // boolModel.setTrueText(specs.getString("trueText"));
    // if (boolModel.getIsMonitor() == 1 || isOnlyRead) {
    // ReadOnlyModelOutput readonlyModel = new ReadOnlyModelOutput();
    // BeanUtils.copyProperties(boolModel, readonlyModel);
    // device.getReadOnlyList().add(readonlyModel);
    // } else {
    // device.getBoolList().add(boolModel);
    // }
    // } else if ("string".equals(type)) {
    // StringModel stringModel = new StringModel();
    // BeanUtils.copyProperties(thingsModelItemBase, stringModel);
    // stringModel.setMaxLength(specs.getInteger("maxLength"));
    // if (stringModel.getIsMonitor() == 1 || isOnlyRead) {
    // ReadOnlyModelOutput readonlyModel = new ReadOnlyModelOutput();
    // BeanUtils.copyProperties(stringModel, readonlyModel);
    // device.getReadOnlyList().add(readonlyModel);
    // } else {
    // device.getStringList().add(stringModel);
    // }
    // } else if ("array".equals(type)) {
    // ArrayModel arrayModel = new ArrayModel();
    // BeanUtils.copyProperties(thingsModelItemBase, arrayModel);
    // arrayModel.setArrayType(specs.getString("arrayType"));
    // if (arrayModel.getIsMonitor() == 1 || isOnlyRead) {
    // ReadOnlyModelOutput readonlyModel = new ReadOnlyModelOutput();
    // BeanUtils.copyProperties(arrayModel, readonlyModel);
    // device.getReadOnlyList().add(readonlyModel);
    // } else {
    // device.getArrayList().add(arrayModel);
    // }
    // } else if ("enum".equals(type)) {
    // EnumModel enumModel = new EnumModel();
    // BeanUtils.copyProperties(thingsModelItemBase, enumModel);
    // List<EnumItem> enumItemList = JSON.parseArray(specs.getString("enumList"), EnumItem.class);
    // enumModel.setEnumList(enumItemList);
    // if (enumModel.getIsMonitor() == 1 || isOnlyRead) {
    // ReadOnlyModelOutput readonlyModel = new ReadOnlyModelOutput();
    // BeanUtils.copyProperties(enumModel, readonlyModel);
    // device.getReadOnlyList().add(readonlyModel);
    // } else {
    // device.getEnumList().add(enumModel);
    // }
    // }
    //
    // device.setReadOnlyList(device.getReadOnlyList());
    // }
    // }

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
        List<ThingsModel> thingsModelList = this.thingsModelService.listThingModelByProductId(productId);
        return thingsModelList.stream().map(thingsModel -> {
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
    // @Override
    // public ThingsModelShadow getDeviceShadowThingsModel(Device device) {
    // // 产品物模型
    // String thingsModelsJson = this.thingsModelService.getThingsModelCache(device.getProductId());
    // List<ThingsModel> thingsModelsList = JSON.parseArray(thingsModelsJson, ThingsModel.class);
    //
    // List<ThingsModel> properties = new ArrayList<>();
    // List<ThingsModel> functions = new ArrayList<>();
    //
    // for (ThingsModel thingsModel : thingsModelsList) {
    // if (thingsModel.getType() == 1) {
    // properties.add(thingsModel);
    // } else if (thingsModel.getType() == 2) {
    // functions.add(thingsModel);
    // }
    // }
    //
    // // 设备物模型值
    // /*
    // [
    // {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""},
    // {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""},
    // {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""},
    // {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""},
    // {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""}
    // ]
    // */
    // List<ThingsModelItemBase> thingsModelItemBaseList =
    // JSON.parseArray(device.getThingsModelValue(), ThingsModelItemBase.class);
    //
    // // 查询出设置的影子值
    // List<ThingsModelItemBase> shadowList = new ArrayList<>();
    // for (ThingsModelItemBase thingsModelItemBase : thingsModelItemBaseList) {
    // // 如果设备的影子值和物模型值不一样
    // if (!thingsModelItemBase.getValue().equals(thingsModelItemBase.getShadow())) {
    // shadowList.add(thingsModelItemBase);
    // }
    // }
    // ThingsModelShadow shadow = new ThingsModelShadow();
    // for (ThingsModelItemBase shadowItem : shadowList) {
    // boolean isGetValue = false;
    // for (ThingsModel property : properties) {
    // if (property.getModelId().equals(shadowItem.getModelId())) {
    // ModelIdAndValue item = new ModelIdAndValue(shadowItem.getModelId(), shadowItem.getShadow());
    // shadow.getProperties().add(item);
    // System.out.println("添加影子属性：" + item.getModelId());
    // isGetValue = true;
    // break;
    // }
    // }
    // if (!isGetValue) {
    // for (ThingsModel function : functions) {
    // if (function.getModelId().equals(shadowItem.getModelId())) {
    // ModelIdAndValue item = new ModelIdAndValue(shadowItem.getModelId(), shadowItem.getShadow());
    // shadow.getFunctions().add(item);
    // System.out.println("添加影子功能：" + item.getModelId());
    // break;
    // }
    // }
    // }
    // }
    // return shadow;
    // }

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
        deviceLog.setUserId(device.getUserId());
        deviceLog.setUserName(device.getUserName());
        deviceLog.setCreateTime(DateUtils.getNowDate());
        deviceLog.setCreateBy(getUsername());
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
}
