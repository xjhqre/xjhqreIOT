package com.xjhqre.system.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.common.domain.entity.DictType;

/**
 * 字典表 数据层
 * 
 * @author xjhqre
 */
@Mapper
public interface DictTypeMapper extends BaseMapper<DictType> {

    DictType checkDictTypeUnique(String dictType);
}
