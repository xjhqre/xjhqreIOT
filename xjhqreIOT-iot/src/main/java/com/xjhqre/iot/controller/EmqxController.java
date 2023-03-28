package com.xjhqre.iot.controller;

import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.exception.EmqxException;
import com.xjhqre.common.utils.AESUtils;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.iot.domain.dto.MqttClientConnectDTO;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.Product;
import com.xjhqre.iot.domain.model.Topic;
import com.xjhqre.iot.mqtt.EmqxService;
import com.xjhqre.iot.mqtt.MqttConfig;
import com.xjhqre.iot.service.DeviceService;
import com.xjhqre.iot.service.ProductService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * Emqx接口
 *
 * @author xjhqre
 * @since 2023-1-6
 */
@Api(tags = "Emqx接口")
@RestController
@Slf4j
@RequestMapping("/iot/emqx")
public class EmqxController extends BaseController {

    @Resource
    private DeviceService deviceService;
    @Resource
    private EmqxService emqxService;
    @Resource
    private MqttConfig mqttConfig;
    @Resource
    private ProductService productService;
    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    @ApiOperation("mqtt认证")
    @PostMapping("/mqtt/auth")
    public ResponseEntity<String> mqttAuth(@RequestParam String clientId, @RequestParam String username,
        @RequestParam String password) {
        log.info("clientId --> {}", clientId);
        log.info("username --> {}", clientId);
        log.info("password --> {}", password);
        if (clientId.startsWith("server")) {
            // 服务端认证：配置的账号密码认证
            if (this.mqttConfig.getUsername().equals(username) && this.mqttConfig.getPassword().equals(password)) {
                log.info("-----------服务端mqtt认证成功,clientId:" + clientId + "---------------");
                return ResponseEntity.ok().body("ok");
            } else {
                throw new EmqxException("mqtt账号和密码与认证服务器配置不匹配");
            }
        } else if (clientId.startsWith("web")) {
            // web端认证：token认证
            String token = password;
            if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
                token = token.replace(Constants.TOKEN_PREFIX, "");
            }
            try {
                Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
                log.info("-----------Web端mqtt认证成功,clientId:" + clientId + "---------------");
                return ResponseEntity.ok().body("ok");
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new EmqxException(ex.getMessage());
            }
        } else { // 设备端认证
            /*
             # 客户端Id等于 产品key & 设备编号
             clientId = productKey & deviceNumber
            # 用户名
            userName = 设备用户名
            # 密码
            password = (设备密码 & 过期时间) 经过AES加密
             */
            String[] clientArray = clientId.split("&");
            if (clientArray.length != 2 || clientArray[0].equals("") || clientArray[1].equals("")) {
                throw new EmqxException("设备客户端Id格式错误");
            }
            String productKey = clientArray[0];
            String deviceNumber = clientArray[1];
            Product product = this.productService.getById(productKey);
            Device device = this.deviceService.getByDeviceNumber(deviceNumber);
            if (product == null) {
                throw new EmqxException("设备认证，没有此id的产品");
            }
            if (device == null) {
                throw new EmqxException("设备认证，没有此id的设备");
            }
            if (!Objects.equals(device.getUserName(), username)) {
                throw new EmqxException("设备认证，设备用户名不匹配");
            }
            if (product.getStatus() != 2) {
                // 产品必须为发布状态：1-未发布，2-已发布
                throw new EmqxException("设备认证，设备对应的产品还未发布");
            }
            // 解析密码
            this.verifyPassword(productKey, deviceNumber, username, password);
            return ResponseEntity.ok().body("ok");
        }
    }

    @ApiOperation("mqtt钩子处理")
    @PostMapping("/mqtt/webhook")
    public void webHookProcess(@RequestBody MqttClientConnectDTO model) {
        log.info("webhook: " + model);
        // 过滤服务端、web端
        if (model.getClientId().startsWith("server") || model.getClientId().startsWith("web")) {
            return;
        }
        // 客户端id格式： productKey & deviceNumber (中间无空格)
        String[] clientArray = model.getClientId().split("&");
        String productKey = clientArray[0]; // 产品id
        String deviceNumber = clientArray[1]; // 设备编号

        // 根据设备编号查询设备
        Device device = this.deviceService.getByDeviceNumber(deviceNumber);

        // 设备状态（1-未激活，2-禁用，3-在线，4-离线）
        if (model.getAction().equals("client_disconnected")) {
            device.setStatus(4);
            this.deviceService.updateDeviceStatusAndLocation(device, "");
            // 设备掉线后发布设备状态
            // this.emqxService.publishStatus(device.getProductId(), device.getDeviceNumber(), 4, device.getIsShadow(),
            // device.getRssi());
            //// 清空保留消息，上线后发布新的属性功能保留消息
            // this.emqxService.publishProperty(device.getProductId(), device.getDeviceNumber(), null);
            // this.emqxService.publishFunction(device.getProductId(), device.getDeviceNumber(), null);
        } else if (model.getAction().equals("client_connected")) {
            device.setStatus(3);
            this.deviceService.updateDeviceStatusAndLocation(device, model.getIpaddress());
            // 影子模式，发布属性和功能
            // if (device.getIsShadow() == 1) {
            // ThingsModelShadow shadow = this.deviceService.getDeviceShadowThingsModel(device);
            // if (shadow.getProperties().size() > 0) {
            // this.emqxService.publishProperty(device.getProductId(), device.getDeviceNumber(),
            // shadow.getProperties());
            // }
            // if (shadow.getFunctions().size() > 0) {
            // this.emqxService.publishFunction(device.getProductId(), device.getDeviceNumber(),
            // shadow.getFunctions());
            // }
            // }
        }
    }

    @ApiOperation("获取NTP时间")
    @GetMapping("/ntp")
    public JSONObject ntp(@RequestParam Long deviceSendTime) {
        JSONObject ntpJson = new JSONObject();
        ntpJson.put("deviceSendTime", deviceSendTime);
        ntpJson.put("serverRecvTime", System.currentTimeMillis());
        ntpJson.put("serverSendTime", System.currentTimeMillis());
        return ntpJson;
    }

    public void verifyPassword(String productKey, String deviceNumber, String username, String encryptPassword) {

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getProductKey, productKey);
        Product product = this.productService.getOne(wrapper);
        String productSecret = product.getProductSecret();
        Device device = this.deviceService.getByDeviceNumber(deviceNumber);

        String decrypt = AESUtils.decrypt(encryptPassword, productSecret);
        if (decrypt == null || decrypt.equals("")) {
            throw new EmqxException("设备认证，设备密码解密失败");
        }
        String[] passwordArray = decrypt.split("&");
        if (passwordArray.length != 2) {
            // 密码加密格式 password & expireTime
            throw new EmqxException("设备认证，设备密码格式错误");
        }
        String decryptPassword = passwordArray[0];
        long expireTime = Long.parseLong(passwordArray[1]);
        // 验证密码
        if (!decryptPassword.equals(device.getDevicePassword())) {
            throw new EmqxException("设备认证，设备密码不匹配");
        }
        // 验证过期时间
        if (expireTime < System.currentTimeMillis()) {
            throw new EmqxException("设备认证，设备密码已过期");
        }
        // 设备状态验证 （1-未激活，2-禁用，3-在线，4-离线）
        if (device.getStatus() == 2) {
            throw new EmqxException("设备加密认证，设备处于禁用状态");
        }
        log.info("-----------设备加密认证成功,clientId: {}&{} ---------------", productKey, deviceNumber);
    }

    @ApiOperation("获取设备topic列表")
    @GetMapping("/listDeviceTopic")
    public List<Topic> listDeviceTopic(@RequestParam Long deviceId) {
        return this.emqxService.listDeviceTopic(deviceId);
    }
}
