package com.xjhqre.iot.mqtt;

import static com.xjhqre.common.utils.SecurityUtils.getUsername;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.DeviceLog;
import com.xjhqre.iot.domain.entity.Product;
import com.xjhqre.iot.domain.entity.ThingsModelValue;
import com.xjhqre.iot.domain.model.NtpModel;
import com.xjhqre.iot.domain.model.Topic;
import com.xjhqre.iot.domain.model.thingsModels.ModelIdAndValue;
import com.xjhqre.iot.service.DeviceLogService;
import com.xjhqre.iot.service.DeviceService;
import com.xjhqre.iot.service.OtaUpgradeLogService;
import com.xjhqre.iot.service.ProductService;
import com.xjhqre.iot.service.ThingsModelValueService;

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
    @Resource
    private ProductService productService;
    @Resource
    private ThingsModelValueService thingsModelValueService;
    @Resource
    private OtaUpgradeLogService otaUpgradeLogService;

    /**
     * 设备发布的主题，客户端订阅，格式：/productKey/deviceNumber/功能名/请求方式
     */
    private static final String prefix = "/+/+/";
    String sInfoTopic = prefix + "info/post";
    String sNtpTopic = prefix + "ntp/post";
    String sPropertyTopic = prefix + "property/post";
    String sFunctionTopic = prefix + "function/post";
    String sEventTopic = prefix + "event/post";
    String sShadowPropertyTopic = prefix + "property-offline/post";
    String sShadowFunctionTopic = prefix + "function-offline/post";
    String sOtaTopic = "/ota/get"; // 浏览器发布，后台/设备订阅

    /**
     * 客户端发布的主题，设备订阅
     */
    String pStatusTopic = "/status/get";
    String pInfoTopic = "/info/get";
    String pNtpTopic = "/ntp/get";
    String pPropertyTopic = "/property/get";
    String pFunctionTopic = "/function/get";

    /**
     * 订阅主题
     * 
     * @param client
     * @throws MqttException
     */
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
        // 订阅ota
        client.subscribe(this.sOtaTopic, 1);
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
        /*
        message：
        {
            "id": "temperature",  物模型标识符
            "value": "27.43",  // 值
            "remark": ""
        }
         */
        String message = new String(mqttMessage.getPayload());
        log.info("接收消息主题 : " + topic);
        log.info("接收消息Qos : " + mqttMessage.getQos());
        log.info("接收消息内容 : " + message);

        String[] topicItem = topic.substring(1).split("/");
        String productKey = topicItem[0];
        String deviceNum = topicItem[1];
        String type = topicItem[2]; // property、function、event
        switch (type) {
            case "info":
                this.updateDeviceInfo(productKey, deviceNum, message);
                break;
            case "ntp":
                this.publishNtp(productKey, deviceNum, message);
                break;
            case "property":
                this.getProperty(productKey, deviceNum, message);
                break;
            case "function":
                this.updateFunction(productKey, deviceNum, message);
                break;
            case "event":
                this.getEvent(deviceNum, message);
                break;
            case "ota":
                this.getOta(deviceNum, message);
                break;
            // case "property-offline":
            // this.updateProperty(productKey, deviceNum, message, true);
            // break;
            // case "function-offline":
            // this.updateFunction(productKey, deviceNum, message, true);
            // break;
        }
        this.recordDeviceLog(topic, message);
    }

    /**
     * 记录ota升级日志
     * 
     * @param deviceNum
     * @param message
     */
    private void getOta(String deviceNum, String message) {
        this.otaUpgradeLogService.recordOtaLog(deviceNum, message);
    }

    /**
     * 设备发布设备信息，客户端接收并更新
     */
    private void updateDeviceInfo(String productKey, String deviceNum, String message) {
        // 设备实体
        Device oldDevice = this.deviceService.getByDeviceNumber(deviceNum);
        // 上报设备信息
        Device newDevice = JSON.parseObject(message, Device.class);

        // 未采用设备定位则清空定位，定位方式(1=ip自动定位，2=设备上报定位，3=自定义)
        if (newDevice.getLongitude() != null) {
            oldDevice.setLongitude(newDevice.getLongitude());
        }
        if (newDevice.getLatitude() != null) {
            oldDevice.setLatitude(newDevice.getLatitude());
        }
        oldDevice.setUpdateTime(DateUtils.getNowDate());
        oldDevice.setUpdateBy(getUsername());
        // 更新激活时间
        this.deviceService.updateById(oldDevice);
        // 平台到设备消息
        this.publishStatus(productKey, deviceNum, 3, oldDevice.getIsShadow(), newDevice.getRssi());
    }

    /**
     * 更新后台设备属性
     *
     */
    private void getProperty(String productKey, String deviceNum, String message) {
        try {
            this.thingsModelValueService.add(productKey, deviceNum, message);
        } catch (Exception e) {
            log.error("接收属性数据，解析数据时异常 message={}", e.getMessage());
        }
    }

    /**
     * 上报功能
     *
     * @param message
     */
    private void updateFunction(String productKey, String deviceNum, String message) {
        try {
            List<ThingsModelValue> thingsModelItemBases = JSON.parseArray(message, ThingsModelValue.class);
            this.deviceService.reportDeviceThingsModelValue(productKey, deviceNum, thingsModelItemBases, 2, message);
        } catch (Exception e) {
            log.error("接收功能，解析数据时异常 message={}", e.getMessage());
        }
    }

    /**
     * 更新后台设备事件
     *
     */
    private void getEvent(String deviceNum, String message) {}

    // 异步记录设备日志
    @Async("scheduledExecutorService")
    void recordDeviceLog(String topic, String message) {
        String[] topicItem = topic.substring(1).split("/");
        String deviceNum = topicItem[1];
        log.info("线程：{}, 记录设备日志：", Thread.currentThread().getName());
        List<ThingsModelValue> thingsModelValueList = JSON.parseArray(message, ThingsModelValue.class);
        Device device = this.deviceService.getByDeviceNumber(deviceNum);
        for (ThingsModelValue thingsModelValue : thingsModelValueList) {
            if (StringUtils.isBlank(thingsModelValue.getValue())) {
                continue;
            }
            // 添加到设备日志
            DeviceLog deviceLog = new DeviceLog();
            deviceLog.setModelId(thingsModelValue.getModelId());
            deviceLog.setModelName(thingsModelValue.getModelName());
            deviceLog.setIdentifier(thingsModelValue.getIdentifier());
            deviceLog.setProductId(thingsModelValue.getProductId());
            deviceLog.setProductName(thingsModelValue.getProductName());
            deviceLog.setRouter(topic);
            deviceLog.setDeviceId(device.getDeviceId());
            deviceLog.setDeviceName(device.getDeviceName());
            deviceLog.setLogValue(thingsModelValue.getValue());
            deviceLog.setRemark(thingsModelValue.getRemark());
            deviceLog.setLogType(thingsModelValue.getType());
            deviceLog.setUserId(device.getUserId());
            deviceLog.setUserName(device.getUserName());
            deviceLog.setCreateTime(DateUtils.getNowDate());
            this.deviceLogService.save(deviceLog);
        }
    }

    /**
     * 1.发布设备状态
     */
    public void publishStatus(String productKey, String deviceNum, int deviceStatus, int isShadow, int rssi) {
        String message = "{\"status\":" + deviceStatus + ",\"isShadow\":" + isShadow + ",\"rssi\":" + rssi + "}";
        this.emqxClient.publish(1, false, "/" + productKey + "/" + deviceNum + this.pStatusTopic, message);
    }

    /**
     * 2.发布设备信息
     */
    public void publishInfo(String productKey, String deviceNum) {
        this.emqxClient.publish(1, false, "/" + productKey + "/" + deviceNum + this.pInfoTopic, "");
    }

    /**
     * 3.发布时钟同步信息
     *
     * @param message
     */
    private void publishNtp(String productKey, String deviceNum, String message) {
        NtpModel ntpModel = JSON.parseObject(message, NtpModel.class);
        ntpModel.setServerRecvTime(System.currentTimeMillis());
        ntpModel.setServerSendTime(System.currentTimeMillis());
        this.emqxClient.publish(1, false, "/" + productKey + "/" + deviceNum + this.pNtpTopic,
            JSON.toJSONString(ntpModel));
    }

    /**
     * 4.发布属性
     */
    public void publishProperty(String productKey, String deviceNum, List<ModelIdAndValue> thingsList) {
        if (thingsList == null) {
            this.emqxClient.publish(1, true, "/" + productKey + "/" + deviceNum + this.pPropertyTopic, "");
        } else {
            this.emqxClient.publish(1, true, "/" + productKey + "/" + deviceNum + this.pPropertyTopic,
                JSON.toJSONString(thingsList));
        }
    }

    /**
     * 5.发布功能
     */
    public void publishFunction(String productKey, String deviceNum, List<ModelIdAndValue> thingsList) {
        if (thingsList == null) {
            this.emqxClient.publish(1, true, "/" + productKey + "/" + deviceNum + this.pFunctionTopic, "");
        } else {
            this.emqxClient.publish(1, true, "/" + productKey + "/" + deviceNum + this.pFunctionTopic,
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
        Product product = this.productService.getById(device.getProductId());
        // 1-未激活，2-禁用，3-在线，4-离线
        if (device.getStatus() == 3) {
            device.setStatus(4);
            this.deviceService.updateById(device);
            // 发布设备信息
            this.publishInfo(product.getProductKey(), device.getDeviceNumber());
        }
        return device;
    }

    /**
     * 查询设备订阅的topic列表
     * 
     * @param deviceId
     * @return
     */
    public List<Topic> listDeviceTopic(Long deviceId) {
        Device device = this.deviceService.getById(deviceId);
        Product product = this.productService.getById(device.getProductId());
        String url = "http://1.15.88.204:18083/api/v4/subscriptions/";
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            log.info("sendGet - {}", url);
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            String authString = "admin:xjhqre";
            byte[] authEncBytes = Base64.getEncoder().encode(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            log.info("recv - {}", result);
        } catch (ConnectException e) {
            log.error("调用HttpUtils.sendGet ConnectException, url=" + url, e);
        } catch (SocketTimeoutException e) {
            log.error("调用HttpUtils.sendGet SocketTimeoutException, url=" + url, e);
        } catch (IOException e) {
            log.error("调用HttpUtils.sendGet IOException, url=" + url, e);
        } catch (Exception e) {
            log.error("调用HttpsUtil.sendGet Exception, url=" + url, e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                log.error("调用in.close Exception, url=" + url, ex);
            }
        }
        JSONObject jsonObject = JSONObject.parseObject(result.toString());
        String data = jsonObject.getString("data");
        return JSON.parseArray(data, Topic.class).stream()
            .filter(topic -> StringUtils.contains(topic.getTopic(), device.getDeviceNumber())
                && StringUtils.contains(topic.getTopic(), product.getProductKey()))
            .collect(Collectors.toList());
    }
}
