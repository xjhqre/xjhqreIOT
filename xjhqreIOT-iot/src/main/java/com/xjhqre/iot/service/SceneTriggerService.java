package com.xjhqre.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.iot.domain.entity.SceneTrigger;

/**
 * <p>
 * SceneTriggerService
 * </p>
 *
 * @author xjhqre
 * @since 3月 29, 2023
 */
public interface SceneTriggerService extends IService<SceneTrigger> {
    List<SceneTrigger> listBySceneId(Long sceneId);

    List<SceneTrigger> listByDeviceId(Long deviceId);
}
