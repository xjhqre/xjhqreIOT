package com.xjhqre.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.common.domain.entity.DictData;

/**
 * 字典表 数据层
 * 
 * @author xjhqre
 */
@Mapper
public interface DictDataMapper extends BaseMapper<DictData> {

    int countDictDataByType(String dictType);

    void updateDictDataType(@Param("oldDictType") String oldDictType, String newDictType);
}
