package com.xjhqre.common.translator;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractNameTranslator<K, V> {
    private Map<Object, V> localCache;
    private final boolean cached;

    protected AbstractNameTranslator() {
        this(true);
    }

    protected AbstractNameTranslator(boolean cached) {
        this.cached = cached;
        if (cached) {
            this.localCache = new HashMap<>();
        }

    }

    public final V get(K key) {
        // 先从缓存中获取，若缓存中没有再执行翻译
        return this.cached ? this.localCache.computeIfAbsent(this.getCacheKey(key), k -> this.doTranslate(key))
                : this.doTranslate(key);
    }

    /**
     * 供其他地方调用
     *
     * @param key 键
     * @return 值
     */
    public final String translate(K key) {
        V name = this.get(key);
        if (name == null) {
            return "";
        } else {
            // 若值不是 String 类型，则将值转换成 String
            return name instanceof String ? (String) name : this.value2String(key, name);
        }
    }

    /**
     * 翻译，一般调用接口进行数据库查询
     *
     * @param key
     * @return
     */
    protected abstract V doTranslate(K key);

    protected Object getCacheKey(K key) {
        return key;
    }

    protected String value2String(K key, V value) {
        throw new UnsupportedOperationException();
    }
}
