package com.xjhqre.common.domain.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 字典类型表 dict_type
 * 
 * @author xjhqre
 */
@Data
@TableName("sys_dict_type")
public class DictType extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 字典主键 */
    @ApiModelProperty(value = "字典类型ID", example = "0")
    @TableId(value = "dict_id", type = IdType.AUTO)
    private Long dictId;

    /** 字典名称 */
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典类型名称长度不能超过100个字符")
    @ApiModelProperty(value = "字典名称")
    private String dictName;

    /** 字典类型 */
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型类型长度不能超过100个字符")
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "字典类型必须以字母开头，且只能为（小写字母，数字，下滑线）")
    @ApiModelProperty(value = "字典类型")
    private String dictType;

    /** 状态（1正常 0停用） */
    @ApiModelProperty(value = "字典状态 1=正常,0=停用")
    private String status;
}
