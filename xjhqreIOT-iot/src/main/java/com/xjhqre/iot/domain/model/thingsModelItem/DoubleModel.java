package com.xjhqre.iot.domain.model.thingsModelItem;

import lombok.Data;

/**
 * 小数类型
 */
@Data
public class DoubleModel extends ThingsModelItemBase {

    /**
     * 最小值
     */
    private Double min;

    /**
     * 最大值
     */
    private Double max;

    /**
     * 步长
     */
    private Double step;

    /**
     * 单位
     */
    private String unit;
}
