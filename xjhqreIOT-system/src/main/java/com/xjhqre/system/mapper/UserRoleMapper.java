package com.xjhqre.system.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.system.domain.entity.UserRole;

/**
 * 用户与角色关联表 数据层
 * 
 * @author xjhqre
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    /**
     * 通过角色ID查询角色使用数量
     * 
     * @param roleId
     *            角色ID
     * @return 结果
     */
    int countUserRoleByRoleId(Long roleId);
}
