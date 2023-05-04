package com.xjhqre.iot.constant;

/**
 * <p>
 * 设备日志类型常量
 * </p>
 *
 * @author xjhqre
 * @since 10月 24, 2022
 */
public class LogTypeConstant {

    // 属性上报。
    public static final Integer ATTRIBUTE_REPORTING = 1;

    // 服务调用
    public static final Integer SERVICE_CALL = 2;

    // 事件上报
    public static final Integer EVENT_REPORTING = 3;

    // 设备上线
    public static final Integer DEVICE_ONLINE = 4;

    // 设备离线
    public static final Integer DEVICE_OFFLINE = 5;

    // 设备信息上报
    public static final Integer INFO_REPORTING = 6;

    // OTA升级
    public static final Integer CALL_OTA = 7;

    // 定时任务
    public static final Integer TIMED_TASK = 8;
}
