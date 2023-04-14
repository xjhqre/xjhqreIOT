package com.xjhqre.iot.service.impl;

import static com.xjhqre.common.utils.SecurityUtils.getUsername;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.xjhqre.iot.domain.entity.Product;
import com.xjhqre.iot.domain.entity.ThingsModel;
import com.xjhqre.iot.mapper.ProductMapper;
import com.xjhqre.iot.mapper.ThingsModelMapper;
import com.xjhqre.iot.service.ThingsModelService;

/**
 * 物模型Service业务层处理
 * 
 * @author xjhqre
 * @date 2021-12-16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ThingsModelServiceImpl extends ServiceImpl<ThingsModelMapper, ThingsModel> implements ThingsModelService {

    @Resource
    private ThingsModelMapper thingsModelMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private RedisCache redisCache;

    /**
     * 分页查询产品物模型列表
     */
    @Override
    public IPage<ThingsModel> find(ThingsModel thingsModel, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ThingsModel> findWrapper = new LambdaQueryWrapper<>();
        findWrapper.eq(thingsModel.getModelId() != null, ThingsModel::getModelId, thingsModel.getModelId())
            .like(thingsModel.getModelName() != null && !"".equals(thingsModel.getModelName()),
                ThingsModel::getModelName, thingsModel.getModelName())
            .eq(thingsModel.getType() != null, ThingsModel::getType, thingsModel.getType())
            .eq(thingsModel.getProductId() != null, ThingsModel::getProductId, thingsModel.getProductId())
            .like(thingsModel.getProductName() != null && !"".equals(thingsModel.getProductName()),
                ThingsModel::getProductName, thingsModel.getProductName());
        return this.thingsModelMapper.selectPage(new Page<>(pageNum, pageSize), findWrapper);
    }

    /**
     * 查询产品物模型列表
     */
    @Override
    public List<ThingsModel> list(ThingsModel thingsModel) {
        LambdaQueryWrapper<ThingsModel> listWrapper = new LambdaQueryWrapper<>();
        listWrapper.eq(thingsModel.getModelId() != null, ThingsModel::getModelId, thingsModel.getModelId())
            .like(thingsModel.getModelName() != null && !"".equals(thingsModel.getModelName()),
                ThingsModel::getModelName, thingsModel.getModelName())
            .eq(thingsModel.getType() != null, ThingsModel::getType, thingsModel.getType())
            .eq(thingsModel.getProductId() != null, ThingsModel::getProductId, thingsModel.getProductId())
            .like(thingsModel.getProductName() != null && !"".equals(thingsModel.getProductName()),
                ThingsModel::getProductName, thingsModel.getProductName());
        return this.thingsModelMapper.selectList(listWrapper);
    }

    /**
     * 查询产品物模型详情
     */
    @Override
    public ThingsModel getDetail(Long modelId) {
        return this.thingsModelMapper.selectById(modelId);
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
        // 标识符重复判断
        LambdaQueryWrapper<ThingsModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ThingsModel::getIdentifier, thingsModel.getIdentifier()).eq(ThingsModel::getProductId,
            thingsModel.getProductId());
        AssertUtils.isNull(this.thingsModelMapper.selectOne(wrapper), "标识符重复");

        thingsModel.setCreateTime(DateUtils.getNowDate());
        thingsModel.setCreateBy(getUsername());
        if (thingsModel.getType() == 1) {
            thingsModel.setInputParam(null);
            thingsModel.setOutputParam(null);

        } else if (thingsModel.getType() == 2) {
            thingsModel.setDataType(null);
        } else if (thingsModel.getType() == 3) {
            thingsModel.setDataType(null);
            thingsModel.setInputParam(null);
        }
        this.thingsModelMapper.insert(thingsModel);
        // 更新redis缓存
        // this.setThingsModelCacheByProductId(thingsModel.getProductId());
    }

    /**
     * 修改产品物模型
     */
    @Override
    public void update(ThingsModel thingsModel) {
        // 标识符重复判断
        LambdaQueryWrapper<ThingsModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ThingsModel::getIdentifier, thingsModel.getIdentifier())
            .eq(ThingsModel::getProductId, thingsModel.getProductId())
            .ne(ThingsModel::getModelId, thingsModel.getModelId());
        AssertUtils.isNull(this.thingsModelMapper.selectOne(wrapper), "标识符重复");

        thingsModel.setUpdateTime(DateUtils.getNowDate());
        thingsModel.setUpdateBy(SecurityUtils.getUsername());
        this.thingsModelMapper.updateById(thingsModel);
        // 更新redis缓存
        // this.setThingsModelCacheByProductId(thingsModel.getProductId());
    }

    /**
     * 批量删除产品物模型
     * 
     */
    @Override
    public void delete(List<Long> modelIds) {
        ThingsModel thingsModel = this.thingsModelMapper.selectById(modelIds.get(0));
        this.thingsModelMapper.deleteBatchIds(modelIds);
        // 更新redis缓存
        // this.setThingsModelCacheByProductId(thingsModel.getProductId());
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

    @Override
    public List<ThingsModel> listThingModelByProductId(Long productId, Integer type) {
        return this.thingsModelMapper.listThingModelByProductId(productId, type);
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
