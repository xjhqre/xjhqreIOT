package com.xjhqre.iot.aspect;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.xjhqre.common.exception.ServiceException;
import com.xjhqre.common.utils.AssertUtils;
import com.xjhqre.iot.domain.entity.Scene;
import com.xjhqre.iot.domain.entity.SceneAction;
import com.xjhqre.iot.domain.entity.SceneLog;
import com.xjhqre.iot.domain.entity.SceneTrigger;
import com.xjhqre.iot.domain.entity.ThingsModelValue;
import com.xjhqre.iot.domain.model.thingsModels.ModelIdAndValue;
import com.xjhqre.iot.mqtt.EmqxClient;
import com.xjhqre.iot.service.SceneActionService;
import com.xjhqre.iot.service.SceneLogService;
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
    private SceneService sceneService;
    @Resource
    private ThingsModelValueService thingsModelValueService;
    @Resource
    private SceneTriggerService sceneTriggerService;
    @Resource
    private SceneActionService sceneActionService;
    @Resource
    private EmqxClient emqxClient;
    @Resource
    private SceneLogService sceneLogService;

    /**
     * 在接收设备上报属性后执行
     *
     */
    @AfterReturning(
        pointcut = "execution(* com.xjhqre.iot.mqtt.EmqxService.getProperty(..))&&args(productKey, deviceNum, message)",
        returning = "resultValue", argNames = "productKey,deviceNum,message,resultValue")
    public void doAfterReturning(String productKey, String deviceNum, String message, Object resultValue) {
        log.info("场景联动aop触发，productKey: {}, deviceNum: {}, message: {}", productKey, deviceNum, message);
        AssertUtils.notEmpty(productKey, "产品key为空");
        AssertUtils.notEmpty(deviceNum, "设备编码为空");

        // 获取有效的场景联动
        List<Scene> sceneList =
            this.sceneService.list().stream().filter(vo -> vo.getStatus() == 1).collect(Collectors.toList());
        for (Scene scene : sceneList) {
            List<SceneTrigger> sceneTriggers = this.sceneTriggerService.listBySceneId(scene.getSceneId());
            List<Boolean> flagList = new ArrayList<>(); // 场景触发器条件是否全部达成
            List<ThingsModelValue> thingsModelValueList = new ArrayList<>(); // 用于场景联动日志记录
            for (SceneTrigger trigger : sceneTriggers) {
                ThingsModelValue thingsModelValue = this.thingsModelValueService.getNewValue(trigger.getModelId()); // 获取最新的值
                if (thingsModelValue == null) {
                    break;
                }
                String operator = trigger.getOperator(); // 比较运算符
                String value = trigger.getValue(); // 触发器值
                String value2 = thingsModelValue.getValue(); // 设备上传新值
                boolean b;
                switch (operator) {
                    case ">":
                        b = Double.parseDouble(value2) > Double.parseDouble(value);
                        if (b) {
                            thingsModelValueList.add(thingsModelValue);
                        }
                        flagList.add(b);
                        break;
                    case ">=":
                        b = Double.parseDouble(value2) >= Double.parseDouble(value);
                        if (b) {
                            thingsModelValueList.add(thingsModelValue);
                        }
                        flagList.add(b);
                        break;
                    case "=":
                        b = value.equals(value2);
                        if (b) {
                            thingsModelValueList.add(thingsModelValue);
                        }
                        flagList.add(b);
                        break;
                    case "!=":
                        b = !value.equals(value2);
                        if (b) {
                            thingsModelValueList.add(thingsModelValue);
                        }
                        flagList.add(b);
                        break;
                    case "<":
                        b = Double.parseDouble(value2) < Double.parseDouble(value);
                        if (b) {
                            thingsModelValueList.add(thingsModelValue);
                        }
                        flagList.add(b);
                        break;
                    case "<=":
                        b = Double.parseDouble(value2) <= Double.parseDouble(value);
                        if (b) {
                            thingsModelValueList.add(thingsModelValue);
                        }
                        flagList.add(b);
                        break;
                    default:
                        throw new ServiceException("运算符错误");
                }
            }
            String restriction = scene.getRestriction(); // any or all
            if ("any".equals(restriction)) {
                if (flagList.stream().anyMatch(val -> val)) {
                    this.publishDeviceServiceMessage(thingsModelValueList, scene);
                }
            } else if ("all".equals(restriction)) {
                if (flagList.stream().allMatch(val -> val)) {
                    this.publishDeviceServiceMessage(thingsModelValueList, scene);
                }
            }
        }

    }

    /**
     * 发送服务调用消息至emqx
     */
    public void publishDeviceServiceMessage(List<ThingsModelValue> message, Scene scene) {
        // 添加场景联动日志
        this.addSceneLog(message, scene);
        // 发布服务调用消息
        for (SceneAction sceneAction : this.sceneActionService.listBySceneId(scene.getSceneId())) {
            ModelIdAndValue modelIdAndValue = new ModelIdAndValue();
            modelIdAndValue.setIdentifier(sceneAction.getIdentifier());
            modelIdAndValue.setValue(sceneAction.getValue());
            this.emqxClient.publish(1, false,
                "/" + sceneAction.getProductKey() + "/" + sceneAction.getDeviceNumber() + "/service/get",
                JSON.toJSONString(modelIdAndValue));
        }
    }

    private void addSceneLog(List<ThingsModelValue> message, Scene scene) {
        List<SceneAction> actions = this.sceneActionService.listBySceneId(scene.getSceneId());
        SceneLog sceneLog = new SceneLog();
        sceneLog.setSceneId(scene.getSceneId());
        sceneLog.setSceneName(scene.getSceneName());
        sceneLog.setTriggerData(JSON.toJSONString(message));
        sceneLog.setActions(JSON.toJSONString(actions));
        sceneLog.setCreateTime(new Date());
        this.sceneLogService.save(sceneLog);
    }

}
