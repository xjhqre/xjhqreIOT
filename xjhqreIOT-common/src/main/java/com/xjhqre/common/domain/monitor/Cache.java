package com.xjhqre.common.domain.monitor;

import com.xjhqre.common.utils.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 缓存信息
 * 
 * @author xjhqre
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cache {
    /** 缓存名称 */
    private String cacheName = "";

    /** 缓存键名 */
    private String cacheKey = "";

    /** 缓存内容 */
    private String cacheValue = "";

    /** 备注 */
    private String remark = "";

    public Cache(String cacheName, String remark) {
        this.cacheName = cacheName;
        this.remark = remark;
    }

    public Cache(String cacheName, String cacheKey, String cacheValue) {
        this.cacheName = StringUtils.replace(cacheName, ":", "");
        this.cacheKey = StringUtils.replace(cacheKey, cacheName, "");
        this.cacheValue = cacheValue;
    }
}
