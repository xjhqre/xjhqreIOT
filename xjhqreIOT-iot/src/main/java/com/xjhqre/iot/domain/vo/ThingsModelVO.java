package com.xjhqre.iot.domain.vo;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.iot.domain.entity.DeviceProp;
import com.xjhqre.iot.domain.entity.ThingsModel;

import lombok.Data;

/**
 * 物模型对象 iot_things_model
 * 
 * @author xjhqre
 * @since 2023-2-8
 */
@Data
@TableName("iot_things_model")
public class ThingsModelVO extends ThingsModel {

    private List<DeviceProp> devicePropList;

    private DeviceProp lastValue;
}
