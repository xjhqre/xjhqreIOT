package com.xjhqre.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.iot.domain.entity.Alert;

/**
 * 设备告警Service接口
 * 
 * @author xjhqre
 * @date 2022-01-13
 */
public interface AlertService extends IService<Alert> {

    /**
     * 分页查询产品告警设置列表
     */
    IPage<Alert> find(Alert alert, Integer pageNum, Integer pageSize);

    /**
     * 获取产品告警设置详情
     *
     */
    Alert getDetail(Long alertId);

    /**
     * 根据 productId 查询告警
     * 
     * @param productId
     * @return
     */
    Alert getByProductId(Long productId);

    /**
     * 添加产品告警设置
     */
    void add(Alert alert);

    /**
     * 修改产品告警设置
     *
     */
    void update(Alert alert);

    /**
     * 删除产品告警设置
     *
     */
    void delete(List<Long> alertIds);

    /**
     * 根据产品id删除告警规则
     * 
     * @param productIds
     */
    void deleteByProductIds(Long[] productIds);
}
