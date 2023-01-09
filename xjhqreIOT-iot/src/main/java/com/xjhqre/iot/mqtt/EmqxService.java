package com.xjhqre.iot.mqtt;

import java.util.List;

import javax.annotation.Resource;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.DeviceLog;
import com.xjhqre.iot.domain.model.NtpModel;
import com.xjhqre.iot.domain.model.thingsModelItem.ThingsModelItemBase;
import com.xjhqre.iot.domain.model.thingsModels.ModelIdAndValue;
import com.xjhqre.iot.service.DeviceLogService;
import com.xjhqre.iot.service.DeviceService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmqxService {

    @Resource
    private EmqxClient emqxClient;
    @Resource
    private DeviceService deviceService;
    @Resource
    protected DeviceLogService deviceLogService;

    /**
     * 订阅的主题，格式：/productId/deviceNum/功能名/请求方式
     */
    private static final String prefix = "/+/+/";
    String sInfoTopic = prefix + "info/post";
    String sNtpTopic = prefix + "ntp/post";
    String sPropertyTopic = prefix + "property/post";
    String sFunctionTopic = prefix + "function/post";
    String sEventTopic = prefix + "event/post";
    String sShadowPropertyTopic = prefix + "property-offline/post";
    String sShadowFunctionTopic = prefix + "function-offline/post";

    /**
     * 发布的主题
     */
    String pStatusTopic = "/status/post";
    String pInfoTopic = "/info/get";
    String pNtpTopic = "/ntp/get";
    String pPropertyTopic = "/property/get";
    String pFunctionTopic = "/function/get";

    public void subscribe(MqttAsyncClient client) throws MqttException {
        // 订阅设备信息
        client.subscribe(this.sInfoTopic, 1);
        // 订阅时钟同步
        client.subscribe(this.sNtpTopic, 1);
        // 订阅设备属性
        client.subscribe(this.sPropertyTopic, 1);
        // 订阅设备功能
        client.subscribe(this.sFunctionTopic, 1);
        // 订阅设备事件
        client.subscribe(this.sEventTopic, 1);
        // 订阅属性（影子模式）
        client.subscribe(this.sShadowPropertyTopic, 1);
        // 订阅功能（影子模式）
        client.subscribe(this.sShadowFunctionTopic, 1);
        log.info("mqtt订阅了设备信息和物模型主题");
    }

    /**
     * 订阅主题后收到消息的回调方法
     * 
     * @param topic
     *            主题
     * @param mqttMessage
     *            消息体
     */
    @Async
    public void subscribeCallback(String topic, MqttMessage mqttMessage) {

        // 测试线程池使用
        log.info("====>>>>线程名--{}", Thread.currentThread().getName());
        // 模拟耗时操作
        // Thread.sleep(1000);
        // subscribe后得到的消息会执行到这里面
        String message = new String(mqttMessage.getPayload());
        log.info("接收消息主题 : " + topic);
        log.info("接收消息Qos : " + mqttMessage.getQos());
        log.info("接收消息内容 : " + message);

        String[] topicItem = topic.substring(1).split("/");
        Long productId = Long.valueOf(topicItem[0]);
        String deviceNum = topicItem[1];
        String name = topicItem[2];
        switch (name) {
            case "info":
                this.updateDeviceInfo(productId, deviceNum, message);
                break;
            case "ntp":
                this.publishNtp(productId, deviceNum, message);
                break;
            case "property":
                this.updateProperty(productId, deviceNum, message, false);
                break;
            case "function":
                this.updateFunction(productId, deviceNum, message, false);
                break;
            case "event":
                this.updateEvent(deviceNum, message);
                break;
            case "property-offline":
                this.updateProperty(productId, deviceNum, message, true);
                break;
            case "function-offline":
                this.updateFunction(productId, deviceNum, message, true);
                break;
        }
    }

    /**
     * 上报设备信息
     */
    private void updateDeviceInfo(Long productId, String deviceNum, String message) {
        try {
            // 设备实体
            Device deviceEntity = this.deviceService.getByDeviceNumber(deviceNum);
            // 上报设备信息
            Device device = JSON.parseObject(message, Device.class);
            device.setProductId(productId);
            device.setDeviceNumber(deviceNum);
            this.deviceService.reportDevice(device, deviceEntity);
            // 发布设备状态
            this.publishStatus(productId, deviceNum, 3, deviceEntity.getIsShadow(), device.getRssi());
        } catch (Exception e) {
            log.error("接收设备信息，解析数据时异常 message={}", e.getMessage());
        }
    }

    /**
     * 更新后台设备属性
     *
     */
    private void updateProperty(Long productId, String deviceNum, String message, boolean isShadow) {
        try {
            List<ThingsModelItemBase> thingsModelItemBases = JSON.parseArray(message, ThingsModelItemBase.class);
            this.deviceService.reportDeviceThingsModelValue(productId, deviceNum, thingsModelItemBases, 1, isShadow);
        } catch (Exception e) {
            log.error("接收属性数据，解析数据时异常 message={}", e.getMessage());
        }
    }

    /**
     * 上报功能
     *
     * @param message
     */
    private void updateFunction(Long productId, String deviceNum, String message, boolean isShadow) {
        try {
            List<ThingsModelItemBase> thingsModelItemBases = JSON.parseArray(message, ThingsModelItemBase.class);
            this.deviceService.reportDeviceThingsModelValue(productId, deviceNum, thingsModelItemBases, 2, isShadow);
        } catch (Exception e) {
            log.error("接收功能，解析数据时异常 message={}", e.getMessage());
        }
    }

    /**
     * 更新后台设备事件
     *
     */
    private void updateEvent(String deviceNum, String message) {
        List<ThingsModelItemBase> thingsModelItemBases = JSON.parseArray(message, ThingsModelItemBase.class);
        Device device = this.deviceService.getByDeviceNumber(deviceNum);
        for (ThingsModelItemBase thingsModelItemBase : thingsModelItemBases) {
            // 添加到设备日志
            DeviceLog deviceLog = new DeviceLog();
            deviceLog.setDeviceId(device.getDeviceId());
            deviceLog.setDeviceName(device.getDeviceName());
            deviceLog.setLogValue(thingsModelItemBase.getValue());
            deviceLog.setRemark(thingsModelItemBase.getRemark());
            deviceLog.setDeviceNumber(device.getDeviceNumber());
            deviceLog.setModelId(thingsModelItemBase.getModelId());
            deviceLog.setLogType(3);
            deviceLog.setIsMonitor(0);
            deviceLog.setUserId(device.getUserId());
            deviceLog.setUserName(device.getUserName());
            deviceLog.setCreateTime(DateUtils.getNowDate());
            // 1=影子模式，2=在线模式，3=其他
            deviceLog.setMode(2);
            this.deviceLogService.save(deviceLog);
        }
    }

    /**
     * 1.发布设备状态
     */
    public void publishStatus(Long productId, String deviceNum, int deviceStatus, int isShadow, int rssi) {
        String message = "{\"status\":" + deviceStatus + ",\"isShadow\":" + isShadow + ",\"rssi\":" + rssi + "}";
        this.emqxClient.publish(1, false, "/" + productId + "/" + deviceNum + this.pStatusTopic, message);
    }

    /**
     * 2.发布设备信息
     */
    public void publishInfo(Long productId, String deviceNum) {
        this.emqxClient.publish(1, false, "/" + productId + "/" + deviceNum + this.pInfoTopic, "");
    }

    /**
     * 3.发布时钟同步信息
     *
     * @param message
     */
    private void publishNtp(Long productId, String deviceNum, String message) {
        NtpModel ntpModel = JSON.parseObject(message, NtpModel.class);
        ntpModel.setServerRecvTime(System.currentTimeMillis());
        ntpModel.setServerSendTime(System.currentTimeMillis());
        this.emqxClient.publish(1, false, "/" + productId + "/" + deviceNum + this.pNtpTopic,
            JSON.toJSONString(ntpModel));
    }

    /**
     * 4.发布属性
     */
    public void publishProperty(Long productId, String deviceNum, List<ModelIdAndValue> thingsList) {
        if (thingsList == null) {
            this.emqxClient.publish(1, true, "/" + productId + "/" + deviceNum + this.pPropertyTopic, "");
        } else {
            this.emqxClient.publish(1, true, "/" + productId + "/" + deviceNum + this.pPropertyTopic,
                JSON.toJSONString(thingsList));
        }
    }

    /**
     * 5.发布功能
     */
    public void publishFunction(Long productId, String deviceNum, List<ModelIdAndValue> thingsList) {
        if (thingsList == null) {
            this.emqxClient.publish(1, true, "/" + productId + "/" + deviceNum + this.pFunctionTopic, "");
        } else {
            this.emqxClient.publish(1, true, "/" + productId + "/" + deviceNum + this.pFunctionTopic,
                JSON.toJSONString(thingsList));
        }

    }

    /**
     * 设备数据同步
     *
     * @param deviceNumber
     *            设备编号
     * @return 设备
     */
    public Device deviceSynchronization(String deviceNumber) {
        Device device = this.deviceService.getByDeviceNumber(deviceNumber);
        // 1-未激活，2-禁用，3-在线，4-离线
        if (device.getStatus() == 3) {
            device.setStatus(4);
            this.deviceService.updateById(device);
            // 发布设备信息
            this.publishInfo(device.getProductId(), device.getDeviceNumber());
        }
        return device;
    }
}
