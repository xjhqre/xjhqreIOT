package com.xjhqre.common.utils;

import org.apache.commons.io.FilenameUtils;

/**
 * <p>
 * FileUtil
 * </p>
 *
 * @author xjhqre
 * @since 10月 28, 2022
 */
public class FileUtils {

    /**
     * 含 . 号，例如 .jpg
     * 
     * @return
     */
    public static final String getExtension(String filename) {
        return "." + FilenameUtils.getExtension(filename);
    }
}
