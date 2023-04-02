package com.xjhqre.emqx.service.impl;

import static com.xjhqre.common.utils.SecurityUtils.getUsername;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.utils.BaiduMapUtils;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.common.utils.http.HttpUtils;
import com.xjhqre.common.utils.ip.IpUtils;
import com.xjhqre.emqx.domain.entity.Device;
import com.xjhqre.emqx.domain.entity.DeviceLog;
import com.xjhqre.emqx.mapper.DeviceMapper;
import com.xjhqre.emqx.service.DeviceLogService;
import com.xjhqre.emqx.service.DeviceService;

import lombok.extern.slf4j.Slf4j;

/**
 * 设备Service业务层处理
 *
 * @author xjhqre
 * @date 2021-12-16
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {

    @Resource
    private DeviceMapper deviceMapper;

    @Resource
    private DeviceLogService deviceLogService;

    /**
     * 根据设备编号查询设备
     */
    @Override
    public Device getByDeviceNumber(String deviceNumber) {
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getDeviceNumber, deviceNumber);
        return this.deviceMapper.selectOne(wrapper);
    }

    /**
     * @param device
     *            设备状态和定位更新
     * @return 结果
     */
    @Override
    public void updateDeviceStatusAndLocation(Device device, String ipAddress) {
        // 设置自动定位和状态
        if (!"".equals(ipAddress)) {
            if (device.getActiveTime() == null) {
                device.setActiveTime(DateUtils.getNowDate());
            }
            // 定位方式(1=ip自动定位，2=设备定位，3=自定义)
            if (device.getLocationWay() == 1) {
                device.setNetworkIp(ipAddress);
                this.setLocation(ipAddress, device);
            } else if (device.getLocationWay() == 2) {
                // 设备上报经纬度定位，不在此设置位置
                device.setAddress(null);
                device.setLatitude(null);
                device.setLongitude(null);
            } else {
                // 用户自定义经纬度，根据经纬度设置地址
                device.setAddress(BaiduMapUtils.getCity(device.getLatitude(), device.getLongitude()));
            }
        }
        this.deviceMapper.updateById(device);

        // 添加到设备日志
        DeviceLog deviceLog = new DeviceLog();
        deviceLog.setDeviceId(device.getDeviceId());
        deviceLog.setDeviceName(device.getDeviceName());
        deviceLog.setUserId(device.getUserId());
        deviceLog.setUserName(device.getUserName());
        deviceLog.setCreateTime(DateUtils.getNowDate());
        deviceLog.setCreateBy(getUsername());
        if (device.getStatus() == 3) {
            deviceLog.setLogValue("1");
            deviceLog.setRemark("设备上线");
            deviceLog.setLogType(5);
        } else if (device.getStatus() == 4) {
            deviceLog.setLogValue("0");
            deviceLog.setRemark("设备离线");
            deviceLog.setLogType(6);
        }
        this.deviceLogService.save(deviceLog);
    }

    /**
     * 根据IP获取地址
     *
     */
    private void setLocation(String ip, Device device) {
        String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";
        // 内网不查询
        if (IpUtils.internalIp(ip)) {
            device.setAddress("内网IP");
        }
        try {
            String rspStr = HttpUtils.sendGet(IP_URL, "ip=" + ip + "&json=true", Constants.GBK);
            if (!StringUtils.isEmpty(rspStr)) {
                JSONObject obj = JSONObject.parseObject(rspStr);
                device.setAddress(obj.getString("addr"));
                log.info(device.getDeviceNumber() + "- 设置地址：" + obj.getString("addr"));
                // 设置经纬度
                Map<String, Double> lngAndLat = BaiduMapUtils.getLngAndLat(obj.getString("city"));
                device.setLongitude(lngAndLat.get("lng")); // 经度
                device.setLatitude(lngAndLat.get("lat")); // 维度
                log.info(device.getDeviceNumber() + "- 设置经度：" + lngAndLat.get("lng") + "，设置纬度：" + lngAndLat.get("lat"));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
