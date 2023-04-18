package com.xjhqre.iot.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.iot.domain.entity.SceneLog;
import com.xjhqre.iot.mapper.SceneLogMapper;
import com.xjhqre.iot.service.SceneLogService;

/**
 * SceneLogServiceImpl
 * 
 * @author xjhqre
 * @date 2023-04-15
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SceneLogServiceImpl extends ServiceImpl<SceneLogMapper, SceneLog> implements SceneLogService {

    @Resource
    private SceneLogMapper sceneLogMapper;

    @Override
    public IPage<SceneLog> find(SceneLog sceneLog, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<SceneLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(sceneLog.getSceneName() != null && !"".equals(sceneLog.getSceneName()), SceneLog::getSceneName,
            sceneLog.getSceneName()).eq(sceneLog.getSceneId() != null, SceneLog::getSceneId, sceneLog.getSceneId());
        return this.sceneLogMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
}
