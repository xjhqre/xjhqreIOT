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
import com.xjhqre.system.domain.entity.OperLog;
import com.xjhqre.system.mapper.OperLogMapper;
import com.xjhqre.system.service.OperLogService;

/**
 * 操作日志 服务层处理
 * 
 * @author xjhqre
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OperLogServiceImpl extends ServiceImpl<OperLogMapper, OperLog> implements OperLogService {
    @Autowired
    private OperLogMapper operLogMapper;

    /**
     * 根据条件分页查询操作日志记录
     * 
     * @param operLog
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public IPage<OperLog> findOperLog(OperLog operLog, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<OperLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(operLog.getOperId() != null, OperLog::getOperId, operLog.getOperId())
            .eq(operLog.getOperIp() != null, OperLog::getOperIp, operLog.getOperIp())
            .like(operLog.getOperName() != null, OperLog::getOperName, operLog.getOperName())
            .eq(operLog.getBusinessType() != null, OperLog::getBusinessType, operLog.getBusinessType())
            .eq(operLog.getMethod() != null, OperLog::getMethod, operLog.getMethod())
            .eq(operLog.getStatus() != null, OperLog::getStatus, operLog.getStatus());
        return this.operLogMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
    }

    /**
     * 查询系统操作日志集合
     * 
     * @param operLog
     *            操作日志对象
     * @return 操作日志集合
     */
    @Override
    public List<OperLog> selectOperLogList(OperLog operLog) {
        LambdaQueryWrapper<OperLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(operLog.getOperId() != null, OperLog::getOperId, operLog.getOperId())
            .eq(operLog.getOperIp() != null, OperLog::getOperIp, operLog.getOperIp())
            .like(operLog.getOperName() != null, OperLog::getOperName, operLog.getOperName())
            .eq(operLog.getBusinessType() != null, OperLog::getBusinessType, operLog.getBusinessType())
            .eq(operLog.getMethod() != null, OperLog::getMethod, operLog.getMethod())
            .eq(operLog.getStatus() != null, OperLog::getStatus, operLog.getStatus());
        return this.operLogMapper.selectList(queryWrapper);
    }

    /**
     * 批量删除系统操作日志
     * 
     * @param operIds
     *            需要删除的操作日志ID
     * @return 结果
     */
    @Override
    public int deleteOperLogByIds(Long[] operIds) {
        return this.operLogMapper.deleteBatchIds(Arrays.asList(operIds));
    }

    /**
     * 查询操作日志详细
     * 
     * @param operId
     *            操作ID
     * @return 操作日志对象
     */
    @Override
    public OperLog selectOperLogById(Long operId) {
        return this.operLogMapper.selectById(operId);
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperLog() {
        this.operLogMapper.cleanOperLog();
    }

    @Override
    public OperLog getInfo(Long operId) {
        return this.operLogMapper.selectById(operId);
    }
}
