package com.xjhqre.common.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xjhqre.common.utils.http.HttpUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 百度地图经纬度和地址相互转换的工具类
 * 
 * @author xjhqre
 * @since 2023-1-4
 */
@Slf4j
public class BaiduMapUtils {

    // 百度地图服务端AK
    public static final String SERVICE = "Od6x0zAUSA3NVrjAf8Ypg49bMsh1cE3C";
    // 百度地图浏览器端AK
    public static final String BROWSER = "FkcMBlhjEto9Ibmtxfp4iA6S4xr8y5uK";

    /**
     * 百度地图通过经纬度来获取地址,传入参数纬度lat、经度lng
     */
    public static String getCity(Double lat, Double lng) {
        String url = "https://api.map.baidu.com/reverse_geocoding/v3/?ak=" + SERVICE
            + "&output=json&coordtype=wgs84ll&location=" + lat + "," + lng + "";
        String baiduResponse = HttpUtils.sendGet(url);
        JSONObject jsonObject =
            JSON.parseObject(baiduResponse).getJSONObject("result").getJSONObject("addressComponent");
        return jsonObject.getString("city");
    }

    /**
     * 百度地图通过地址来获取经纬度，传入参数address
     */
    public static Map<String, Double> getLngAndLat(String address) {
        Map<String, Double> map = new HashMap<>();
        String url = "https://api.map.baidu.com/geocoding/v3/?address=" + address + "&output=json&ak=" + SERVICE
            + "&callback=showLocation";
        String baiduResponse = HttpUtils.sendGet(url);
        // 去除不必要的字符串
        baiduResponse = baiduResponse.replaceAll("showLocation&&showLocation\\(", "");
        baiduResponse = baiduResponse.replaceAll("\\)", "");
        JSONObject obj = JSON.parseObject(baiduResponse);
        if (obj.get("status").toString().equals("0")) {
            double lng = obj.getJSONObject("result").getJSONObject("location").getDouble("lng"); // 经度
            double lat = obj.getJSONObject("result").getJSONObject("location").getDouble("lat"); // 纬度
            map.put("lng", lng);
            map.put("lat", lat);
            log.info("经度：" + lng + "--- 纬度：" + lat);
        } else {
            log.info("未找到相匹配的经纬度！");
        }
        return map;
    }

}