package com.xjhqre.iot.domain.model.thingsModelItem;

import lombok.Data;

/**
 * 数组类型
 */
@Data
public class ArrayModel extends ThingsModelItemBase {

    /**
     * 元素类型
     */
    private String arrayType;

}
