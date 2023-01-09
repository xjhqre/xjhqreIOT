package com.xjhqre.common.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xjhqre.common.domain.entity.Role;
import com.xjhqre.common.domain.entity.User;

import lombok.Data;

/**
 * <p>
 * UserDTO
 * </p>
 *
 * @author xjhqre
 * @since 12月 20, 2022
 */
@Data
public class UserDTO extends User {
    private static final long serialVersionUID = 1L;

    /**
     * 角色对象
     */
    @TableField(exist = false)
    private Role role;

    /**
     * 角色id
     */
    @TableField(exist = false)
    private Long roleId;
}
