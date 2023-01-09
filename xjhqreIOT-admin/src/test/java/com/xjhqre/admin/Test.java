package com.xjhqre.admin;

import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.xjhqre.common.utils.BaiduMapUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Test
 * </p>
 *
 * @author xjhqre
 * @since 1月 04, 2023
 */
@Slf4j
public class Test {

    @org.junit.Test
    public void test2() {
        Map<String, Double> map = BaiduMapUtils.getLngAndLat("北京市海淀区上地十街10号");
        log.info("map --> {}", JSON.toJSONString(map));
    }

    @org.junit.Test
    public void test1() {
        log.info("city --> {}");
    }

}
