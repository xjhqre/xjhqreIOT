package com.xjhqre.common.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.utils.http.HttpHelper;

/**
 * 构建可重复读取inputStream的request
 * 
 * @author xjhqre
 */
public class RepeatedlyRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] body; // 用来缓存从HttpServletRequest的io流中读取的参数转为字节缓存下来

    // 初始化的时候，就从request读取放到属性字段
    public RepeatedlyRequestWrapper(HttpServletRequest request, ServletResponse response) throws IOException {
        super(request);
        request.setCharacterEncoding(Constants.UTF8);
        response.setCharacterEncoding(Constants.UTF8);

        this.body = HttpHelper.getBodyString(request).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    /**
     * 获取请求的输入流
     * 
     * @return
     */
    @Override
    public ServletInputStream getInputStream() { // 后续读取流的操作都是从属性字段中读取的缓存下来的信息
        final ByteArrayInputStream bais = new ByteArrayInputStream(this.body);
        return new ServletInputStream() {
            @Override
            public int read() {
                return bais.read();
            }

            @Override
            public int available() {
                return RepeatedlyRequestWrapper.this.body.length;
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }
}
