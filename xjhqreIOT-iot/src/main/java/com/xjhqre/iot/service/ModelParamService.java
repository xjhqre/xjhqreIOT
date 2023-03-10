package com.xjhqre.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.iot.domain.entity.ModelParam;

/**
 * <p>
 * ModelParamService
 * </p>
 *
 * @author xjhqre
 * @since 2月 03, 2023
 */
public interface ModelParamService extends IService<ModelParam> {

    /**
     * 查询物模型参数
     */
    List<ModelParam> list(ModelParam modelParam);

    /**
     * 获取参数详情
     * 
     * @param paramId
     * @return
     */
    ModelParam getDetail(Long paramId);

    /**
     * 添加物模型参数
     * 
     * @param modelParam
     */
    void add(ModelParam modelParam);

    /**
     * 修改物模型参数
     * 
     * @param modelParam
     */
    void update(ModelParam modelParam);

    /**
     * 删除物模型参数
     * 
     * @param modelId
     */
    void deleteByModelId(Long modelId);

    /**
     * 根据modelId查询输入输出参数
     * 
     * @param modelId
     * @return
     */
    List<ModelParam> listByModelId(Long modelId);
}
