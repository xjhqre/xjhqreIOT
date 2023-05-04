package com.xjhqre.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 文件类型常量
 * </p>
 *
 * @author xjhqre
 * @since 4月 18, 2023
 */
public final class FileDirConstants {

    private static final Map<Integer, String> FileDirTypeMap = new HashMap<>();

    static {
        FileDirTypeMap.put(1, "avatar/");
        FileDirTypeMap.put(2, "product/");
        FileDirTypeMap.put(3, "screenshot/");
        FileDirTypeMap.put(4, "device/");
        FileDirTypeMap.put(5, "picture/");
        FileDirTypeMap.put(6, "firmware/");
    }

    // common
    public static final String COMMON = "common/";

    // 头像目录
    public static final String AVATAR = "avatar/";

    // 产品目录
    public static final String PRODUCT = "product/";

    // 截图目录
    public static final String SCREENSHOT = "screenshot/";

    // 设备文件目录
    public static final String DEVICE = "device/";

    // 图片目录
    public static final String PICTURE = "picture/";

    // 固件目录
    public static final String FIRMWARE = "firmware/";

    public static String getFileDirTypeMap(int key) {
        return FileDirTypeMap.get(key);
    }

}
