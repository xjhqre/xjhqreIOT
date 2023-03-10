package com.xjhqre.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 角色和菜单关联 sys_role_menu
 * 
 * @author xjhqre
 */
@Data
@TableName("sys_role_menu")
public class RoleMenu {
    /** 角色ID */
    @ApiModelProperty(value = "角色id")
    private Long roleId;

    /** 菜单ID */
    @ApiModelProperty(value = "菜单id")
    private Long menuId;
}
