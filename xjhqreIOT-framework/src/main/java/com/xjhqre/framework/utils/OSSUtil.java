package com.xjhqre.framework.utils;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.fasterxml.jackson.annotation.JsonValue;
import com.xjhqre.common.exception.ServiceException;
import com.xjhqre.common.utils.FileUtils;
import com.xjhqre.common.utils.file.MimeTypeUtils;
import com.xjhqre.framework.config.properties.OssProperties;

/**
 * OSS上传工具类
 */
public class OSSUtil {

    // 定义日志
    private static final Logger LOGGER = LoggerFactory.getLogger(OSSUtil.class);
    // OSS 的地址
    private final static String OSS_END_POINT = OssProperties.END_POINT;
    // OSS 的key值
    private final static String OSS_ACCESS_KEY_ID = OssProperties.KEY_ID;
    // OSS 的secret值
    private final static String OSS_ACCESS_KEY_SECRET = OssProperties.KEY_SECRET;
    // OSS 的bucket名字
    private final static String OSS_BUCKET_NAME = OssProperties.BUCKET_NAME;
    // 设置URL过期时间为10年
    private final static Date OSS_URL_EXPIRATION = DateUtils.addDays(new Date(), 365 * 10);

    private volatile static OSSClient instance;

    private OSSUtil() {}

    /**
     * 单例
     * 
     * @return OSS工具类实例
     */
    private static OSSClient getOSSClient() {
        if (instance == null) {
            synchronized (OSSUtil.class) {
                if (instance == null) {
                    instance = new OSSClient(OSS_END_POINT, OSS_ACCESS_KEY_ID, OSS_ACCESS_KEY_SECRET);
                }
            }
        }
        return instance;
    }

    // 文件路径的枚举
    public enum FileDirType {
        AVATAR("avatar/"), PICTURE("picture/");

        private final String dir;

        FileDirType(String dir) {
            this.dir = dir;
        }

        @JsonValue
        public String getDir() {
            return this.dir;
        }
    }

    /**
     * 上传文件---去除URL中的？后的时间戳
     *
     * @param file
     *            文件
     * @param fileDir
     *            上传到OSS上文件的路径
     * @param pictureId
     * @return 文件的访问地址
     */
    public static String upload(MultipartFile file, FileDirType fileDir, String pictureId) {
        OSSUtil.createBucket();
        String fileName = OSSUtil.uploadFile(file, fileDir, pictureId); // 返回唯一文件名
        String fileOssURL = OSSUtil.getImgUrl(fileName, fileDir); // 返回OSS地址
        int firstChar = fileOssURL.indexOf("?");
        if (firstChar > 0) {
            fileOssURL = fileOssURL.substring(0, firstChar);
        }
        return fileOssURL;
    }

    /**
     * 当Bucket不存在时创建Bucket
     *
     * @throws OSSException
     *             异常
     * @throws ClientException
     *             Bucket命名规则： 1.只能包含小写字母、数字和短横线， 2.必须以小写字母和数字开头和结尾 3.长度在3-63之间
     */
    private static void createBucket() {
        try {
            if (!OSSUtil.getOSSClient().doesBucketExist(OSS_BUCKET_NAME)) {// 判断是否存在该Bucket，不存在时再重新创建
                OSSUtil.getOSSClient().createBucket(OSS_BUCKET_NAME);
            }
        } catch (Exception e) {
            LOGGER.error("{}", "创建Bucket失败,请核对Bucket名称(规则：只能包含小写字母、数字和短横线，必须以小写字母和数字开头和结尾，长度在3-63之间)");
            throw new ServiceException("创建Bucket失败,请核对Bucket名称(规则：只能包含小写字母、数字和短横线，必须以小写字母和数字开头和结尾，长度在3-63之间)");
        }
    }

    /**
     * 上传到OSS服务器 如果同名文件会覆盖服务器上的
     *
     * @param file
     *            文件
     * @param fileDir
     *            上传到OSS上文件的路径
     * @param pictureId
     * @return 唯一文件名，例如：asdasfwafa.jpg
     */
    private static String uploadFile(MultipartFile file, FileDirType fileDir, String pictureId) {
        // 生成文件名为 UUID.ext 的形式
        try (InputStream inputStream = file.getInputStream()) {
            // 创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inputStream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(getContentType(FileUtils.getExtension(file.getOriginalFilename())));
            objectMetadata.setContentDisposition("inline;filename=" + pictureId);
            // 上传文件
            PutObjectResult putResult = OSSUtil.getOSSClient().putObject(OSS_BUCKET_NAME, fileDir.getDir() + pictureId,
                inputStream, objectMetadata);
        } catch (Exception e) {
            throw new ServiceException("上传图片到OSS失败");
        }
        return pictureId;
    }

    /**
     * 获得文件路径
     * 
     * @param fileName
     *            文件的URL
     * @param fileDir
     *            文件在OSS上的路径
     * @return 文件的路径
     */
    private static String getImgUrl(String fileName, FileDirType fileDir) {
        if (StringUtils.isEmpty(fileName)) {
            LOGGER.error("{}", "文件地址为空");
            throw new RuntimeException("文件地址为空");
        }
        String[] split = fileName.split("/");

        // 获取oss图片URL失败
        URL url = OSSUtil.getOSSClient().generatePresignedUrl(OSS_BUCKET_NAME,
            fileDir.getDir() + split[split.length - 1], OSS_URL_EXPIRATION);
        if (url == null) {
            LOGGER.error("{}", "获取oss文件URL失败");
            throw new ServiceException("获取oss文件URL失败");
        }
        return url.toString();
    }

    /**
     * 判断OSS服务文件上传时文件的contentType
     *
     * @param FilenameExtension
     *            文件后缀
     * @return 后缀
     */
    private static String getContentType(String FilenameExtension) {
        if (FilenameExtension.equalsIgnoreCase("bmp")) {
            return MimeTypeUtils.IMAGE_BMP;
        }
        if (FilenameExtension.equalsIgnoreCase("gif")) {
            return MimeTypeUtils.IMAGE_GIF;
        }
        if (FilenameExtension.equalsIgnoreCase("jpeg") || FilenameExtension.equalsIgnoreCase("jpg")
            || FilenameExtension.equalsIgnoreCase("png")) {
            return MimeTypeUtils.IMAGE_JPEG;
        }
        if (FilenameExtension.equalsIgnoreCase("html")) {
            return "text/html";
        }
        if (FilenameExtension.equalsIgnoreCase("txt")) {
            return "text/plain";
        }
        if (FilenameExtension.equalsIgnoreCase("vsd")) {
            return "application/vnd.visio";
        }
        if (FilenameExtension.equalsIgnoreCase("pptx") || FilenameExtension.equalsIgnoreCase("ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (FilenameExtension.equalsIgnoreCase("docx") || FilenameExtension.equalsIgnoreCase("doc")) {
            return "application/msword";
        }
        if (FilenameExtension.equalsIgnoreCase("xml")) {
            return "text/xml";
        }
        return "image/jpeg";
    }
}