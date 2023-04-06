package com.xjhqre.iot.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.iot.domain.entity.SceneTrigger;
import com.xjhqre.iot.mapper.SceneTriggerMapper;
import com.xjhqre.iot.service.SceneTriggerService;
import org.springframework.transaction.annotation.Transactional;

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
public class SceneTriggerServiceImpl extends ServiceImpl<SceneTriggerMapper, SceneTrigger>
    implements SceneTriggerService {}
