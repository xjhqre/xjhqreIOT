package com.xjhqre.iot.domain.model.thingsModelItem;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

/**
 * 只读类型
 */
@Data
public class ReadOnlyModelOutput extends ThingsModelItemBase {
    /**
     * 最小值
     */
    private BigDecimal min;

    /**
     * 最大值
     */
    private BigDecimal max;

    /**
     * 步长
     */
    private BigDecimal step;

    /**
     * 单位
     */
    private String unit;

    /**
     * 数组元素类型
     */
    private String arrayType;

    /**
     * 0值对应文本，如关闭
     */
    private String falseText;

    /**
     * 1值对应文本，如开启
     */
    private String trueText;

    /**
     * 字符串最大长度
     */
    private int maxLength;

    /**
     * 枚举数组
     */
    private List<EnumItem> enumList;
}
