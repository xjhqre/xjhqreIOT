package com.xjhqre.iot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.iot.domain.entity.ThingsModel;

/**
 * 物模型Mapper接口
 * 
 * @author xjhqre
 * @date 2021-12-16
 */
@Mapper
public interface ThingsModelMapper extends BaseMapper<ThingsModel> {
    List<ThingsModel> listThingModelByProductId(@Param("productId") Long productId, @Param("type") Integer type);
}
