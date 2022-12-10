package com.xjhqre.system.service.impl;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.constant.CacheConstants;
import com.xjhqre.common.constant.ConfigConstant;
import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.constant.UserConstants;
import com.xjhqre.common.exception.ServiceException;
import com.xjhqre.common.text.Convert;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.common.utils.redis.RedisCache;
import com.xjhqre.system.domain.entity.Config;
import com.xjhqre.system.mapper.ConfigMapper;
import com.xjhqre.system.service.ConfigService;

import lombok.extern.slf4j.Slf4j;

/**
 * 参数配置 服务层实现
 * 
 * @author xjhqre
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {
    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private RedisCache redisCache;

    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init() {
        log.info("开始初始化配置");
        this.loadingConfigCache();
        log.info("初始化配置完成");
    }

    /**
     * 分页查询配置
     * 
     * @param config
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public IPage<Config> findConfig(Config config, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Config> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(config.getConfigId() != null, Config::getConfigId, config.getConfigId())
            .like(config.getConfigName() != null, Config::getConfigName, config.getConfigName())
            .like(config.getConfigKey() != null, Config::getConfigKey, config.getConfigKey());
        return this.configMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
    }

    /**
     * 根据键名查询参数配置信息
     * 
     * @param configKey
     *            参数key
     * @return 参数键值 config_value
     */
    @Override
    public String selectConfigByKey(String configKey) {
        String configValue = Convert.toStr(this.redisCache.getCacheObject(this.getCacheKey(configKey)));
        if (StringUtils.isNotEmpty(configValue)) {
            return configValue;
        }
        LambdaQueryWrapper<Config> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Config::getConfigKey, configKey);
        Config retConfig = this.configMapper.selectOne(queryWrapper);
        if (StringUtils.isNotNull(retConfig)) {
            this.redisCache.setCacheObject(this.getCacheKey(configKey), retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取验证码开关
     * 
     * @return true开启，false关闭
     */
    @Override
    public boolean selectCaptchaEnabled() {
        String captchaEnabled = this.selectConfigByKey(ConfigConstant.CAPTCHA_ENABLED);
        if (StringUtils.isEmpty(captchaEnabled)) {
            return true;
        }
        return Convert.toBool(captchaEnabled);
    }

    /**
     * 获取文章审核开关
     *
     * @return true开启，false关闭
     */
    @Override
    public boolean selectArticleAuditEnabled() {
        String articleAudit = this.selectConfigByKey(ConfigConstant.ARTICLE_AUDIT);
        if (StringUtils.isEmpty(articleAudit)) {
            return true;
        }
        return Convert.toBool(articleAudit);
    }

    /**
     * 获取图片审核开关
     *
     * @return true开启，false关闭
     */
    @Override
    public boolean selectPictureAuditEnabled() {
        String pictureAudit = this.selectConfigByKey(ConfigConstant.PICTURE_AUDIT);
        if (StringUtils.isEmpty(pictureAudit)) {
            return true;
        }
        return Convert.toBool(pictureAudit);
    }

    /**
     * 获取全文检索开关
     *
     * @return true开启，false关闭
     */
    @Override
    public boolean selectEsSearch() {
        String esSearch = this.selectConfigByKey(ConfigConstant.ES_SEARCH);
        if (StringUtils.isEmpty(esSearch)) {
            return true;
        }
        return Convert.toBool(esSearch);
    }

    /**
     * 查询参数配置列表
     * 
     * @param config
     *            参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<Config> selectConfigList(Config config) {
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(config.getConfigName() != null, Config::getConfigName, config.getConfigName())
            .eq(config.getConfigType() != null, Config::getConfigType, config.getConfigType())
            .like(config.getConfigKey() != null, Config::getConfigKey, config.getConfigKey());
        return this.configMapper.selectList(wrapper);
    }

    /**
     * 新增参数配置
     * 
     * @param config
     *            参数配置信息
     * @return 结果
     */
    @Override
    public void insertConfig(Config config) {
        this.configMapper.insert(config);
        this.redisCache.setCacheObject(this.getCacheKey(config.getConfigKey()), config.getConfigValue());
    }

    /**
     * 修改参数配置
     * 
     * @param config
     *            参数配置信息
     * @return 结果
     */
    @Override
    public void updateConfig(Config config) {
        this.configMapper.updateById(config);
        this.redisCache.setCacheObject(this.getCacheKey(config.getConfigKey()), config.getConfigValue());
    }

    /**
     * 批量删除参数信息
     * 
     * @param configIds
     *            需要删除的参数ID
     */
    @Override
    public void deleteConfigByIds(Long[] configIds) {
        for (Long configId : configIds) {
            Config config = this.configMapper.selectById(configId);
            if (StringUtils.equals(UserConstants.YES, config.getConfigType())) {
                throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            this.configMapper.deleteById(configId);
            this.redisCache.deleteObject(this.getCacheKey(config.getConfigKey()));
        }
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache() {
        List<Config> configsList = this.configMapper.selectList(null);
        for (Config config : configsList) {
            this.redisCache.setCacheObject(this.getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache() {
        Collection<String> keys = this.redisCache.keys(CacheConstants.SYS_CONFIG_KEY + "*");
        this.redisCache.deleteObject(keys);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache() {
        this.clearConfigCache();
        this.loadingConfigCache();
    }

    /**
     * 校验参数键名是否唯一
     * 
     * @param configKey
     *            参数配置信息
     * @return 结果
     */
    @Override
    public Boolean checkConfigKeyUnique(String configKey) {
        LambdaQueryWrapper<Config> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Config::getConfigKey, configKey);
        Config info = this.configMapper.selectOne(queryWrapper);
        if (StringUtils.isNotNull(info)) {
            return Constants.NOT_UNIQUE;
        }
        return Constants.UNIQUE;
    }

    /**
     * 设置cache key
     * 
     * @param configKey
     *            参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey) {
        return CacheConstants.SYS_CONFIG_KEY + configKey;
    }
}
