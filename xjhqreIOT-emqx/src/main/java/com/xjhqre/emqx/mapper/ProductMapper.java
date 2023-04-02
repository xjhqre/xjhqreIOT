package com.xjhqre.emqx.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.emqx.domain.entity.Product;

/**
 * 产品Mapper接口
 * 
 * @author xjhqre
 * @date 2021-12-16
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {}
