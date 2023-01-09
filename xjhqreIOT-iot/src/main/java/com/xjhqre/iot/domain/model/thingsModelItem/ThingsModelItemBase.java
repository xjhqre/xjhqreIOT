package com.xjhqre.iot.domain.model.thingsModelItem;

import com.xjhqre.iot.domain.entity.ThingsModel;

import lombok.Data;

/**
 * 物模型基础类型
 */
@Data
public class ThingsModelItemBase extends ThingsModel {

    /**
     * 物模型值
     */
    private String value;

    /**
     * 影子值
     */
    private String shadow;
}
