package com.xjhqre.common.constant;

/**
 * 用户常量信息
 * 
 * @author xjhqre
 */
public class UserConstants {
    /**
     * 平台内系统用户的唯一标志
     */
    public static final String SYS_USER = "SYS_USER";

    /** 正常状态 */
    public static final String NORMAL = "1";

    /** 异常状态 */
    public static final String EXCEPTION = "0";

    /** 用户封禁状态 */
    public static final String USER_DISABLE = "0";

    /** 角色封禁状态 */
    public static final String ROLE_DISABLE = "0";

    /** 字典正常状态 */
    public static final String DICT_NORMAL = "1";

    /** 是否为系统默认（是） */
    public static final String YES = "Y";

    /** 菜单类型（目录） */
    public static final String TYPE_DIR = "M";

    /** 菜单类型（菜单） */
    public static final String TYPE_MENU = "C";

    /**
     * 是否菜单外链（是）
     */
    public static final Integer YES_FRAME = 1;

    /**
     * 是否菜单外链（否）
     */
    public static final Integer NO_FRAME = 0;

    /** 菜单类型（按钮） */
    public static final String TYPE_BUTTON = "F";

    /** InnerLink组件标识 */
    public final static String INNER_LINK = "InnerLink";

    /** Layout组件标识 */
    public final static String LAYOUT = "Layout";

    /** ParentView组件标识 */
    public final static String PARENT_VIEW = "ParentView";

    /**
     * 用户名长度限制
     */
    public static final int USERNAME_MIN_LENGTH = 2;
    public static final int USERNAME_MAX_LENGTH = 20;

    /**
     * 密码长度限制
     */
    public static final int PASSWORD_MIN_LENGTH = 5;
    public static final int PASSWORD_MAX_LENGTH = 20;
}
