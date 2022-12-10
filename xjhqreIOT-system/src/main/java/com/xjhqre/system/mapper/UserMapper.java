package com.xjhqre.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjhqre.common.domain.entity.User;

/**
 * 用户表 数据层
 * 
 * @author xjhqre
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 校验用户名称是否唯一
     * 
     * @param userName
     *            用户名称
     * @return 结果
     */
    User checkUserNameUnique(String userName);

    /**
     * 校验手机号码是否唯一
     *
     * @param mobile
     *            手机号码
     * @return 结果
     */
    User checkPhoneUnique(@Param("mobile") String mobile);

    /**
     * 校验email是否唯一
     *
     * @param email
     *            用户邮箱
     * @return 结果
     */
    User checkEmailUnique(String email);

    IPage<User> selectAllocatedUserList(@Param("objectPage") Page<User> objectPage, @Param("user") User user);

    List<User> selectUnallocatedUserList(User user);
}
