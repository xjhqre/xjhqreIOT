package com.xjhqre.system.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.common.domain.entity.Role;
import com.xjhqre.common.domain.entity.User;

/**
 * 用户 业务层
 * 
 * @author xjhqre
 */
public interface UserService extends IService<User> {

    /**
     * 根据条件分页查询用户列表
     * 
     * @param user
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<User> find(User user, Integer pageNum, Integer pageSize);

    User getDetail(Long userId);

    /**
     * 根据条件分页查询已分配用户角色列表
     * 
     * @param user
     *            用户信息
     * @return 用户信息集合信息
     */
    IPage<User> selectAllocatedUserList(User user, Integer pageNum, Integer pageSize);

    /**
     * 根据条件分页查询未分配用户角色列表
     * 
     * @param user
     *            用户信息
     * @return 用户信息集合信息
     */
    List<User> selectUnallocatedUserList(User user);

    /**
     * 通过用户名查询用户
     * 
     * @param userName
     *            用户名
     * @return 用户对象信息
     */
    User selectUserByUserName(String userName);

    /**
     * 根据用户ID查询用户所属角色组
     * 
     * @param userId
     *            用户名
     * @return 结果
     */
    List<Role> selectRolesByUserId(Long userId);

    /**
     * 校验用户名称是否唯一
     * 
     * @param user
     *            用户信息
     * @return 结果
     */
    Boolean checkUserNameUnique(User user);

    /**
     * 校验手机号码是否唯一
     *
     * @param user
     *            用户信息
     * @return 结果
     */
    Boolean checkPhoneUnique(User user);

    /**
     * 校验email是否唯一
     *
     * @param user
     *            用户信息
     * @return 结果
     */
    Boolean checkEmailUnique(User user);

    /**
     * 校验用户是否允许操作
     * 
     * @param userId
     *            用户信息
     */
    void checkUserAllowed(Long userId);

    /**
     * 新增用户信息
     * 
     * @param user
     *            用户信息
     * @return 结果
     */
    void insertUser(User user);

    /**
     * 修改用户信息
     * 
     * @param user
     *            用户信息
     * @return 结果
     */
    void updateUser(User user);

    void changeStatus(User user);

    /**
     * 批量删除用户信息
     * 
     * @param userIds
     *            需要删除的用户ID
     * @return 结果
     */
    void deleteUserByIds(List<Long> userIds);

    boolean registerUser(User user);

}
