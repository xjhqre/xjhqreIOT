# /***********************************************************
#  * author: xiaoY [物美智能 wumei-smart]
#  * create: 2022-05-10
#  * email：qimint@outlook.com
#  * source:https://github.com/kerwincui/wumei-smart
#  * board:raspberry 4b
#  ***********************************************************/

import json
import paho.mqtt.client as mqtt
import random
import requests
import threading  # 导入线程模块，用作定时器
import time

from aes import AesCryptor

#################################################################
# 需要连接好外部网络
#################################################################

# 作为python的AES的iv,应该为16位，字节型数据
iv = b"wumei-smart-open"
# 发布监测数据的最大次数
monitorCount = 5
#  发布监测数据的间隔，默认5秒。 使用esp8266单片机时，服务器传来的间隔单位为毫秒，本程序由于定时运行需要的是秒，将转化为秒，如需毫秒运行，自行更改程序
monitorInterval = 5
# NTP地址（用于获取时间,可选的修改为自己部署项目的地址）
ntpServer = "http://120.24.218.158:8080/iot/tool/ntp?deviceSendTime="
# 连接成功标志位
g_rc = -1
# 全局变量，管理定时监测
global t2

# 设备信息配置
deviceNum = "DW43CI6RM8GMG23H"
userId = "1"
productId = "4"
productSecret = "4"
firmwareVersion = "1.0"
# 经度和纬度可选，如果产品使用设备定位，则必须传
latitude = 0
longitude = 0

# Mqtt配置
mqttHost = "120.24.218.158"
mqttPort = 1883
mqttUserName = "admin"
mqttPwd = "xjhqre"
# 作为python的AES的key,应该为16位，字节型数据
mqttSecret = b"K2IB784BM0O01GG6"

# 订阅的主题
prefix = "/" + productSecret + "/" + deviceNum
sInfoTopic = prefix + "/info/get"
sOtaTopic = prefix + "/ota/get"
sNtpTopic = prefix + "/ntp/get"
sPropertyTopic = prefix + "/property/get"
sFunctionTopic = prefix + "/function/get"
sPropertyOnline = prefix + "/property-online/get"
sFunctionOnline = prefix + "/function-online/get"
sMonitorTopic = prefix + "/monitor/get"
# 发布的主题
pInfoTopic = prefix + "/info/post"
pNtpTopic = prefix + "/ntp/post"
pPropertyTopic = prefix + "/property/post"
pFunctionTopic = prefix + "/function/post"
pMonitorTopic = prefix + "/monitor/post"
pEventTopic = prefix + "/event/post"

# 初始化，连接设备mqtt客户端Id格式为：产品ID & 设备编号
clientId = productId + "&" + deviceNum
client = mqtt.Client(clientId)


# 加密 (AES-CBC-128-pkcs5padding)
def encrypt(plain_data, key, iv):
    aes = AesCryptor(key, iv, padding_mode="PKCS5Padding")
    r_data = aes.encryptFromString(plain_data)
    print_msg("密码(已加密)：" + r_data.toBase64())
    return r_data.toBase64()


# 回调函数。当尝试与MQTT broker 建立连接时，触发该函数。
# client 是本次连接的客户端实例。
# userdata 是用户的信息，一般为空。但如果有需要，也可以通过 user_data_set 函数设置。
# flags 保存服务器响应标志的字典
# rc 是响应码。
# 0: 连接成功
# 1: 连接失败-不正确的协议版本
# 2: 连接失败-无效的客户端标识符
# 3: 连接失败-服务器不可用
# 4: 连接失败-错误的用户名或密码
# 5: 连接失败-未授权
# 6-255: 未定义.
# 一般情况下，我们只需要关注rc响应码是否为0就可以了。
def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print_msg("连接成功")
        # 放在on_connect下可以保证重连重新订阅
        # 订阅(OTA、NTP、属性、功能、实时监测)
        client.subscribe(sInfoTopic, 1)
        client.subscribe(sOtaTopic, 1)
        client.subscribe(sNtpTopic, 1)
        client.subscribe(sPropertyTopic, 1)
        client.subscribe(sFunctionTopic, 1)
        client.subscribe(sPropertyOnline, 1)
        client.subscribe(sFunctionOnline, 1)
        client.subscribe(sMonitorTopic, 1)

        print_msg("订阅主题：" + sInfoTopic)
        print_msg("订阅主题：" + sOtaTopic)
        print_msg("订阅主题：" + sNtpTopic)
        print_msg("订阅主题：" + sPropertyTopic)
        print_msg("订阅主题：" + sFunctionTopic)
        print_msg("订阅主题：" + sPropertyOnline)
        print_msg("订阅主题：" + sFunctionOnline)
        print_msg("订阅主题：" + sMonitorTopic)
        # 发布设备信息
        publish_info()
        global g_rc
        g_rc = 0
    else:
        print_msg("连接失败，rc=" + str(rc))
        print_msg("3秒后重连...")
        time.sleep(3)
        connect_mqtt()


#  物模型-属性处理
def process_property(payload):
    data = json.loads(payload)
    for item in data:
        #  匹配云端定义的属性（不包含属性中的监测数据）
        id = item["id"]
        value = item["value"]
        print_msg(str(id) + ":" + str(value))
    #  最后发布属性，服务端订阅存储（重要）
    publish_property(json.dumps(data))


#  物模型-功能处理
def process_function(payload):
    data = json.loads(payload)
    for item in data:
        # 匹配云端定义的功能
        id = item["id"]
        value = item["value"]
        if id == "switch":
            print_msg("开关 switch：" + str(value))
        elif id == "gear":
            print_msg("档位 gear：" + str(value))
        elif id == "light_color":
            print_msg("灯光颜色 light_color：" + str(value))
        elif id == "message":
            print_msg("屏显消息 message：" + str(value))
        elif id == "report_monitor":
            msg = random_property_data()
            print_msg("订阅到上报监测数据指令，上报数据：")
            print_msg(msg)
            publish_property(msg)
    #  最后发布属性，服务端订阅存储（重要）
    publish_property(json.dumps(data))


# 回调函数，在客户端订阅的主题上接收到消息时调用，“message”变量是一个MQTT消息描述所有消息特征
def on_message(client, userdata, msg):
    print_msg("接收数据:" + msg.topic + " " + str(msg.payload))
    if msg.topic == sOtaTopic:
        print_msg("订阅到设备升级指令...")
        json_data = json.loads(msg.payload)
        new_version = json_data["version"]
        download_url = json_data["downloadUrl"]
        print_msg("固件版本：" + new_version)
        print_msg("下载地址：" + download_url)
    elif msg.topic == sInfoTopic:
        print_msg("订阅到设备信息指令...")
        # 发布设备信息
        publish_info()
    elif msg.topic == sNtpTopic:
        print_msg("订阅到NTP时间...");
        json_data = json.loads(msg.payload)
        device_send_time = json_data["deviceSendTime"]
        server_send_time = json_data["serverSendTime"]
        server_recv_time = json_data["serverRecvTime"]
        device_recv_time = round(time.time() * 1000)
        now = (server_send_time + server_recv_time + device_recv_time - device_send_time) / 2
        print_msg("当前时间：" + str(round(now)))
    elif msg.topic == sPropertyTopic or msg.topic == sPropertyOnline:
        print_msg("订阅到属性指令...")
        process_property(msg.payload)
    elif msg.topic == sFunctionTopic or msg.topic == sFunctionOnline:
        print_msg("订阅到功能指令...")
        process_function(msg.payload)
    elif msg.topic == sMonitorTopic:
        # python全局变量的使用
        global t2
        global monitorCount
        global monitorInterval
        print_msg("订阅到实时监测指令...")
        json_data = json.loads(msg.payload)
        monitorCount = json_data["count"]
        monitorInterval = json_data["interval"] / 1000
        t2.cancel()
        t2 = threading.Timer(monitorInterval, timing_publish_monitor)
        t2.start()


# 1.发布设备信息
def publish_info():
    # rssi值 树莓派中暂时不处理wifi信号问题
    #  信号强度（信号极好4格[-55— 0]，信号好3格[-70— -55]，信号一般2格[-85— -70]，信号差1格[-100— -85]）
    # status值 （1-未激活，2-禁用，3-在线，4-离线）
    doc = {"rssi": 1, "firmwareVersion": firmwareVersion, "status": 3, "userId": userId, "longitude": longitude,
           "latitude": latitude, "summary": {"name": "device", "chip": "esp8266", "author": "xjhqre", "version": 1.6,
                                             "create": "2022 - 06 - 06"}}
    #     client.publish('raspberry/topic',payload=i,qos=0,retain=False) 
    json_data = json.dumps(doc)
    print_msg("发布设备信息：" + pInfoTopic + " " + json_data)
    client.publish(pInfoTopic, json_data)


#  2.发布时钟同步信，用于获取当前时间(可选)
def publish_ntp():
    data = {"deviceSendTime": round(time.time() * 1000)}
    json_data = json.dumps(data)
    print_msg("发布NTP信息" + json_data)
    client.publish(pNtpTopic, json_data)


# 3.发布属性
# msg 接收格式json
def publish_property(msg):
    print_msg("发布属性:" + msg)
    client.publish(pPropertyTopic, msg)


#  4.发布功能
def publish_function(msg):
    print_msg("发布功能:" + msg)
    client.publish(pFunctionTopic, msg)


# 5.发布事件
def publish_event():
    obj_temperature = {"id": "height_temperature", "value": 40, "remark": "温度过高警告"}
    obj_exception = {"id": "exception", "value": "异常消息，消息内容XXXXXXXX", "remark": "设备发生错误"}
    data = [obj_temperature, obj_exception]
    json_data = json.dumps(data)
    print_msg("发布事件:" + json_data)
    client.publish(pEventTopic, json_data)


# 6.发布实时监测数据
def publish_monitor():
    msg = random_property_data()
    # 发布为实时监测数据，不会存储
    print_msg("发布实时监测数据：" + msg)
    client.publish(pMonitorTopic, msg)


# 随机生成监测值
def random_property_data():
    # 匹配云端定义的监测数据，随机数代替监测结果
    # random.randint(0,10) #生成数据包括0,10 
    # random.uniform(30,60)生成数据为浮点型
    obj_temperature = {"id": "temperature", "value": str(round(random.uniform(10, 30), 2)), "remark": ""}
    obj_humidity = {"id": "humidity", "value": str(round(random.uniform(30, 60), 2)), "remark": ""}
    obj_co2 = {"id": "co2", "value": str(random.randint(400, 1000)), "remark": ""}
    obj_brightness = {"id": "brightness", "value": str(random.randint(1000, 10000)), "remark": ""}
    print_msg("随机生成监测数据值:")
    data = [obj_temperature, obj_humidity, obj_co2, obj_brightness]
    print(json.dumps(data))
    return json.dumps(data)


# 连接mqtt
def connect_mqtt():
    print_msg("连接Mqtt服务器")
    # 生成mqtt认证密码(设备加密认证，密码加密格式为：mqtt密码 & 过期时间)
    password = generationPwd()
    encrypt_password = encrypt(password, mqttSecret, iv)
    client.username_pw_set(mqttUserName, encrypt_password)
    client.on_connect = on_connect
    client.on_message = on_message
    client.connect(mqttHost, mqttPort, 10)


# 打印提示信息
def print_msg(msg):
    print("[{}] {}".format(time.strftime("%Y-%m-%d %H:%M:%S"), msg))


# 生成密码
def generationPwd():
    try:
        doc = json.loads(getTime())
    except:
        print_msg("Json解析失败")
        exit()
    deviceSendTime = doc["deviceSendTime"]
    serverSendTime = doc["serverSendTime"]
    serverRecvTime = doc["serverRecvTime"]
    deviceRecvTime = round(time.time() * 1000)
    now = (serverSendTime + serverRecvTime + deviceRecvTime - deviceSendTime) / 2
    expireTime = int(now + 1 * 60 * 60 * 1000)
    # 密码加密格式为：mqtt密码 & 过期时间
    password = mqttPwd + "&" + str(expireTime, 0)
    print_msg("密码(未加密):" + password)
    return password


# HTTP获取时间
def getTime():
    try:
        r = requests.get(ntpServer + str(round(time.time() * 1000)))
        if (r.status_code > 0):
            if (r.status_code == 200 or r.status_code == 301):
                print_msg("获取时间成功，data:" + r.text)
                return r.text
        else:
            print_msg("获取时间失败，error:" + r.status_code)
    except:
        print_msg("连接Http失败")


# 定时上报属性
def timing_publish_property():
    print_msg("执行定时上报")
    # 发布事件
    publish_event()
    # 发布时钟同步
    publish_ntp()
    #  发布属性(监测值)
    msg = random_property_data()
    publish_property(msg)
    t1 = threading.Timer(60, timing_publish_property)
    t1.start()


# 定时上报监测数据
def timing_publish_monitor():
    global monitorCount
    monitorCount = monitorCount - 1
    print_msg("执行监测")
    publish_monitor()
    if monitorCount > 0:
        t2 = threading.Timer(monitorInterval, timing_publish_monitor)
        t2.start()


if __name__ == '__main__':
    connect_mqtt()
    client.loop_start()
    print_msg("等待连接MQTT")
    while g_rc != 0:
        print("-", end=" ")
        time.sleep(1)
    t1 = threading.Timer(60, timing_publish_property)
    t1.setDaemon(True)  # 当主线程被关闭后，子线程也关闭
    t1.start()
    t2 = threading.Timer(monitorInterval, timing_publish_monitor)
    t2.setDaemon(True)  # 当主线程被关闭后，子线程也关闭
    t2.start()

    while True:
        time.sleep(10)  # 定时上报、检测上报都是线程执行，主线程可以做自己的任务
