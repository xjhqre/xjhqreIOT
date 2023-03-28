package com.xjhqre.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xjhqre.common.utils.BaiduMapUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.iot.domain.model.Topic;

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
    public void test4() {
        String url = "http://1.15.88.204:18083/api/v4/subscriptions/";
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            log.info("sendGet - {}", url);
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            String authString = "admin:xjhqre";
            byte[] authEncBytes = Base64.getEncoder().encode(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            log.info("recv - {}", result);
        } catch (ConnectException e) {
            log.error("调用HttpUtils.sendGet ConnectException, url=" + url, e);
        } catch (SocketTimeoutException e) {
            log.error("调用HttpUtils.sendGet SocketTimeoutException, url=" + url, e);
        } catch (IOException e) {
            log.error("调用HttpUtils.sendGet IOException, url=" + url, e);
        } catch (Exception e) {
            log.error("调用HttpsUtil.sendGet Exception, url=" + url, e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                log.error("调用in.close Exception, url=" + url, ex);
            }
        }
        JSONObject jsonObject = JSONObject.parseObject(result.toString());
        String data = jsonObject.getString("data");
        log.info("topic --> {}", JSON.parseArray(data, Topic.class));
    }

    @org.junit.Test
    public void test3() {
        String s = SecurityUtils.encryptPassword("123456");
        System.out.println(s);
    }

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
