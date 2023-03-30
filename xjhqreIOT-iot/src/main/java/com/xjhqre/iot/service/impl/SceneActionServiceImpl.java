package com.xjhqre.iot.service.impl;

import org.springframework.stereotype.Service;

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
public class SceneActionServiceImpl extends ServiceImpl<SceneActionMapper, SceneAction> implements SceneActionService {}
