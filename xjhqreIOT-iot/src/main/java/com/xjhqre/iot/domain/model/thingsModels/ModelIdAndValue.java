package com.xjhqre.iot.domain.model.thingsModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 物模型值
 *
 * @author xjhqre
 * @since 2023-1-7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelIdAndValue {

    /**
     * 物模型标识符
     */
    private String identifier;

    /** 物模型值 */
    private String value;
}
