package com.xjhqre.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.iot.domain.entity.Alert;

/**
 * 设备告警Service接口
 * 
 * @author kerwincui
 * @date 2022-01-13
 */
public interface AlertService {

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
}
