package com.xjhqre.iot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.iot.domain.entity.SceneTrigger;

/**
 * <p>
 * SceneTriggerMapper
 * </p>
 *
 * @author xjhqre
 * @since 3月 29, 2023
 */
@Mapper
public interface SceneTriggerMapper extends BaseMapper<SceneTrigger> {
    List<SceneTrigger> listBySceneId(@Param("sceneId") Long sceneId);
}
