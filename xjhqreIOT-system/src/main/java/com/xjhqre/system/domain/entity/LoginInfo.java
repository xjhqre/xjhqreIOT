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
 * 系统访问记录表 t_logininfor
 *
 * @author xjhqre
 */
@Data
@TableName("sys_login_info")
public class LoginInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "日志主键")
    @TableId(value = "info_id", type = IdType.AUTO)
    private Long infoId;

    @ApiModelProperty(name = "用户账号")
    private String userName;

    @ApiModelProperty(name = "登录状态 1成功 0失败")
    private String status;

    @ApiModelProperty(name = "登录IP地址")
    private String ipaddr;

    @ApiModelProperty(name = "登陆地点")
    private String loginLocation;

    @ApiModelProperty(name = "浏览器")
    private String browser;

    @ApiModelProperty(name = "操作系统")
    private String os;

    @ApiModelProperty(name = "提示消息")
    private String msg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(name = "访问时间")
    private Date loginTime;
}
