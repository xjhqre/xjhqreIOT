package com.xjhqre.common.constant;

/**
 * 缓存的key 常量
 * 
 * @author xjhqre
 */
public class CacheConstants {
    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 邮箱验证码 email key
     */
    public static final String EMAIL_CODE_KEY = "email_codes:";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";

    /**
     * 用户状态 redis key
     */
    public static final String USER_STATUS = "user_status:";

    /**
     * 用户总的点赞数 HashMap<String, String>
     */
    public static final String USER_TOTAL_THUMB_COUNT_KEY = "user_total_thumb_count:";

    /**
     * 用户点赞的文章 HashMap<用户id, Set<文章id>>
     */
    public static final String USER_THUMB_ARTICLE_KEY = "user_thumb_article:";

    /**
     * 文章点赞的用户信息 HashMap<文章id, Set<用户id>>
     */
    public static final String ARTICLE_LIKED_USER_KEY = "article_liked_user:";

    /**
     * 浏览量 HashMap<文章id, Set<用户id>>
     */
    public static final String ARTICLE_VIEW_USER_KEY = "article_view_user_key:";
}
