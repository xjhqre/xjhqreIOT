package com.xjhqre.iot.aspect;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
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
     * 在接收设备上报属性后执行
     *
     */
    @AfterReturning(
        pointcut = "execution(* com.xjhqre.iot.mqtt.EmqxService.getProperty(..))&&args(productKey, deviceNum, message)",
        returning = "resultValue", argNames = "productKey,deviceNum,message,resultValue")
    public void doAfterReturning(String productKey, String deviceNum, String message, Object resultValue) {
        log.info("告警aop触发，productKey: {}, deviceNum: {}, message: {}", productKey, deviceNum, message);
        AssertUtils.notEmpty(productKey, "产品key为空");
        AssertUtils.notEmpty(deviceNum, "设备编码为空");

        Device device = this.deviceService.getByDeviceNumber(deviceNum);
        Long deviceId = device.getDeviceId();
        Long productId = device.getProductId();

        // 获取产品下的告警
        List<Alert> alerts = this.alertService.getByProductId(productId);
        for (Alert alert : alerts) {
            List<Boolean> flagList = new ArrayList<>(); // 判断触发器条件是否全部达成
            List<ThingsModelValue> thingsModelValueList = new ArrayList<>(); // 用于记录在日志中
            for (AlertTrigger trigger : alert.getTriggers()) {
                ThingsModelValue thingsModelValue = this.thingsModelValueService.getNewValue(trigger.getModelId()); // 获取物模型最新的值
                if (thingsModelValue == null) {
                    break;
                }
                String operator = trigger.getOperator(); // 运算符
                String value = trigger.getValue(); // 触发值
                String value2 = thingsModelValue.getValue(); // 最新上传值
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
            String restriction = alert.getRestriction(); // any or all
            if ("any".equals(restriction)) {
                if (flagList.stream().anyMatch(val -> val)) {
                    // 触发告警，添加告警日志
                    this.addAlertLog(thingsModelValueList, deviceId, alert);
                }
            } else if ("all".equals(restriction)) {
                if (flagList.stream().allMatch(val -> val)) {
                    // 触发告警，添加告警日志
                    this.addAlertLog(thingsModelValueList, deviceId, alert);
                }
            }
        }
    }

    /**
     * 添加告警日志
     *
     */
    private void addAlertLog(List<ThingsModelValue> message, Long deviceId, Alert alert) {
        Device device = this.deviceService.getById(deviceId);
        AlertLog alertLog = new AlertLog();
        alertLog.setAlertId(alert.getAlertId());
        alertLog.setAlertName(alert.getAlertName());
        alertLog.setProductId(alert.getProductId());
        alertLog.setProductName(alert.getProductName());
        alertLog.setDeviceId(device.getDeviceId());
        alertLog.setDeviceName(device.getDeviceName());
        alertLog.setData(JSON.toJSONString(message));
        alertLog.setCreateTime(DateUtils.getNowDate());
        this.alertLogService.save(alertLog);
    }
}
