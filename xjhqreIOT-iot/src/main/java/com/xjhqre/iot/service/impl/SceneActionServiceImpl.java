package com.xjhqre.iot.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.iot.domain.entity.SceneAction;
import com.xjhqre.iot.mapper.SceneActionMapper;
import com.xjhqre.iot.service.SceneActionService;

/**
 * <p>
 * SceneTriggerServiceImpl
 * </p>
 *
 * @author xjhqre
 * @since 3月 29, 2023
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SceneActionServiceImpl extends ServiceImpl<SceneActionMapper, SceneAction> implements SceneActionService {

    @Resource
    private SceneActionMapper sceneActionMapper;

    @Override
    public List<SceneAction> listBySceneId(Long sceneId) {
        return this.sceneActionMapper.listBySceneId(sceneId);
    }

    @Override
    public List<SceneAction> listByDeviceId(Long deviceId) {
        return sceneActionMapper.listByDeviceId(deviceId);
    }
}
