package com.xjhqre.iot.domain.model;

import lombok.Data;

/**
 * <p>
 * DataType
 * </p>
 *
 * @author xjhqre
 * @since 3月 09, 2023
 */
@Data
public class DataType {

    // integer、double
    private String type;

    private Specs specs;
}
