package com.xjhqre.admin.controller.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.constant.CacheConstants;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.domain.monitor.Cache;
import com.xjhqre.common.utils.StringUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 缓存监控
 * 
 * @author xjhqre
 */
@RestController
@Api(value = "缓存监控", tags = "缓存监控")
@RequestMapping("/monitor/cache")
public class CacheController extends BaseController {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final static List<Cache> caches = new ArrayList<>();
    static {
        caches.add(new Cache(CacheConstants.LOGIN_TOKEN_KEY, "用户信息"));
        caches.add(new Cache(CacheConstants.SYS_CONFIG_KEY, "配置信息"));
        caches.add(new Cache(CacheConstants.SYS_DICT_KEY, "数据字典"));
        caches.add(new Cache(CacheConstants.CAPTCHA_CODE_KEY, "验证码"));
        caches.add(new Cache(CacheConstants.REPEAT_SUBMIT_KEY, "防重提交"));
        caches.add(new Cache(CacheConstants.RATE_LIMIT_KEY, "限流处理"));
        caches.add(new Cache(CacheConstants.PWD_ERR_CNT_KEY, "密码错误次数"));
    }

    /**
     * 获取 redis 相关性能数据
     * 
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "获取 redis 相关性能数据")
    @PreAuthorize("@ss.hasPermission('monitor:cache:list')")
    @GetMapping()
    public R<Map<String, Object>> getInfo() {
        Properties info = (Properties)this.redisTemplate.execute((RedisCallback<Object>)RedisServerCommands::info);
        Properties commandStats = (Properties)this.redisTemplate
            .execute((RedisCallback<Object>)connection -> connection.info("commandstats"));
        // 获取当前选定数据库中可用键的总数
        Object dbSize = this.redisTemplate.execute((RedisCallback<Object>)RedisServerCommands::dbSize);

        Map<String, Object> result = new HashMap<>(3);
        result.put("info", info);
        result.put("dbSize", dbSize);

        List<Map<String, String>> pieList = new ArrayList<>();
        // 获取变量名遍历
        assert commandStats != null;
        commandStats.stringPropertyNames().forEach(key -> {
            Map<String, String> data = new HashMap<>(2);
            // 获取值
            String property = commandStats.getProperty(key);
            data.put("name", StringUtils.removeStart(key, "cmdstat_"));
            data.put("value", StringUtils.substringBetween(property, "calls=", ",usec"));
            pieList.add(data);
        });
        result.put("commandStats", pieList);
        return R.success(result);
    }

    /**
     * 获取缓存信息
     * 
     * @return
     */
    @ApiOperation(value = "获取缓存信息")
    @PreAuthorize("@ss.hasPermission('monitor:cache:list')")
    @GetMapping("/getNames")
    public R<List<Cache>> cache() {
        return R.success(caches);
    }

    /**
     * 获取所有缓存的键
     * 
     * @param cacheName
     * @return
     */
    @ApiOperation(value = "获取所有缓存的键")
    @PreAuthorize("@ss.hasPermission('monitor:cache:list')")
    @GetMapping("/getKeys/{cacheName}")
    public R<Set<String>> getCacheKeys(@PathVariable String cacheName) {
        Set<String> cacheKeys = this.redisTemplate.keys(cacheName + "*");
        return R.success(cacheKeys);
    }

    /**
     * 根据键获取对应的缓存值
     * 
     * @param cacheName
     * @param cacheKey
     * @return
     */
    @ApiOperation(value = "根据键获取对应的缓存值")
    @PreAuthorize("@ss.hasPermission('monitor:cache:list')")
    @GetMapping("/getValue/{cacheName}/{cacheKey}")
    public R<Cache> getCacheValue(@PathVariable String cacheName, @PathVariable String cacheKey) {
        String cacheValue = this.redisTemplate.opsForValue().get(cacheKey);
        Cache sysCache = new Cache(cacheName, cacheKey, cacheValue);
        return R.success(sysCache);
    }

    /**
     * 清空对应前缀的缓存
     * 
     * @param cacheName
     * @return
     */
    @ApiOperation(value = "清空对应前缀的缓存")
    @PreAuthorize("@ss.hasPermission('monitor:cache:list')")
    @DeleteMapping("/clearCacheName/{cacheName}")
    public R<String> clearCacheName(@PathVariable String cacheName) {
        // 模糊查询以 CacheName 开头的缓存
        Collection<String> cacheKeys = this.redisTemplate.keys(cacheName + "*");
        if (cacheKeys != null) {
            this.redisTemplate.delete(cacheKeys);
        }
        return R.success("清空缓存成功");
    }

    /**
     * 删除对应键的缓存
     * 
     * @param cacheKey
     * @return
     */
    @ApiOperation(value = "删除对应键的缓存")
    @PreAuthorize("@ss.hasPermission('monitor:cache:list')")
    @DeleteMapping("/clearCacheKey/{cacheKey}")
    public R<String> clearCacheKey(@PathVariable String cacheKey) {
        this.redisTemplate.delete(cacheKey);
        return R.success("删除缓存成功");
    }

    /**
     * 删除所有缓存
     * 
     * @return
     */
    @ApiOperation(value = "删除所有缓存")
    @PreAuthorize("@ss.hasPermission('monitor:cache:list')")
    @DeleteMapping("/clearCacheAll")
    public R<String> clearCacheAll() {
        Collection<String> cacheKeys = this.redisTemplate.keys("*");
        if (cacheKeys != null && !cacheKeys.isEmpty()) {
            this.redisTemplate.delete(cacheKeys);
        }
        return R.success("删除所有缓存成功");
    }
}
