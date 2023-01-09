package com.xjhqre.iot.domain.model.thingsModelItem;

import java.util.List;

import lombok.Data;

/**
 * 枚举数组
 */
@Data
public class EnumModel extends ThingsModelItemBase {
    private List<EnumItem> enumList;
}
