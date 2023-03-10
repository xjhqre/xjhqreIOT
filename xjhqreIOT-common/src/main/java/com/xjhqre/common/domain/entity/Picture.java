package com.xjhqre.common.domain.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 图片表
 * </p>
 *
 * @author xjhqre
 * @since 2022-10-25
 */

@ApiModel(value = "Picture对象", description = "图片表")
@TableName("iot_picture")
@Data
public class Picture extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一id", hidden = true)
    @TableId(value = "picture_id", type = IdType.INPUT)
    private String pictureId;

    @ApiModelProperty(value = "图片编号", hidden = true)
    private String number;

    @ApiModelProperty(value = "图片名", hidden = true)
    private String name;

    @ApiModelProperty(value = "图片地址", hidden = true)
    private String url;

    @ApiModelProperty(value = "图片描述")
    private String description;

    @ApiModelProperty(value = "图片类型（1：头像 2：产品图片 3：监控截图）")
    private Integer type;

    @ApiModelProperty(value = "上传人id", hidden = true)
    private Long uploader;

    @ApiModelProperty(value = "上传时间", hidden = true)
    private Date uploadTime;
}
