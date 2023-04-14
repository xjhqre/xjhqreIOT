package com.xjhqre.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.iot.domain.entity.SceneAction;

/**
 * <p>
 * SceneTriggerService
 * </p>
 *
 * @author xjhqre
 * @since 3月 29, 2023
 */
public interface SceneActionService extends IService<SceneAction> {
    List<SceneAction> listBySceneId(Long sceneId);
}
