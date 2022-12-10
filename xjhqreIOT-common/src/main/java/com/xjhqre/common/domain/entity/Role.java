package com.xjhqre.common.domain.entity;

import java.io.Serializable;
import java.util.Set;

import javax.validation.constraints.NotBlank;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by lhr on 17-7-31.
 */

@Data
@TableName("sys_role")
public class Role extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "角色id", example = "0")
    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;

    @ApiModelProperty(name = "角色名称")
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @ApiModelProperty(name = "角色权限字符串")
    @NotBlank(message = "权限字符不能为空")
    private String roleKey;

    @NotBlank(message = "显示顺序不能为空")
    @ApiModelProperty(name = "显示顺序", example = "0")
    private Integer roleSort;

    @ApiModelProperty(name = "角色状态（0正常 1停用）", hidden = true)
    private String status;

    /**
     * 菜单组
     */
    @TableField(exist = false)
    private Long[] menuIds;

    /**
     * 角色菜单权限
     */
    @TableField(exist = false)
    private Set<String> permissions;

    public boolean isSuperAdmin() {
        return this.roleId != null && 100L == this.roleId;
    }
}
