package com.xjhqre.common.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xjhqre.common.domain.entity.Picture;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * PictureDTO
 * </p>
 *
 * @author xjhqre
 * @since 2月 02, 2023
 */
@Data
public class PictureDTO extends Picture {

    /**
     * 产品ID
     */
    @ApiModelProperty(value = "产品ID", hidden = true)
    @TableField(exist = false)
    private Long productId;

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id", hidden = true)
    @TableField(exist = false)
    private Long deviceId;
}
