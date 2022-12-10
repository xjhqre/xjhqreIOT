package com.xjhqre.system.domain.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 通知公告表 sys_notice
 * 
 * @author xjhqre
 */
@Data
@TableName("sys_notice")
public class Notice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 公告ID */
    @ApiModelProperty(name = "公告ID", example = "0")
    @TableId(value = "notice_id", type = IdType.AUTO)
    private Long noticeId;

    /** 公告标题 */
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 50, message = "公告标题不能超过50个字符")
    @ApiModelProperty(name = "公告标题", example = "0")
    private String noticeTitle;

    /** 公告类型（1通知 2公告） */
    @ApiModelProperty(name = "公告类型（1通知 2公告）", example = "0")
    private String noticeType;

    /** 公告内容 */
    @ApiModelProperty(name = "公告内容", example = "0")
    private String noticeContent;

    /** 公告状态（0正常 1关闭） */
    @ApiModelProperty(name = "公告状态（0正常 1关闭）", example = "0")
    private String status;
}
