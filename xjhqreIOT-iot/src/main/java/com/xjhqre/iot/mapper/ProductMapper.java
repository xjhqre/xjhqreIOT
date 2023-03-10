package com.xjhqre.iot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.iot.domain.entity.Product;

/**
 * 产品Mapper接口
 * 
 * @author xjhqre
 * @date 2021-12-16
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 更新产品状态，1-未发布，2-已发布
     *
     * @return 结果
     */
    int changeProductStatus(@Param("productId") Long productId, @Param("status") Integer status);

    /**
     * 批量删除产品物模型
     *
     * @param productIds
     *            需要删除的数据主键集合
     * @return 结果
     */
    int deleteProductThingsModelByProductIds(@Param("productIds") Long[] productIds);

    /**
     * 产品下的固件数量
     * 
     * @param productIds
     *            需要删除的数据主键集合
     * @return 结果
     */
    int firmwareCountInProducts(@Param("productIds") Long[] productIds);

    /**
     * 产品下的设备数量
     * 
     * @param productIds
     *            需要删除的数据主键集合
     * @return 结果
     */
    int deviceCountInProducts(@Param("productIds") Long[] productIds);

    /**
     * 产品下的物模型数量
     * 
     * @param productId
     *            需要删除的数据主键集合
     * @return 结果
     */
    int thingsCountInProduct(@Param("productId") Long productId);
}
