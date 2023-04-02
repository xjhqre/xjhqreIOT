package com.xjhqre.iot.aspect;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xjhqre.common.exception.ServiceException;
import com.xjhqre.common.utils.AssertUtils;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.iot.domain.entity.Alert;
import com.xjhqre.iot.domain.entity.AlertLog;
import com.xjhqre.iot.domain.entity.AlertTrigger;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.ThingsModelValue;
import com.xjhqre.iot.service.AlertLogService;
import com.xjhqre.iot.service.AlertService;
import com.xjhqre.iot.service.DeviceService;
import com.xjhqre.iot.service.ThingsModelValueService;

import lombok.extern.slf4j.Slf4j;

/**
 * 告警触发器AOP
 *
 * @author xjhqre
 */
@Aspect
@Component
@Slf4j
public class AlertTriggerAspect {

    @Resource
    private DeviceService deviceService;
    @Resource
    private AlertService alertService;
    @Resource
    private ThingsModelValueService thingsModelValueService;
    @Resource
    private AlertLogService alertLogService;

    /**
     * 处理完请求后执行 argNames属性使用了命名绑定模式，定义参数类型、个数和顺序，和args(下文有介绍)效果一样，只是argNames优先级高于args
     *
     */
    @AfterReturning(
        pointcut = "execution(* com.xjhqre.iot.mqtt.EmqxService.subscribeCallback(..))&&args(topic, mqttMessage)",
        returning = "resultValue", argNames = "topic,mqttMessage,resultValue")
    public void doAfterReturning(String topic, MqttMessage mqttMessage, Object resultValue) {
        String[] topicItem = topic.substring(1).split("/");
        String type = topicItem[2]; // property、service、event
        if (!"property".equals(type)) {
            return;
        }
        log.info("告警aop收到topic：{}", topic);
        log.info("告警aop收到mqttMessage：{}", mqttMessage);
        String productKey = topicItem[0];
        String deviceNum = topicItem[1];
        AssertUtils.notEmpty(productKey, "产品key为空");
        AssertUtils.notEmpty(deviceNum, "设备编码为空");
        AssertUtils.notEmpty(type, "物模型类型为空");

        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getDeviceNumber, deviceNum);
        Device device = this.deviceService.getOne(wrapper);
        Long deviceId = device.getDeviceId();
        Long productId = device.getProductId();

        List<Alert> alerts = this.alertService.getByProductId(productId);
        for (Alert alert : alerts) {
            List<Boolean> flagList = new ArrayList<>();
            List<ThingsModelValue> message = new ArrayList<>();
            for (AlertTrigger trigger : alert.getTriggers()) {
                LambdaQueryWrapper<ThingsModelValue> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.eq(ThingsModelValue::getModelId, trigger.getModelId())
                    .orderByDesc(ThingsModelValue::getCreateTime);
                ThingsModelValue thingsModelValue = this.thingsModelValueService.list(wrapper1).get(0); // 获取最新的值
                if (thingsModelValue != null) {
                    String operator = trigger.getOperator();
                    String value = trigger.getValue();
                    String value2 = thingsModelValue.getValue();
                    boolean b;
                    switch (operator) {
                        case ">":
                            b = Double.parseDouble(value2) > Double.parseDouble(value);
                            if (b) {
                                message.add(thingsModelValue);
                            }
                            flagList.add(b);
                            break;
                        case ">=":
                            b = Double.parseDouble(value2) >= Double.parseDouble(value);
                            if (b) {
                                message.add(thingsModelValue);
                            }
                            flagList.add(b);
                            break;
                        case "=":
                            b = value.equals(value2);
                            if (b) {
                                message.add(thingsModelValue);
                            }
                            flagList.add(b);
                            break;
                        case "!=":
                            b = !value.equals(value2);
                            if (b) {
                                message.add(thingsModelValue);
                            }
                            flagList.add(b);
                            break;
                        case "<":
                            b = Double.parseDouble(value2) < Double.parseDouble(value);
                            if (b) {
                                message.add(thingsModelValue);
                            }
                            flagList.add(b);
                            break;
                        case "<=":
                            b = Double.parseDouble(value2) <= Double.parseDouble(value);
                            if (b) {
                                message.add(thingsModelValue);
                            }
                            flagList.add(b);
                            break;
                        default:
                            throw new ServiceException("运算符错误");
                    }
                }
            }
            String restriction = alert.getRestriction(); // any or all
            if ("any".equals(restriction)) {
                if (flagList.stream().anyMatch(val -> val)) {
                    // 触发告警，添加告警日志
                    this.addAlertLog(message, deviceId, alert);
                }
            } else if ("all".equals(restriction)) {
                if (flagList.stream().allMatch(val -> val)) {
                    // 触发告警，添加告警日志
                    this.addAlertLog(message, deviceId, alert);
                }
            }
        }
    }

    /**
     * 添加告警日志
     *
     * @param message
     * @param deviceId
     * @param alert
     */
    private void addAlertLog(List<ThingsModelValue> message, Long deviceId, Alert alert) {
        Device device = this.deviceService.getById(deviceId);
        AlertLog alertLog = new AlertLog();
        alertLog.setAlertName(alert.getAlertName());
        alertLog.setProductId(alert.getProductId());
        alertLog.setProductName(alert.getProductName());
        alertLog.setDeviceId(device.getDeviceId());
        alertLog.setDeviceName(device.getDeviceName());
        alertLog.setUserId(device.getUserId());
        alertLog.setUserName(device.getUserName());
        alertLog.setData(JSON.toJSONString(message));
        alertLog.setCreateTime(DateUtils.getNowDate());
        this.alertLogService.save(alertLog);
    }
}
