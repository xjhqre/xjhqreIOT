package com.xjhqre.iot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.iot.domain.entity.AlertLog;

/**
 * 设备告警Mapper接口
 * 
 * @author xjhqre
 * @since 2023-01-6
 */
@Mapper
public interface AlertLogMapper extends BaseMapper<AlertLog> {
    List<AlertLog> getNewAlertLogList();

    int getTodayLogCount();

    int getMonthLogCount();
}
