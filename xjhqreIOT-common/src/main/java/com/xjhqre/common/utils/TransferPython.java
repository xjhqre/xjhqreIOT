package com.xjhqre.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class TransferPython {

    /**
     * 发送到Python进行处理
     *
     * @param ids
     * @param urls
     * @return
     */
    public static String sendToProcess(String ids, String urls) {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String host = addr.getHostName();

            // 初始化套接字，设置访问服务的主机和进程端口号，HOST是访问python进程的主机名称，可以是IP地址或者域名，PORT是python进程绑定的端口号
            Socket socket = new Socket(host, 12345);

            // 获取输出流对象
            OutputStream os = socket.getOutputStream();
            PrintStream out = new PrintStream(os);
            // 发送内容
            out.print(ids);
            // 分隔字符
            out.print("|");
            out.print(urls);
            // 告诉服务进程，内容发送完毕，可以开始处理
            out.print("over");
            // 获取服务进程的输入流
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String tmp;
            StringBuilder sb = new StringBuilder();
            // 读取内容
            while ((tmp = br.readLine()) != null)
                sb.append(tmp).append('\n');
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "500";
        }
    }

    /**
     * 发送图片地址给Python
     *
     * @param path 图片本地地址
     * @return
     */
    public static String sendFilePath(String path) {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String host = addr.getHostName();

            // 初始化套接字，设置访问服务的主机和进程端口号，HOST是访问python进程的主机名称，可以是IP地址或者域名，PORT是python进程绑定的端口号
            Socket socket = new Socket(host, 12346);

            // 获取输出流对象
            OutputStream os = socket.getOutputStream();
            PrintStream out = new PrintStream(os);
            // 发送图片本地地址
            out.print(path);
            // 告诉服务进程，内容发送完毕，可以开始处理
            out.print("over");
            // 获取服务进程的输入流
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String tmp;
            StringBuilder sb = new StringBuilder();
            // 读取内容
            while ((tmp = br.readLine()) != null)
                sb.append(tmp).append('\n');
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "500";
        }
    }
}