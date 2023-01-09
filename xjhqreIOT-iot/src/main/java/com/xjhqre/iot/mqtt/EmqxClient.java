package com.xjhqre.iot.mqtt;

import javax.annotation.Resource;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import lombok.extern.slf4j.Slf4j;

/**
 * @Classname EmqxClient
 * @Description mqtt推送客户端
 */
@Slf4j
public class EmqxClient {
    @Resource
    private EmqxCallback emqxCallback;

    /**
     * 异步调用客户端，使用非阻塞方法与MQTT服务器通信，允许操作在后台运行。
     */
    public static MqttAsyncClient asyncClient;

    /**
     * 保存控制客户端连接到服务器的方式的选项集，包括用户名、密码等。
     */
    private MqttConnectOptions options;

    /**
     * EMQX服务器地址url
     */
    private String hostUrl;

    /**
     * 超时时间
     */
    private int timeout;

    /** 包活时间 */
    private int keepalive;

    /**
     * 客户端Id，不能相同，采用随机数 ${random.value}
     */
    private String clientId;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 是否清除会话 */
    private boolean clearSession;

    public EmqxClient(String clientId, String username, String password, String hostUrl, int timeout, int keepalive,
        boolean clearSession) {
        this.clientId = clientId;
        this.username = username;
        this.password = password;
        this.hostUrl = hostUrl;
        this.timeout = timeout;
        this.keepalive = keepalive;
        this.clearSession = clearSession;
    }

    /**
     * 连接MQTT服务器
     */
    public synchronized void connect() {

        /*设置配置*/
        if (this.options == null) {
            this.setOptions();
        }
        if (asyncClient == null) {
            this.createClient();
        }
        while (!asyncClient.isConnected()) {
            try {
                // 连接会调用 /mqtt/auth 接口
                IMqttToken token = asyncClient.connect(this.options);
                token.waitForCompletion();
            } catch (Exception e) {
                log.error("=====>>>>>mqtt连接失败 message={}", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建客户端
     */
    private void createClient() {
        if (asyncClient == null) {
            try {
                /*host为主机名，clientId是连接MQTT的客户端ID，MemoryPersistence设置clientId的保存方式
                默认是以内存方式保存*/
                asyncClient = new MqttAsyncClient(this.hostUrl, this.clientId, new MemoryPersistence());
                // 设置回调函数
                asyncClient.setCallback(this.emqxCallback);
                log.debug("====>>>mqtt客户端启动成功");
            } catch (MqttException e) {
                log.error("mqtt客户端连接错误 error={}", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置连接属性
     */
    private void setOptions() {
        this.options = new MqttConnectOptions();
        this.options.setUserName(this.username);
        this.options.setPassword(this.password.toCharArray());
        this.options.setConnectionTimeout(this.timeout);
        this.options.setKeepAliveInterval(this.keepalive);
        // 设置自动重新连接
        this.options.setAutomaticReconnect(true);
        this.options.setCleanSession(this.clearSession);
        log.debug("====>>>>设置mqtt参数成功");
    }

    /**
     * 断开与mqtt的连接
     */
    public synchronized void disconnect() {
        // 判断客户端是否null 是否连接
        if (asyncClient != null && asyncClient.isConnected()) {
            try {
                IMqttToken token = asyncClient.disconnect();
                token.waitForCompletion();
            } catch (MqttException e) {
                log.error("====>>>>断开mqtt连接发生错误 message={}", e.getMessage());
            }
        }
        asyncClient = null;
    }

    /**
     * 重新连接MQTT
     */
    public synchronized void refresh() {
        this.disconnect();
        this.setOptions();
        this.createClient();
        this.connect();
    }

    /**
     * 发布
     * 
     * @param qos
     *            连接方式
     * @param retained
     *            是否保留
     * @param topic
     *            主题
     * @param pushMessage
     *            消息体
     */
    public void publish(int qos, boolean retained, String topic, String pushMessage) {
        log.info("发布主题" + topic);
        MqttMessage message = new MqttMessage();
        message.setQos(qos);
        message.setRetained(retained);
        message.setPayload(pushMessage.getBytes());

        try {
            IMqttDeliveryToken token = asyncClient.publish(topic, message);
            token.waitForCompletion(); // 阻塞等待发布完成
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            log.error("=======>>>>>发布主题时发生错误 topic={},message={}", topic, e.getMessage());
        }
    }

    /**
     * 订阅某个主题
     * 
     * @param topic
     *            主题
     * @param qos
     *            消息质量 Qos1：消息发送一次，不确保 Qos2：至少分发一次，服务器确保接收消息进行确认 Qos3：只分发一次，确保消息送达和只传递一次
     */
    public void subscribe(String topic, int qos) {
        log.info("=======>>>>>订阅了主题 topic={}", topic);
        try {
            IMqttToken token = asyncClient.subscribe(topic, qos);
            token.waitForCompletion();
        } catch (MqttException e) {
            log.error("=======>>>>>订阅主题 topic={} 失败 message={}", topic, e.getMessage());
        }
    }

    /** 是否处于连接状态 */
    public boolean isConnected() {
        return asyncClient != null && asyncClient.isConnected();
    }

    public String getClientId() {
        return this.clientId;
    };
}