package com.xjhqre.iot.domain.model.thingsModelItem;

import lombok.Data;

/**
 * 布尔类型
 */
@Data
public class BoolModel extends ThingsModelItemBase {

    /**
     * 0 值对应文本，如关闭
     */
    private String falseText;

    /**
     * 1 值对应文本，如开启
     */
    private String trueText;
}
