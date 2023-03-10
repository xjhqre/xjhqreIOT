package com.xjhqre.framework.security.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.xjhqre.common.constant.CacheConstants;
import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.domain.model.LoginUser;
import com.xjhqre.common.utils.ServletUtils;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.common.utils.ip.AddressUtils;
import com.xjhqre.common.utils.ip.IpUtils;
import com.xjhqre.common.utils.redis.RedisCache;
import com.xjhqre.common.utils.uuid.IdUtils;

import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * token验证处理
 *
 * @author xjhqre
 */
@Component
public class TokenService {
    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    // 令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private int expireTime;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    // 30 分钟
    private static final Long MILLIS_MINUTE_TEN = 30 * 60 * 1000L;

    // redis 工具类
    @Autowired
    private RedisCache redisCache;

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = this.getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            try {
                Claims claims = this.parseToken(token);
                // 解析对应的权限以及用户信息
                String uuid = (String)claims.get(Constants.LOGIN_USER_KEY);
                String userKey = this.getTokenKey(uuid);
                return this.redisCache.getCacheObject(userKey);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUser loginUser) {
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNotEmpty(loginUser.getToken())) {
            this.refreshToken(loginUser);
        }
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userKey = this.getTokenKey(token);
            this.redisCache.deleteObject(userKey);
        }
    }

    /**
     * 创建令牌
     *
     * @param loginUser
     *            用户信息
     * @return 令牌
     */
    public String createToken(LoginUser loginUser) {
        String token = IdUtils.fastUUID();
        loginUser.setToken(token);
        this.setUserAgent(loginUser);
        // 将用户信息存入redis
        this.refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, token);
        claims.put(Claims.SUBJECT, loginUser.getUsername());
        return this.createToken(claims);
    }

    /**
     * 验证令牌有效期，相差不足30分钟，自动刷新缓存
     *
     * @param loginUser
     * @return 令牌
     */
    public void verifyToken(LoginUser loginUser) {
        long thisExpireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (thisExpireTime - currentTime <= MILLIS_MINUTE_TEN) {
            this.refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser
     *            登录信息
     */
    public void refreshToken(LoginUser loginUser) {
        String address = AddressUtils.getRealAddressByIP(loginUser.getUser().getLoginIp());
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setLoginLocation(address);
        loginUser.setExpireTime(loginUser.getLoginTime() + this.expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = this.getTokenKey(loginUser.getToken());
        this.redisCache.setCacheObject(userKey, loginUser, this.expireTime, TimeUnit.MINUTES);
    }

    /**
     * 设置用户代理信息
     *
     * @param loginUser
     *            登录信息
     */
    public void setUserAgent(LoginUser loginUser) {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        loginUser.setIpaddr(ip);
        // loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims
     *            数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, this.secret).compact();
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token
     *            令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token
     *            令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = this.parseToken(token);
        return claims.getSubject();
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(this.header);
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return token;
    }

    private String getTokenKey(String uuid) {
        return CacheConstants.LOGIN_TOKEN_KEY + uuid;
    }
}
