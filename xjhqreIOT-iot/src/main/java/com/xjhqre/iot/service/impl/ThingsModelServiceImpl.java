package com.xjhqre.iot.service.impl;

import static com.xjhqre.common.utils.SecurityUtils.getUsername;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.constant.CacheConstants;
import com.xjhqre.common.utils.AssertUtils;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.common.utils.redis.RedisCache;
import com.xjhqre.iot.domain.entity.ModelParam;
import com.xjhqre.iot.domain.entity.Product;
import com.xjhqre.iot.domain.entity.ThingsModel;
import com.xjhqre.iot.mapper.ProductMapper;
import com.xjhqre.iot.mapper.ThingsModelMapper;
import com.xjhqre.iot.service.ModelParamService;
import com.xjhqre.iot.service.ThingsModelService;

/**
 * 物模型Service业务层处理
 * 
 * @author xjhqre
 * @date 2021-12-16
 */
@Service
public class ThingsModelServiceImpl extends ServiceImpl<ThingsModelMapper, ThingsModel> implements ThingsModelService {

    @Resource
    private ThingsModelMapper thingsModelMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private RedisCache redisCache;
    @Resource
    private ModelParamService modelParamService;

    /**
     * 分页查询产品物模型列表
     */
    @Override
    public IPage<ThingsModel> find(ThingsModel thingsModel, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ThingsModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(thingsModel.getModelId() != null, ThingsModel::getModelId, thingsModel.getModelId())
            .like(thingsModel.getModelName() != null && !"".equals(thingsModel.getModelName()),
                ThingsModel::getModelName, thingsModel.getModelName())
            .eq(thingsModel.getType() != null, ThingsModel::getType, thingsModel.getType())
            .eq(thingsModel.getProductId() != null, ThingsModel::getProductId, thingsModel.getProductId())
            .like(thingsModel.getProductName() != null && !"".equals(thingsModel.getProductName()),
                ThingsModel::getProductName, thingsModel.getProductName());
        // 装入输入输出参数
        return this.thingsModelMapper.selectPage(new Page<>(pageNum, pageSize), wrapper).convert(model -> {
            if (model.getType() != 1) { // 添加输入输出参数
                List<ModelParam> modelParams = this.modelParamService.listByModelId(model.getModelId());
                model.setParamList(modelParams);
            }
            return model;
        });
    }

    /**
     * 查询产品物模型列表
     */
    @Override
    public List<ThingsModel> list(ThingsModel thingsModel) {
        LambdaQueryWrapper<ThingsModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(thingsModel.getModelId() != null, ThingsModel::getModelId, thingsModel.getModelId())
            .like(thingsModel.getModelName() != null && !"".equals(thingsModel.getModelName()),
                ThingsModel::getModelName, thingsModel.getModelName())
            .eq(thingsModel.getType() != null, ThingsModel::getType, thingsModel.getType())
            .eq(thingsModel.getProductId() != null, ThingsModel::getProductId, thingsModel.getProductId())
            .like(thingsModel.getProductName() != null && !"".equals(thingsModel.getProductName()),
                ThingsModel::getProductName, thingsModel.getProductName());
        return this.thingsModelMapper.selectList(wrapper);
    }

    /**
     * 查询产品物模型详情
     */
    @Override
    public ThingsModel getDetail(Long modelId) {
        ThingsModel thingsModel = this.thingsModelMapper.selectById(modelId);
        LambdaQueryWrapper<ModelParam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelParam::getModelId, modelId);
        List<ModelParam> modelParamList = this.modelParamService.list(wrapper);
        thingsModel.setParamList(modelParamList);
        return thingsModel;
    }

    /**
     * 新增物模型
     * 
     * @param thingsModel
     *            物模型
     * @return 结果
     */
    @Override
    public void add(ThingsModel thingsModel) {
        thingsModel.setCreateTime(DateUtils.getNowDate());
        thingsModel.setCreateBy(getUsername());
        this.thingsModelMapper.insert(thingsModel);
        // 添加输出输出参数
        if (!CollectionUtils.isEmpty(thingsModel.getParamList())) {
            List<ModelParam> modelParamList = thingsModel.getParamList().stream().peek(modelParam -> {
                modelParam.setModelId(thingsModel.getModelId());
                modelParam.setModelName(thingsModel.getModelName());
                modelParam.setProductId(thingsModel.getProductId());
                modelParam.setProductName(thingsModel.getProductName());
                modelParam.setIdentifier(thingsModel.getIdentifier());
                modelParam.setCreateBy(SecurityUtils.getUsername());
                modelParam.setCreateTime(DateUtils.getNowDate());
            }).collect(Collectors.toList());
            this.modelParamService.saveBatch(modelParamList);
        }
        // 更新redis缓存
        this.setThingsModelCacheByProductId(thingsModel.getProductId());
    }

    /**
     * 修改产品物模型
     */
    @Override
    public void update(ThingsModel thingsModel) {
        thingsModel.setUpdateTime(DateUtils.getNowDate());
        thingsModel.setUpdateBy(SecurityUtils.getUsername());
        this.thingsModelMapper.updateById(thingsModel);
        // 更新redis缓存
        this.setThingsModelCacheByProductId(thingsModel.getProductId());
    }

    /**
     * 批量删除产品物模型
     * 
     */
    @Override
    public void delete(List<Long> modelIds) {
        ThingsModel thingsModel = this.thingsModelMapper.selectById(modelIds.get(0));
        for (Long modelId : modelIds) {
            ThingsModel thingsModel1 = this.thingsModelMapper.selectById(modelId);
            // 删除服务和事件的入参和出参
            if (thingsModel1.getType() != 1) {
                this.modelParamService.deleteByModelId(modelId);
            }
        }
        this.thingsModelMapper.deleteBatchIds(modelIds);
        // 更新redis缓存
        this.setThingsModelCacheByProductId(thingsModel.getProductId());
    }

    /**
     * 根据产品ID获取缓存的产品物模型(JSON格式)
     * 
     * @param productId
     *            产品id
     * @return 产品物模型(JSON格式)
     */
    @Override
    public String getThingsModelCache(Long productId) {
        // redis获取物模型
        String thingsModelJson = this.redisCache.getCacheObject(CacheConstants.THING_MODEL_KEY + productId);
        AssertUtils.notEmpty(thingsModelJson, "获取产品物模型缓存失败，缓存为空");
        return thingsModelJson;
    }

    /**
     * 根据产品id获取物模型列表
     * 
     * @param productId
     * @return
     */
    @Override
    public List<ThingsModel> listThingModelByProductId(Long productId) {
        LambdaQueryWrapper<ThingsModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ThingsModel::getProductId, productId);
        return this.thingsModelMapper.selectList(wrapper);
    }

    /**
     * 根据产品ID更新JSON物模型
     */
    private void setThingsModelCacheByProductId(Long productId) {
        // 数据库查询物模型集合
        LambdaQueryWrapper<ThingsModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ThingsModel::getProductId, productId);
        List<ThingsModel> thingsModels = this.thingsModelMapper.selectList(wrapper);
        /* thingsModelsJson: 
        [
        {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""},
        {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""},
        {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""},
        {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""},
        {"modelId": "", "modelName": "", ..., "type": "", "dataType": "", "specs", ""}
        ]
         */

        String thingsModelsJson = JSON.toJSONString(thingsModels);
        this.redisCache.setCacheObject(CacheConstants.THING_MODEL_KEY + productId, thingsModelsJson);
        // 更新数据库
        Product product = new Product();
        product.setProductId(productId);
        product.setThingsModelsJson(thingsModelsJson);
        this.productMapper.updateById(product);
    }
}
