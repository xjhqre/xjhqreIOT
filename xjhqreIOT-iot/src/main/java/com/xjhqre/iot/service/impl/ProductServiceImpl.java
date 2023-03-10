package com.xjhqre.iot.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.constant.CacheConstants;
import com.xjhqre.common.domain.model.LoginUser;
import com.xjhqre.common.exception.ServiceException;
import com.xjhqre.common.utils.AssertUtils;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.common.utils.redis.RedisCache;
import com.xjhqre.common.utils.uuid.RandomUtils;
import com.xjhqre.iot.domain.entity.Product;
import com.xjhqre.iot.domain.entity.ThingsModel;
import com.xjhqre.iot.mapper.ProductMapper;
import com.xjhqre.iot.service.AlertService;
import com.xjhqre.iot.service.ProductService;
import com.xjhqre.iot.service.ThingsModelService;

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
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Resource
    private ProductMapper productMapper;
    @Resource
    private RedisCache redisCache;
    @Resource
    ThingsModelService thingsModelService;
    @Resource
    AlertService alertService;

    /**
     * 项目启动时，加载产品物模型缓存
     */
    @PostConstruct
    public void init() {
        log.info("开始加载物模型缓存");
        // 数据库查询物模型集合
        List<Product> products = this.productMapper.selectList(null);
        for (Product product : products) {
            LambdaQueryWrapper<ThingsModel> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ThingsModel::getProductId, product.getProductId());
            List<ThingsModel> thingsModels = this.thingsModelService.list(wrapper);
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
            this.redisCache.setCacheObject(CacheConstants.THING_MODEL_KEY + product.getProductId(), thingsModelsJson);
        }

        log.info("物模型缓存加载完成");
    }

    @Override
    public IPage<Product> find(Product product, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(product.getProductId() != null, Product::getProductId, product.getProductId())
            .like(product.getProductName() != null && !"".equals(product.getProductName()), Product::getProductName,
                product.getProductName())
            .eq(product.getStatus() != null, Product::getStatus, product.getStatus());

        LoginUser user = SecurityUtils.getLoginUser();
        if (!SecurityUtils.isAdmin(user.getUserId())) {
            wrapper.eq(Product::getUserId, SecurityUtils.getUserId());
        }

        return this.productMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<Product> list(Product product) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(product.getProductId() != null, Product::getProductId, product.getProductId())
            .like(product.getProductName() != null && !"".equals(product.getProductName()), Product::getProductName,
                product.getProductName())
            .eq(product.getStatus() != null, Product::getStatus, product.getStatus());

        LoginUser user = SecurityUtils.getLoginUser();
        if (!SecurityUtils.isAdmin(user.getUserId())) {
            wrapper.eq(Product::getUserId, SecurityUtils.getUserId());
        }

        return this.productMapper.selectList(wrapper);
    }

    /**
     * 获取产品详情
     */
    @Override
    public Product getDetail(Long productId) {
        return this.productMapper.selectById(productId);
    }

    /**
     * 添加产品
     */
    @Override
    public void add(Product product) {
        // 判断是否为管理员
        product.setProductKey("K" + RandomUtils.randomString(15));
        product.setProductSecret("S" + RandomUtils.randomString(15));
        product.setStatus(product.getStatus() == null ? 1 : product.getStatus());
        product.setUserId(SecurityUtils.getUserId());
        product.setCreateTime(DateUtils.getNowDate());
        product.setCreateBy(SecurityUtils.getUsername());
        this.productMapper.insert(product);
    }

    /**
     * 修改产品
     */
    @Override
    public void update(Product product) {
        product.setUpdateBy(SecurityUtils.getUsername());
        product.setUpdateTime(DateUtils.getNowDate());
        this.productMapper.updateById(product);
    }

    @Override
    public void changeProductStatus(Long productId, Integer status) {
        if (status == 1) {
            // 产品下不能有设备
            Long[] productIds = new Long[1];
            productIds[0] = productId;
            int deviceCount = this.productMapper.deviceCountInProducts(productIds);
            AssertUtils.isTrue(deviceCount == 0, "取消发布失败，请先删除产品下的设备");
        } else if (status == 2) {
            // 产品下必须包含物模型
            int thingsCount = this.productMapper.thingsCountInProduct(productId);
            AssertUtils.isTrue(thingsCount != 0, "发布失败，请先添加产品的物模型");
        } else {
            throw new ServiceException("状态更新失败,状态值有误");
        }
        this.productMapper.changeProductStatus(productId, status);
    }

    @Override
    public void delete(Long[] productIds) {
        // 删除物模型JSON缓存
        for (Long productId : productIds) {
            this.redisCache.deleteObject(CacheConstants.THING_MODEL_KEY + productId);
        }
        // 产品下不能有固件
        // int firmwareCount = this.productMapper.firmwareCountInProducts(productIds);
        // AssertUtils.isTrue(!(firmwareCount > 0), "删除失败，请先删除对应产品下的固件");
        // 产品下不能有设备
        int deviceCount = this.productMapper.deviceCountInProducts(productIds);
        AssertUtils.isTrue(!(deviceCount > 0), "删除失败，请先删除对应产品下的设备");
        // 删除产品告警规则
        this.alertService.deleteByProductIds(productIds);
        // 删除产品物模型
        this.productMapper.deleteProductThingsModelByProductIds(productIds);
        // 删除产品
        this.productMapper.deleteBatchIds(Arrays.asList(productIds));
    }
}
