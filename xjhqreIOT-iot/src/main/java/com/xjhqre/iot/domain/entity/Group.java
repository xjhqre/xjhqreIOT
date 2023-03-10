package com.xjhqre.iot.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备分组对象 iot_group
 * 
 * @author xjhqre
 * @date 2021-12-16
 */
@Data
@TableName("iot_group")
public class Group extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 分组ID
     */
    @ApiModelProperty(value = "分组ID")
    @TableId(value = "group_id", type = IdType.AUTO)
    private Long groupId;

    /**
     * 分组名称
     */
    @ApiModelProperty(value = "分组名称")
    private String groupName;

    /**
     * 分组排序
     */
    @ApiModelProperty(value = "分组排序")
    private Long groupOrder;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String userName;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag;
}
