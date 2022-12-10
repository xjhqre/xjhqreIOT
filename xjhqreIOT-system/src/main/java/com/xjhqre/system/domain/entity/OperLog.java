package com.xjhqre.system.domain.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xjhqre.common.base.BaseEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 操作日志记录
 * </p>
 *
 * @author xjhqre
 * @since 2022-10-03
 */

@TableName("sys_oper_log")
@Data
public class OperLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "日志主键")
    @TableId(value = "oper_id", type = IdType.AUTO)
    private Long operId;

    @ApiModelProperty(name = "模块标题")
    private String title;

    @ApiModelProperty(name = "业务类型（0其它 1新增 2修改 3删除）", example = "0")
    private Integer businessType;

    @ApiModelProperty(name = "方法名称")
    private String method;

    @ApiModelProperty(name = "请求方式")
    private String requestMethod;

    @ApiModelProperty(name = "操作人员")
    private String operName;

    @ApiModelProperty(name = "请求URL")
    private String operUrl;

    @ApiModelProperty(name = "主机地址")
    private String operIp;

    @ApiModelProperty(name = "操作地点")
    private String operLocation;

    @ApiModelProperty(name = "请求参数")
    private String operParam;

    @ApiModelProperty(name = "返回参数")
    private String jsonResult;

    @ApiModelProperty(name = "操作状态（1正常 0异常）", example = "0")
    private Integer status;

    @ApiModelProperty(name = "错误消息")
    private String errorMsg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(name = "操作时间")
    private Date operTime;

}
