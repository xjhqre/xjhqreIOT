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
public final class FileTypeConstants {

    private static final Map<Integer, String> FileTypeMap = new HashMap<>();

    static {
        FileTypeMap.put(1, "picture");
        FileTypeMap.put(2, "file");
        FileTypeMap.put(3, "video");
        FileTypeMap.put(4, "audio");
    }

    public static final Integer PICTURE = 1;
    public static final Integer FILE = 2;
    public static final Integer VIDEO = 3;
    public static final Integer AUDIO = 4;

    public static String getFileTypeMap(int key) {
        return FileTypeMap.get(key);
    }

}
