package com.xjhqre.iot.domain.entity;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xjhqre.common.base.BaseEntity;
import com.xjhqre.common.group.Insert;
import com.xjhqre.common.group.Update;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备告警设置
 * 
 * @author xjhqre
 * @date 2022-12-19
 */
@Data
@TableName("iot_alert")
public class Alert extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 告警ID
     */
    @ApiModelProperty(value = "告警ID", hidden = true)
    @TableId(value = "alert_id", type = IdType.AUTO)
    private Long alertId;

    /**
     * 告警名称
     */
    @ApiModelProperty(value = "告警名称")
    @NotBlank(groups = {Insert.class, Update.class})
    private String alertName;

    /**
     * 产品ID
     */
    @ApiModelProperty(value = "产品ID")
    @NotNull(groups = {Insert.class, Update.class})
    private Long productId;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称")
    @NotBlank(groups = {Insert.class, Update.class})
    private String productName;

    /**
     * 触发器
     */
    @ApiModelProperty(value = "触发器")
    @TableField(exist = false)
    @NotEmpty(groups = {Insert.class, Update.class})
    private List<AlertTrigger> triggers;

    /**
     * 条件限制，部分满足或全部满足，值为 any 或者 all
     */
    @ApiModelProperty(value = "条件限制，部分满足或全部满足，值为 any 或者 all")
    @NotNull(groups = {Insert.class, Update.class})
    @JsonProperty("condition")
    private String restriction;

    /**
     * 告警状态 （1-启动，2-停止）
     */
    @ApiModelProperty(value = "告警状态 （1-启动，2-停止）")
    @NotNull(groups = {Insert.class, Update.class})
    private Integer status;
}
