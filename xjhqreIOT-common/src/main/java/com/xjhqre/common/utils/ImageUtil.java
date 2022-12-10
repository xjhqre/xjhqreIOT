package com.xjhqre.common.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xjhqre.common.exception.ServiceException;
import com.xjhqre.common.utils.uuid.IdUtils;

/**
 * <p>
 * ImageUtil
 * </p>
 *
 * @author xjhqre
 * @since 10月 25, 2022
 */
public class ImageUtil {
    // public final static String SAVE_IMAGE_PATH =
    // "G:\\workspace\\xjhqreBBS\\xjhqreBBS-picture\\src\\main\\resources\\upload\\";

    public final static List<String> SUFFIXS =
        Arrays.asList(".jpg", ".jpeg", ".gif", ".png", ".JPG", ".JPEG", ".GIF", ".PNG");

    /**
     * 返回文件后缀
     * 
     * @param file
     * @return
     */
    public static String getSuffix(MultipartFile file) {
        String fileName = file.getOriginalFilename();// 获取原文件名
        if (fileName == null) {
            throw new ServiceException("非法文件名");
        }
        int index = fileName.lastIndexOf(".");
        return fileName.substring(index);
    }

    /**
     * 保存图片
     * 
     * @param mFile
     * @param file
     * @return
     */
    public static boolean saveImage(MultipartFile mFile, File file) {
        // 查看文件夹是否存在，不存在则创建
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            // 使用此方法保存必须要绝对路径且文件夹必须已存在,否则报错
            mFile.transferTo(file);
            return true;
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 文件id
     *
     * @return
     */
    public static String getFileId() {
        return IdUtils.simpleUUID();
    }

    // /**
    // * 返回图片保存地址
    // *
    // * @param name
    // * @return
    // */
    // public static String getNewImagePath(String name) {
    // return SAVE_IMAGE_PATH + name;
    // }

}
