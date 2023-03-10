package com.xjhqre.iot.domain.model;

import java.util.List;

import lombok.Data;

/**
 * <p>
 * Specs
 * </p>
 *
 * @author xjhqre
 * @since 3月 09, 2023
 */
@Data
public class Specs {

    // 枚举值列表
    private List<Enum> enumList;

    // 列表类型：int、float
    private String arrayType;

    // 最小值
    private String min;

    // 最大值
    private String max;

    // 单位
    private String unit;

    // 步长
    private String step;

    // 文本长度
    private String maxLength;

    private String falseText;

    private String trueText;
}
