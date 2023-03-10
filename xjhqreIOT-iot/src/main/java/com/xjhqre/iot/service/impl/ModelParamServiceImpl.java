package com.xjhqre.iot.service.impl;

import static com.xjhqre.common.utils.SecurityUtils.getUsername;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.iot.domain.entity.ModelParam;
import com.xjhqre.iot.mapper.ModelParamMapper;
import com.xjhqre.iot.service.ModelParamService;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * ModelParamServiceImpl
 * </p>
 *
 * @author xjhqre
 * @since 2月 03, 2023
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ModelParamServiceImpl extends ServiceImpl<ModelParamMapper, ModelParam> implements ModelParamService {

    @Resource
    ModelParamMapper modelParamMapper;

    @Override
    public List<ModelParam> list(ModelParam modelParam) {
        LambdaQueryWrapper<ModelParam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(modelParam.getModelId() != null, ModelParam::getModelId, modelParam.getModelId())
            .eq(modelParam.getParamId() != null, ModelParam::getParamId, modelParam.getParamId())
            .eq(modelParam.getType() != null, ModelParam::getType, modelParam.getType());
        return this.modelParamMapper.selectList(wrapper);
    }

    @Override
    public ModelParam getDetail(Long paramId) {
        return this.modelParamMapper.selectById(paramId);
    }

    @Override
    public void add(ModelParam modelParam) {
        modelParam.setCreateTime(DateUtils.getNowDate());
        modelParam.setCreateBy(getUsername());
        this.modelParamMapper.insert(modelParam);
    }

    @Override
    public void update(ModelParam modelParam) {
        modelParam.setUpdateTime(DateUtils.getNowDate());
        modelParam.setUpdateBy(SecurityUtils.getUsername());
        this.modelParamMapper.updateById(modelParam);
    }

    @Override
    public void deleteByModelId(Long modelId) {
        LambdaQueryWrapper<ModelParam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelParam::getModelId, modelId);
        this.modelParamMapper.delete(wrapper);
    }

    /**
     * 根据modelId查询输入输出参数
     * 
     * @param modelId
     * @return
     */
    @Override
    public List<ModelParam> listByModelId(Long modelId) {
        LambdaQueryWrapper<ModelParam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelParam::getModelId, modelId);
        return this.modelParamMapper.selectList(wrapper);
    }
}
