package com.xjhqre.iot.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.iot.domain.entity.SceneTrigger;
import com.xjhqre.iot.mapper.SceneTriggerMapper;
import com.xjhqre.iot.service.SceneTriggerService;

/**
 * <p>
 * SceneTriggerServiceImpl
 * </p>
 *
 * @author xjhqre
 * @since 3月 29, 2023
 */
@Service
public class SceneTriggerServiceImpl extends ServiceImpl<SceneTriggerMapper, SceneTrigger>
    implements SceneTriggerService {}