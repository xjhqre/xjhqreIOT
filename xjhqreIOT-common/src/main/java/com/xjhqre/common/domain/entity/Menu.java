package com.xjhqre.common.domain.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("sys_menu")
public class Menu extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "权限ID", example = "0")
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    @ApiModelProperty(name = "权限名称")
    private String menuName;

    @ApiModelProperty(name = "父菜单ID", example = "0")
    private Long parentId;

    @NotNull(message = "显示顺序不能为空")
    @ApiModelProperty(name = "显示顺序", example = "0")
    private Integer orderNum;

    @Size(max = 200, message = "路由地址不能超过200个字符")
    @ApiModelProperty(name = "路由地址")
    private String path;

    @Size(max = 200, message = "组件路径不能超过255个字符")
    @ApiModelProperty(name = "组件路径")
    private String component;

    @NotBlank(message = "菜单类型不能为空")
    @ApiModelProperty(name = "菜单类型（M目录 C菜单 F按钮）")
    private String menuType;

    @ApiModelProperty(name = "菜单状态（1正常 0停用）")
    private String status;

    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    @ApiModelProperty(name = "权限标识")
    private String perms;

    /**
     * 子菜单
     */
    @TableField(exist = false)
    private List<Menu> children = new ArrayList<>();
}
