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
 * 参数配置表 sys_config
 * 
 * @author xjhqre
 */
@Data
@TableName("sys_config")
public class Config extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 参数主键 */
    @ApiModelProperty(name = "配置ID", example = "0")
    @TableId(value = "config_id", type = IdType.AUTO)
    private Long configId;

    /** 参数名称 */
    @NotBlank(message = "参数名称不能为空")
    @Size(max = 100, message = "参数名称不能超过100个字符")
    @ApiModelProperty(name = "参数名称")
    private String configName;

    /** 参数键名 */
    @NotBlank(message = "参数键名长度不能为空")
    @Size(max = 100, message = "参数键名长度不能超过100个字符")
    @ApiModelProperty(name = "参数键名")
    private String configKey;

    /** 参数键值 */
    @NotBlank(message = "参数键值不能为空")
    @Size(max = 500, message = "参数键值长度不能超过500个字符")
    @ApiModelProperty(name = "参数键值")
    private String configValue;

    /** 系统内置（Y是 N否） */
    @ApiModelProperty(name = "系统内置（Y是 N否）")
    private String configType;
}
