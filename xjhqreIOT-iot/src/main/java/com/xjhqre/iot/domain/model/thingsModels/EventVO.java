package com.xjhqre.iot.domain.model.thingsModels;

import com.alibaba.fastjson2.JSONObject;

import lombok.Data;

/**
 * 事件，设备运行时，主动上报给云端的信息，一般包含需要被外部感知和处理的信息、告警和故障。事件中可包含多个输出参数。 例如，某项任务完成后的通知信息；设备发生故障时的温度、时间信息；设备告警时的运行状态等。 事件可以被订阅和推送。
 *
 * @author xjhqre
 * @date 2022-12-16
 */
@Data
public class EventVO {
    /**
     * 物模型唯一标识符
     */
    private String id;

    /**
     * 物模型名称
     */
    private String name;

    /**
     * 模型排序
     */
    private Integer sort;

    /**
     * 数据定义
     */
    private JSONObject datatype;
}
