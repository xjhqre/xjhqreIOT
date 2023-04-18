package com.xjhqre.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.iot.domain.entity.SceneLog;

/**
 * SceneLogService
 * 
 * @author xjhqre
 * @date 2023-04-115
 */
public interface SceneLogService extends IService<SceneLog> {
    IPage<SceneLog> find(SceneLog sceneLog, Integer pageNum, Integer pageSize);
}
