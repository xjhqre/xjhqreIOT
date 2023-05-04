package com.xjhqre.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.iot.domain.entity.Product;

/**
 * ProductService
 * 
 * @author xjhqre
 * @date 2022-12-18
 */
public interface ProductService extends IService<Product> {

    /**
     * 分页查询产品
     * 
     * @param product
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<Product> find(Product product, Integer pageNum, Integer pageSize);

    List<Product> list(Product product);

    /**
     * 获取产品详情
     *
     */
    Product getDetail(Long productId);

    /**
     * 添加产品
     * 
     */
    void add(Product product);

    /**
     * 修改产品
     * 
     */
    void update(Product product);

    /**
     * 更新产品状态，1-未发布，2-已发布
     */
    void changeProductStatus(Long productId, Integer status);

    /**
     * 批量删除产品
     */
    void delete(Long[] productIds);

    /**
     * 根据id获取key
     * 
     * @param productId
     * @return
     */
    String getKeyById(Long productId);

    Product getByKey(String productKey);

    Product getByDeviceId(Long deviceId);
}
