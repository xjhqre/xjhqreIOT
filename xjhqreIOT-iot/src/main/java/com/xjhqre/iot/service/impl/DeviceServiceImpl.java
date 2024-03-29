package com.xjhqre.iot.service.impl;

import static com.xjhqre.common.utils.SecurityUtils.getLoginUser;
import static com.xjhqre.common.utils.SecurityUtils.getUsername;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.constant.FileDirConstants;
import com.xjhqre.common.constant.FileTypeConstants;
import com.xjhqre.common.domain.entity.User;
import com.xjhqre.common.domain.model.LoginUser;
import com.xjhqre.common.exception.ServiceException;
import com.xjhqre.common.utils.*;
import com.xjhqre.common.utils.file.FileTypeUtils;
import com.xjhqre.common.utils.http.HttpUtils;
import com.xjhqre.common.utils.ip.IpUtils;
import com.xjhqre.common.utils.uuid.IdUtils;
import com.xjhqre.common.utils.uuid.RandomUtils;
import com.xjhqre.iot.constant.DeviceStatusConstant;
import com.xjhqre.iot.constant.LogTypeConstant;
import com.xjhqre.iot.constant.OtaStatusConstant;
import com.xjhqre.iot.domain.dto.UpgradeDeviceDTO;
import com.xjhqre.iot.domain.entity.*;
import com.xjhqre.iot.domain.model.DeviceStatistic;
import com.xjhqre.iot.domain.vo.DeviceVO;
import com.xjhqre.iot.mapper.DeviceMapper;
import com.xjhqre.iot.mqtt.EmqxService;
import com.xjhqre.iot.service.*;

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
    private AlertLogService alertLogService;
    @Resource
    private AlertService alertService;
    @Resource
    private ThingsModelValueService thingsModelValueService;
    @Resource
    private SceneService sceneService;
    @Resource
    private SceneActionService sceneActionService;
    @Resource
    private SceneTriggerService sceneTriggerService;
    @Resource
    private ChannelService channelService;
    @Resource
    private EmqxService emqxService;
    @Resource
    private DeviceFileService deviceFileService;

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
                device.getProductName())
            .eq(device.getGroupId() != null, Device::getGroupId, device.getGroupId());

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
        deviceWrapper.eq(Device::getStatus, DeviceStatusConstant.ON_LINE);
        Integer onlineDeviceCount = this.deviceMapper.selectCount(deviceWrapper);

        // 离线设备数量
        deviceWrapper = new LambdaQueryWrapper<>();
        deviceWrapper.eq(Device::getStatus, DeviceStatusConstant.OFF_LINE);
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
        device.setUpgradeStatus(OtaStatusConstant.UPGRADE_SUCCESSFUL); // 默认升级成功
        device.setFirmwareVersion(1.0); // 默认版本1.0
        Product product = this.productService.getById(device.getProductId());
        device.setImgUrl(product.getImgUrl());
        User user = getLoginUser().getUser();
        device.setNetworkIp(user.getLoginIp());
        // 定位方式, 1=ip自动定位，2=设备上报定位，3=自定义
        Integer locationWay = device.getLocationWay();
        if (locationWay != 3) {
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
        if (Objects.equals(status, DeviceStatusConstant.DISABLED)) { // 禁用设备
            device.setStatus(DeviceStatusConstant.DISABLED);
        } else { // 启动设备
            if (device.getActiveTime() != null) { // 已经激活过，设置为离线状态
                device.setStatus(DeviceStatusConstant.OFF_LINE);
            } else { // 未激活
                device.setStatus(DeviceStatusConstant.NOT_ACTIVE); // 设置未激活状态
            }
        }
        this.deviceMapper.updateById(device);
    }

    /**
     * 删除设备
     *
     */
    @Override
    public void delete(List<Long> deviceIds) {
        List<Device> devices = this.deviceMapper.selectBatchIds(deviceIds);

        for (Device device : devices) {
            // 检查场景联动
            List<SceneAction> sceneActionList = sceneActionService.listByDeviceId(device.getDeviceId());
            AssertUtils.isEmpty(sceneActionList, "该设备已在场景联动中使用，请先删除场景联动");
            List<SceneTrigger> sceneTriggerList = sceneTriggerService.listByDeviceId(device.getDeviceId());
            AssertUtils.isEmpty(sceneTriggerList, "该设备已在场景联动中使用，请先删除场景联动");
            // 删除设备视频通道
            List<Channel> channelList = channelService.listByDeviceId(device.getDeviceId());
            AssertUtils.isEmpty(channelList, "该设备已在视频监控中使用");
            // 删除设备分组
            this.deviceMapper.deleteDeviceGroupByDeviceId(device.getDeviceId());
            // 删除定时任务
            this.deviceJobService.deleteJobByDeviceId(Collections.singletonList(device.getDeviceId()));
            // 批量删除设备日志
            // this.logService.deleteDeviceLogByDeviceNumber(device.getDeviceNumber());
            this.deviceLogService.deleteDeviceLogByDeviceId(device.getDeviceId());
            // 删除设备物模型值日志
            this.thingsModelValueService.deleteByDeviceId(device.getDeviceId());
            // 删除设备告警记录
            this.alertLogService.deleteByDeviceId(device.getDeviceId());
            // 删除设备
            this.deviceMapper.deleteById(device.getDeviceId());
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
            ThingsModelValue thingsModelValue = this.deviceMapper.getLastModelValue(thingsModel.getModelId(), deviceId);
            if (thingsModelValue != null) {
                thingsModel.setLastValue(thingsModelValue.getValue());
                thingsModel.setCreateTime(thingsModelValue.getCreateTime());
            }
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
        wrapper.eq(Device::getStatus, DeviceStatusConstant.NOT_ACTIVE);
        int activateDeviceCount = this.count(wrapper);

        // 在线设备
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getStatus, DeviceStatusConstant.ON_LINE);
        int onlineDeviceCount = this.count(wrapper);

        // 禁用设备
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getStatus, DeviceStatusConstant.DISABLED);
        int disableDeviceCount = this.count(wrapper);

        // 离线设备
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getStatus, DeviceStatusConstant.OFF_LINE);
        int offlineDeviceCount = this.count(wrapper);

        Map<String, Integer> retMap = new HashMap<>();
        retMap.put("deviceTotalCount", deviceTotalCount);
        retMap.put("activateDeviceCount", activateDeviceCount);
        retMap.put("onlineDeviceCount", onlineDeviceCount);
        retMap.put("disableDeviceCount", disableDeviceCount);
        retMap.put("offlineDeviceCount", offlineDeviceCount);
        return retMap;
    }

    @Override
    public List<ThingsModel> listDeviceService(Long deviceId) {
        Device device = this.deviceMapper.selectById(deviceId);
        LambdaQueryWrapper<ThingsModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ThingsModel::getProductId, device.getProductId()).eq(ThingsModel::getType, 2);
        return this.thingsModelService.list(wrapper);
    }

    /**
     * 升级设备固件
     * 
     * @param dto
     */
    @Override
    public void upgradeDevice(UpgradeDeviceDTO dto) {
        // 修改设备升级状态
        Device device = deviceMapper.selectById(dto.getDeviceId());
        device.setUpgradeStatus(OtaStatusConstant.UPGRADING);
        deviceMapper.updateById(device);

        // 发送设备升级指令
        Product product = productService.getByDeviceId(device.getDeviceId());
        emqxService.publishOta(product.getProductKey(), device.getDeviceNumber(), JSON.toJSONString(dto));
    }

    /**
     * 设备上传图片
     *
     * @param deviceNumber
     * @param file
     */
    @Override
    public void uploadPhoto(String deviceNumber, MultipartFile file) {
        if (file == null) {
            throw new ServiceException("上传文件为空");
        }

        String extension = FileTypeUtils.getExtension(file.getOriginalFilename());

        // 生成文件编号（唯一）
        String number = IdUtils.simpleUUID();

        // 上传OSS
        String fileUrl = OSSUtil.upload(file, FileDirConstants.COMMON, number + extension);

        long fileSize = file.getSize();

        Device device = this.getByDeviceNumber(deviceNumber);

        DeviceFile deviceFile = new DeviceFile();
        deviceFile.setDeviceId(device.getDeviceId());
        deviceFile.setFileName(file.getOriginalFilename());
        deviceFile.setFileUrl(fileUrl);
        deviceFile.setFileSize(fileSize);
        deviceFile.setFileType(FileTypeConstants.PICTURE);
        deviceFile.setCreateTime(new Date());
        deviceFile.setUpdateTime(new Date());
        deviceFileService.save(deviceFile);
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
     * @param device
     *            设备状态和定位更新
     * @return 结果
     */
    @Override
    public void updateDeviceStatusAndLocation(Device device, String ip) {
        // 设置自动定位和状态
        if (!"".equals(ip)) {
            if (device.getActiveTime() == null) {
                device.setActiveTime(DateUtils.getNowDate());
            }
            // 定位方式(1=ip自动定位，2=设备定位，3=自定义)
            if (device.getLocationWay() == 1) {
                device.setNetworkIp(ip);
                this.setLocation(ip, device);
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
        deviceLog.setCreateTime(DateUtils.getNowDate());
        deviceLog.setCreateBy(getUsername());
        if (device.getStatus() == 3) {
            // deviceLog.setLogValue("1");
            deviceLog.setRemark("设备上线");
            deviceLog.setLogType(LogTypeConstant.DEVICE_ONLINE);
        } else if (device.getStatus() == 4) {
            // deviceLog.setLogValue("0");
            deviceLog.setRemark("设备离线");
            deviceLog.setLogType(LogTypeConstant.DEVICE_OFFLINE);
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
