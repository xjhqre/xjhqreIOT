package com.xjhqre.common.domain.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 字典数据表 sys_dict_data
 * 
 * @author xjhqre
 */
@Data
@TableName("sys_dict_data")
public class DictData extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 字典编码 */
    @ApiModelProperty(value = "字典编码", example = "0")
    @TableId(value = "dict_code", type = IdType.AUTO)
    private Long dictCode;

    /** 字典排序 */
    @ApiModelProperty(value = "字典排序", example = "0")
    private Long dictSort;

    /** 字典标签 */
    @NotBlank(message = "字典标签不能为空")
    @Size(min = 0, max = 100, message = "字典标签长度不能超过100个字符")
    @ApiModelProperty(value = "字典标签")
    private String dictLabel;

    /** 字典键值 */
    @NotBlank(message = "字典键值不能为空")
    @Size(max = 100, message = "字典键值长度不能超过100个字符")
    @ApiModelProperty(value = "字典键值")
    private String dictValue;

    /** 字典类型 */
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    @ApiModelProperty(value = "字典类型")
    private String dictType;

    /** 字典标签样式 */
    @Size(max = 100, message = "字典键值长度不能超过100个字符")
    @ApiModelProperty(value = "字典标签样式")
    private String listClass;

    /** 是否默认（Y是 N否） */
    @ApiModelProperty(value = "是否默认，Y=是,N=否")
    private String isDefault;

    /** 状态（0正常 1停用） */
    @ApiModelProperty(value = "状态，0=正常,1=停用")
    private String status;
}
