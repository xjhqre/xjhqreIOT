package com.xjhqre.common.utils.file;

import com.xjhqre.common.config.RuoYiConfig;

/**
 * 文件上传工具类
 *
 * @author ruoyi
 */
public class FileUploadUtils {
    /**
     * 默认大小 50M
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024;

    /**
     * 默认的文件名最大长度 100
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    /**
     * 默认上传的地址
     */
    private static String defaultBaseDir = RuoYiConfig.getProfile();

    public static void setDefaultBaseDir(String defaultBaseDir) {
        FileUploadUtils.defaultBaseDir = defaultBaseDir;
    }

    public static String getDefaultBaseDir() {
        return defaultBaseDir;
    }

    /// **
    // * 以默认配置进行文件上传
    // *
    // * @param file
    // * 上传的文件
    // * @return 文件名称
    // */
    // public static String upload(MultipartFile file) throws IOException {
    // try {
    // return upload(getDefaultBaseDir(), file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
    // } catch (Exception e) {
    // throw new IOException(e.getMessage(), e);
    // }
    // }

    /// **
    // * 根据文件路径上传
    // *
    // * @param baseDir
    // * 相对应用的基目录
    // * @param file
    // * 上传的文件
    // * @return 文件名称
    // */
    // public static String upload(String baseDir, MultipartFile file) throws IOException {
    // try {
    // return upload(baseDir, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
    // } catch (Exception e) {
    // throw new IOException(e.getMessage(), e);
    // }
    // }

    /// **
    // * 文件上传
    // *
    // * @param baseDir
    // * 相对应用的基目录
    // * @param file
    // * 上传的文件
    // * @param allowedExtension
    // * 上传文件类型
    // * @return 返回上传成功的文件名
    // * @throws FileSizeLimitExceededException
    // * 如果超出最大大小
    // * @throws FileNameLengthLimitExceededException
    // * 文件名太长
    // * @throws IOException
    // * 比如读写文件出错时
    // * @throws InvalidExtensionException
    // * 文件校验异常
    // */
    // public static String upload(String baseDir, MultipartFile file, String[] allowedExtension)
    // throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException,
    // InvalidExtensionException {
    // String originalFilename = file.getOriginalFilename();
    // if (StringUtils.isNotEmpty(originalFilename)) {
    // assert originalFilename != null;
    // int fileNameLength = originalFilename.length();
    // if (fileNameLength > FileUploadUtils.DEFAULT_FILE_NAME_LENGTH) {
    // throw new FileNameLengthLimitExceededException(FileUploadUtils.DEFAULT_FILE_NAME_LENGTH);
    // }
    // }
    //
    // // 判断文件大小是否低于限制
    // assertAllowed(file, allowedExtension);
    //
    // // 组装文件名
    // String fileName = extractFilename(file);
    //
    // File desc = getAbsoluteFile(baseDir, fileName);
    // // 写入该文件到磁盘
    // file.transferTo(desc);
    // // 获取文件路径名
    // return getPathFileName(baseDir, fileName);
    // }

    /// **
    // * 编码文件名
    // */
    // public static String extractFilename(MultipartFile file) {
    // String extension = FileTypeUtils.getExtension(file);
    // return DateUtils.datePath() + "/" + IdUtils.fastUUID() + "." + extension;
    // }
    //
    // public static File getAbsoluteFile(String uploadDir, String fileName) {
    // File desc = new File(uploadDir + File.separator + fileName);
    //
    // if (!desc.exists()) {
    // // 如果父文件不存在
    // if (!desc.getParentFile().exists()) {
    // boolean mkdirs = desc.getParentFile().mkdirs();
    // if (!mkdirs) {
    // throw new UtilException("创建文件夹失败！！");
    // }
    // }
    // }
    // return desc;
    // }

    // public static String getPathFileName(String uploadDir, String fileName) {
    // int dirLastIndex = RuoYiConfig.getProfile().length() + 1;
    // String currentDir = StringUtils.substring(uploadDir, dirLastIndex);
    // return Constants.RESOURCE_PREFIX + "/" + currentDir + "/" + fileName;
    // }
    //
    /// **
    // * 文件大小校验
    // *
    // * @param file
    // * 上传的文件
    // * @throws FileSizeLimitExceededException
    // * 如果超出最大大小
    // */
    // public static void assertAllowed(MultipartFile file, String[] allowedExtension)
    // throws FileSizeLimitExceededException, InvalidExtensionException {
    // long size = file.getSize();
    // if (size > DEFAULT_MAX_SIZE) {
    // throw new FileSizeLimitExceededException(DEFAULT_MAX_SIZE / 1024 / 1024);
    // }
    //
    // String fileName = file.getOriginalFilename();
    // String extension = FileTypeUtils.getExtension(file);
    // if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension)) {
    // if (allowedExtension == MimeTypeUtils.IMAGE_EXTENSION) {
    // throw new InvalidExtensionException.InvalidImageExtensionException(allowedExtension, extension,
    // fileName);
    // } else if (allowedExtension == MimeTypeUtils.FLASH_EXTENSION) {
    // throw new InvalidExtensionException.InvalidFlashExtensionException(allowedExtension, extension,
    // fileName);
    // } else if (allowedExtension == MimeTypeUtils.MEDIA_EXTENSION) {
    // throw new InvalidExtensionException.InvalidMediaExtensionException(allowedExtension, extension,
    // fileName);
    // } else if (allowedExtension == MimeTypeUtils.VIDEO_EXTENSION) {
    // throw new InvalidExtensionException.InvalidVideoExtensionException(allowedExtension, extension,
    // fileName);
    // } else {
    // throw new InvalidExtensionException(allowedExtension, extension, fileName);
    // }
    // }
    //
    // }

    /// **
    // * 判断MIME类型是否是允许的MIME类型
    // */
    // public static boolean isAllowedExtension(String extension, String[] allowedExtension) {
    // for (String str : allowedExtension) {
    // if (str.equalsIgnoreCase(extension)) {
    // return true;
    // }
    // }
    // return false;
    // }
}
