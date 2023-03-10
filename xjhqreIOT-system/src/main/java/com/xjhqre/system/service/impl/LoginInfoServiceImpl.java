package com.xjhqre.system.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.system.domain.entity.LoginInfo;
import com.xjhqre.system.mapper.LoginInfoMapper;
import com.xjhqre.system.service.LoginInfoService;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author xjhqre
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LoginInfoServiceImpl extends ServiceImpl<LoginInfoMapper, LoginInfo> implements LoginInfoService {

    @Autowired
    private LoginInfoMapper loginInfoMapper;

    /**
     * 根据条件分页查询登陆信息
     *
     * @param loginInfo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public IPage<LoginInfo> find(LoginInfo loginInfo, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<LoginInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(loginInfo.getInfoId() != null, LoginInfo::getInfoId, loginInfo.getInfoId())
            .like(loginInfo.getIpaddr() != null && !"".equals(loginInfo.getIpaddr()), LoginInfo::getIpaddr,
                loginInfo.getIpaddr())
            .eq(loginInfo.getStatus() != null && !"".equals(loginInfo.getStatus()), LoginInfo::getStatus,
                loginInfo.getStatus())
            .like(loginInfo.getUserName() != null && !"".equals(loginInfo.getUserName()), LoginInfo::getUserName,
                loginInfo.getUserName());
        return this.loginInfoMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
    }

    /**
     * 查询系统登录日志集合
     *
     * @param loginInfo
     *            访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<LoginInfo> selectLoginInfoList(LoginInfo loginInfo) {
        LambdaQueryWrapper<LoginInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(loginInfo.getInfoId() != null, LoginInfo::getInfoId, loginInfo.getInfoId())
            .eq(loginInfo.getIpaddr() != null && !"".equals(loginInfo.getIpaddr()), LoginInfo::getIpaddr,
                loginInfo.getIpaddr())
            .eq(loginInfo.getStatus() != null && !"".equals(loginInfo.getStatus()), LoginInfo::getStatus,
                loginInfo.getStatus())
            .like(loginInfo.getUserName() != null && !"".equals(loginInfo.getUserName()), LoginInfo::getUserName,
                loginInfo.getUserName());
        return this.loginInfoMapper.selectList(queryWrapper);
    }

    /**
     * 新增系统登录日志
     *
     * @param loginInfo
     *            访问日志对象
     */
    @Override
    public void insertLoginInfo(LoginInfo loginInfo) {
        // 此时 SecurityUtils 还没有内容
        // loginInfo.setCreateBy(SecurityUtils.getUsername());
        // loginInfo.setCreateTime(DateUtils.getNowDate());
        this.loginInfoMapper.insert(loginInfo);
    }

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds
     *            需要删除的登录日志ID
     * @return 结果
     */
    @Override
    public int delete(Long[] infoIds) {
        return this.loginInfoMapper.deleteBatchIds(Arrays.asList(infoIds));
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLoginInfo() {
        this.loginInfoMapper.cleanLoginInfo();
    }
}
