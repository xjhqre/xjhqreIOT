package com.xjhqre.framework.interceptor.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.xjhqre.common.annotation.RepeatSubmit;
import com.xjhqre.common.constant.CacheConstants;
import com.xjhqre.common.filter.RepeatedlyRequestWrapper;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.common.utils.http.HttpHelper;
import com.xjhqre.common.utils.redis.RedisCache;
import com.xjhqre.framework.interceptor.RepeatSubmitInterceptor;

/**
 * 判断请求url和数据是否和上一次相同， 如果和上次相同，则是重复提交表单。 有效时间为10秒内。
 * 
 * @author xjhqre
 */
@Component
public class SameUrlDataInterceptor extends RepeatSubmitInterceptor {
    public final String REPEAT_PARAMS = "repeatParams";

    public final String REPEAT_TIME = "repeatTime";

    // 令牌自定义标识(Authorization)
    @Value("${token.header}")
    private String header;

    @Autowired
    private RedisCache redisCache;

    @SuppressWarnings("unchecked")
    @Override
    public boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation) {
        String nowParams = "";
        if (request instanceof RepeatedlyRequestWrapper) {
            RepeatedlyRequestWrapper repeatedlyRequest = (RepeatedlyRequestWrapper)request;
            nowParams = HttpHelper.getBodyString(repeatedlyRequest); // 获取请求参数
        }

        // body参数为空，获取Parameter的数据
        if (StringUtils.isEmpty(nowParams)) {
            nowParams = JSON.toJSONString(request.getParameterMap());
        }
        Map<String, Object> nowDataMap = new HashMap<>();
        // 设置参数和请求时间
        nowDataMap.put(this.REPEAT_PARAMS, nowParams);
        nowDataMap.put(this.REPEAT_TIME, System.currentTimeMillis());

        // 请求地址（作为存放cache的key值）
        String url = request.getRequestURI();

        // 唯一值（没有消息头则使用请求地址）
        String submitKey = StringUtils.trimToEmpty(request.getHeader(this.header));

        // 唯一标识（指定key + url + 消息头）
        String cacheRepeatKey = CacheConstants.REPEAT_SUBMIT_KEY + url + submitKey;

        Object sessionObj = this.redisCache.getCacheObject(cacheRepeatKey);
        if (sessionObj != null) {
            Map<String, Object> sessionMap = (Map<String, Object>)sessionObj;
            if (sessionMap.containsKey(url)) {
                // url 为键，map为值，map包含请求参数和请求时间
                Map<String, Object> preDataMap = (Map<String, Object>)sessionMap.get(url);
                // 如果两次请求的参数相同 && 两次请求的时间差小于5秒 --> 认为是重复请求
                if (this.compareParams(nowDataMap, preDataMap)
                    && this.compareTime(nowDataMap, preDataMap, annotation.interval())) {
                    return true;
                }
            }
        }
        Map<String, Object> cacheMap = new HashMap<>();
        cacheMap.put(url, nowDataMap);
        this.redisCache.setCacheObject(cacheRepeatKey, cacheMap, annotation.interval(), TimeUnit.MILLISECONDS);
        return false;
    }

    /**
     * 判断参数是否相同
     */
    private boolean compareParams(Map<String, Object> nowMap, Map<String, Object> preMap) {
        String nowParams = (String)nowMap.get(this.REPEAT_PARAMS);
        String preParams = (String)preMap.get(this.REPEAT_PARAMS);
        return nowParams.equals(preParams);
    }

    /**
     * 判断两次间隔时间
     */
    private boolean compareTime(Map<String, Object> nowMap, Map<String, Object> preMap, int interval) {
        long time1 = (Long)nowMap.get(this.REPEAT_TIME);
        long time2 = (Long)preMap.get(this.REPEAT_TIME);
        if ((time1 - time2) < interval) {
            return true;
        }
        return false;
    }
}
