package com.xjhqre.iot.domain.model.thingsModelItem;

import lombok.Data;

@Data
public class StringModel extends ThingsModelItemBase {

    /**
     * 字符串最大长度
     */
    private int maxLength;
}
