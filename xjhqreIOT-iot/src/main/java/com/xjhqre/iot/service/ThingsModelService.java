package com.xjhqre.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.iot.domain.entity.ThingsModel;

/**
 * 物模型Service接口
 * 
 * @author xjhqre
 * @date 2023-1-1
 */
public interface ThingsModelService extends IService<ThingsModel> {

    /**
     * 分页查询产品物模型列表
     */
    IPage<ThingsModel> find(ThingsModel thingsModel, Integer pageNum, Integer pageSize);

    /**
     * 查询产品物模型列表
     */
    List<ThingsModel> list(ThingsModel thingsModel);

    /**
     * 查询产品物模型详情
     */
    ThingsModel getDetail(Long modelId);

    /**
     * 添加产品物模型
     *
     */
    void add(ThingsModel thingsModel);

    /**
     * 修改产品物模型
     */
    void update(ThingsModel thingsModel);

    /**
     * 批量删除产品物模型
     */
    void delete(List<Long> modelIds);

    /**
     * 根据产品ID获取缓存的产品物模型(JSON格式)
     */
    String getThingsModelCache(Long productId);

}
