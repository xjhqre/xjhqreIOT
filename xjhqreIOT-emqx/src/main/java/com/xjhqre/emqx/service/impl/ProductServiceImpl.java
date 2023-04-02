package com.xjhqre.emqx.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.emqx.domain.entity.Product;
import com.xjhqre.emqx.mapper.ProductMapper;
import com.xjhqre.emqx.service.ProductService;

import lombok.extern.slf4j.Slf4j;

/**
 * 产品Service业务层处理
 * 
 * @author xjhqre
 * @date 2021-12-16
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {}
