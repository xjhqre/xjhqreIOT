package com.xjhqre.iot.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.iot.domain.dto.UpgradeDeviceDTO;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.ThingsModel;
import com.xjhqre.iot.domain.entity.ThingsModelValue;
import com.xjhqre.iot.domain.model.DeviceStatistic;
import com.xjhqre.iot.domain.vo.DeviceVO;

/**
 * DeviceService
 *
 * @author xjhqre
 * @date 2022-12-19
 */
public interface DeviceService extends IService<Device> {

    /**
     * 分页查询设备列表
     */
    IPage<DeviceVO> find(Device device, Integer pageNum, Integer pageSize);

    /**
     * 查询分组可添加设备分页列表
     */
    IPage<Device> findByGroup(Device device, Integer pageNum, Integer pageSize);

    /**
     * 查询所有设备
     */
    IPage<Device> selectAllDevice(Device device, Integer pageNum, Integer pageSize);

    /**
     * 查询设备列表
     */
    List<Device> list(Device device);

    /**
     * 查询设备详情
     */
    DeviceVO getDetail(Long deviceId);

    /**
     * 根据设备编号查询设备
     *
     * @param deviceNumber
     *            设备主键
     * @return 设备
     */
    Device getByDeviceNumber(String deviceNumber);

    /**
     * 查询设备统计信息
     *
     * @return 设备
     */
    DeviceStatistic getStatisticInfo();

    /**
     * 新增设备
     */
    void add(Device device);

    /**
     * 修改设备
     */
    void update(Device device);

    /**
     * 启用/禁用设备
     */
    void updateDeviceStatus(String deviceId, Integer status);

    /**
     * 删除设备
     */
    void delete(List<Long> deviceIds);

    /**
     * 上报设备的物模型
     *
     * @param productId
     *            产品id
     * @param deviceNum
     *            设备编号
     * @param thingsModelValues
     *            设备物模型值
     * @param type
     *            日志类型（1=属性上报，2=事件上报，3=调用功能，4=设备升级，5=设备上线，6=设备离线）
     * @param message
     *            消息
     */
    void reportDeviceThingsModelValue(String productId, String deviceNum, List<ThingsModelValue> thingsModelValues,
        int type, String message);

    /**
     * 获取设备设置的影子
     *
     * @param device
     * @return
     */
    // ThingsModelShadow getDeviceShadowThingsModel(Device device);

    /**
     * 更新设备状态和定位
     *
     * @param device
     *            设备
     * @return 结果
     */
    void updateDeviceStatusAndLocation(Device device, String ipAddress);

    /**
     * 查询设备属性记录
     * 
     * @param deviceId
     * @return
     */
    List<ThingsModel> listPropertiesWithLastValue(Long deviceId, String modelName);

    Map<String, Integer> getDeviceCount();

    List<ThingsModel> listDeviceService(Long deviceId);

    void upgradeDevice(UpgradeDeviceDTO dto);

    void uploadPhoto(String deviceNumber, MultipartFile file);
}
