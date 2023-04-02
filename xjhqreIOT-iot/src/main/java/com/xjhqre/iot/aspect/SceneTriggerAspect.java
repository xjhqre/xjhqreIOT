package com.xjhqre.iot.aspect;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xjhqre.common.exception.ServiceException;
import com.xjhqre.common.utils.AssertUtils;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.Scene;
import com.xjhqre.iot.domain.entity.SceneTrigger;
import com.xjhqre.iot.domain.entity.ThingsModelValue;
import com.xjhqre.iot.service.DeviceService;
import com.xjhqre.iot.service.SceneService;
import com.xjhqre.iot.service.SceneTriggerService;
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
public class SceneTriggerAspect {

    @Resource
    private DeviceService deviceService;
    @Resource
    private SceneService sceneService;
    @Resource
    private ThingsModelValueService thingsModelValueService;
    @Resource
    private SceneTriggerService sceneTriggerService;

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
        // String message = new String(mqttMessage.getPayload());
        // List<ThingsModelValue> thingsModelValues = JSON.parseArray(message, ThingsModelValue.class);
        //// 消息内的modelId
        // List<Long> modelIds = thingsModelValues.stream().filter(vo -> StringUtils.isNotBlank(vo.getValue()))
        // .map(ThingsModelValue::getModelId).collect(Collectors.toList());
        //// 触发器的modelId
        // List<Scene> scenes =
        // this.sceneService.list().stream().filter(vo -> vo.getStatus() == 1).collect(Collectors.toList());
        // List<Long> triggerModelIds = scenes.stream().map(scene -> {
        // LambdaQueryWrapper<SceneTrigger> wrapper = new LambdaQueryWrapper<>();
        // wrapper.eq(SceneTrigger::getSceneId, scene.getSceneId());
        // return this.sceneTriggerService.list(wrapper);
        // }).flatMap(Collection::stream).map(SceneTrigger::getModelId).collect(Collectors.toList());
        //// 如果触发器内的属性不包含消息内的属性，直接返回
        // if (!CollectionUtils.containsAny(triggerModelIds, modelIds)) {
        // return;
        // }
        log.info("场景aop收到topic：{}", topic);
        log.info("场景aop收到mqttMessage：{}", mqttMessage);
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

        List<Scene> sceneList =
            this.sceneService.list().stream().filter(vo -> vo.getStatus() == 1).collect(Collectors.toList());
        for (Scene scene : sceneList) {
            LambdaQueryWrapper<SceneTrigger> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(SceneTrigger::getSceneId, scene.getSceneId());
            List<SceneTrigger> sceneTriggers = this.sceneTriggerService.list(wrapper1);
            List<Boolean> flagList = new ArrayList<>();
            List<ThingsModelValue> message = new ArrayList<>();
            for (SceneTrigger trigger : sceneTriggers) {
                LambdaQueryWrapper<ThingsModelValue> wrapper2 = new LambdaQueryWrapper<>();
                // 找出触发器对应的最新物模型的值
                wrapper2.eq(ThingsModelValue::getModelId, trigger.getModelId())
                    .orderByDesc(ThingsModelValue::getCreateTime);
                ThingsModelValue thingsModelValue = this.thingsModelValueService.list(wrapper2).get(0); // 获取最新的值
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
            String restriction = scene.getRestriction(); // any or all
            if ("any".equals(restriction)) {
                if (flagList.stream().anyMatch(val -> val)) {
                    // 触发告警，添加告警日志
                    // this.addAlertLog(message, deviceId, alert);
                }
            } else if ("all".equals(restriction)) {
                if (flagList.stream().allMatch(val -> val)) {
                    // 触发告警，添加告警日志
                    // this.addAlertLog(message, deviceId, alert);
                }
            }
        }

    }

}
