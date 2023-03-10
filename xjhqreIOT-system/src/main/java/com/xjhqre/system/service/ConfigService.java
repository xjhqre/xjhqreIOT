package com.xjhqre.system.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.system.domain.entity.Config;

/**
 * 参数配置 服务层
 * 
 * @author xjhqre
 */
public interface ConfigService extends IService<Config> {

    /**
     * 分页查询配置信息
     * 
     * @param config
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<Config> find(Config config, Integer pageNum, Integer pageSize);

    /**
     * 根据键名查询参数配置信息
     *
     */
    String getByConfigKey(String configKey);

    /**
     * 获取验证码开关
     * 
     * @return true开启，false关闭
     */
    boolean selectCaptchaEnabled();

    /**
     * 获取图片审核开关
     *
     * @return true开启，false关闭
     */
    boolean selectPictureAuditEnabled();

    /**
     * 获取全文检索开关
     *
     * @return true开启，false关闭
     */
    boolean selectEsSearch();

    /**
     * 查询参数配置列表
     * 
     * @param config
     *            参数配置信息
     * @return 参数配置集合
     */
    List<Config> selectConfigList(Config config);

    /**
     * 新增参数配置
     * 
     * @param config
     *            参数配置信息
     * @return 结果
     */
    void add(Config config);

    /**
     * 修改参数配置
     * 
     * @param config
     *            参数配置信息
     * @return 结果
     */
    void update(Config config);

    /**
     * 批量删除参数信息
     * 
     * @param configIds
     *            需要删除的参数ID
     */
    void delete(List<Long> configIds);

    /**
     * 加载参数缓存数据
     */
    void loadingConfigCache();

    /**
     * 清空参数缓存数据
     */
    void clearConfigCache();

    /**
     * 重置参数缓存数据
     */
    void resetConfigCache();

    /**
     * 校验参数键名是否唯一
     *
     * @param config
     *            参数信息
     * @param configId
     * @return 结果
     */
    Boolean checkConfigKeyUnique(String configKey, Long configId);
}
