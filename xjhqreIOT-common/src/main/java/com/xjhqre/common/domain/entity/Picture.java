package com.xjhqre.common.domain.entity;

import java.time.LocalDateTime;

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
@TableName("sys_picture")
@Data
public class Picture extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一id", hidden = true)
    @TableId(value = "picture_id", type = IdType.INPUT)
    private String pictureId;

    @ApiModelProperty(value = "图片名", hidden = true)
    private String picName;

    @ApiModelProperty(value = "图片地址", hidden = true)
    private String url;

    @ApiModelProperty(value = "图片描述")
    private String description;

    @ApiModelProperty(value = "上传人id", hidden = true)
    private Long uploader;

    @ApiModelProperty(value = "上传时间", hidden = true)
    private LocalDateTime uploadTime;

    @ApiModelProperty(value = "审核人", hidden = true)
    private Long approver;

    @ApiModelProperty(value = "审核时间", hidden = true)
    private LocalDateTime approvalTime;

    @ApiModelProperty(value = "状态（0：审核中、1：审核失败:2：已入库）", hidden = true)
    private Integer status;
}
