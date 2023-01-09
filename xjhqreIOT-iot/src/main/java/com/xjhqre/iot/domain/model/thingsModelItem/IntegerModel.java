package com.xjhqre.iot.domain.model.thingsModelItem;

import lombok.Data;

/**
 * 整数类型
 */
@Data
public class IntegerModel extends ThingsModelItemBase {

    /**
     * 最大值
     */
    private Integer min;

    /**
     * 最小值
     */
    private Integer max;

    /**
     * 步长
     */
    private Integer step;

    /**
     * 单位
     */
    private String unit;
}
