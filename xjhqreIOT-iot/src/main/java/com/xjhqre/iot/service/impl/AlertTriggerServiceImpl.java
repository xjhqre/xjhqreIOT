package com.xjhqre.iot.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.iot.domain.entity.AlertTrigger;
import com.xjhqre.iot.mapper.AlertTriggerMapper;
import com.xjhqre.iot.service.AlertTriggerService;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * AlertTriggerServiceImpl
 * </p>
 *
 * @author xjhqre
 * @since 2月 27, 2023
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AlertTriggerServiceImpl extends ServiceImpl<AlertTriggerMapper, AlertTrigger>
    implements AlertTriggerService {}
