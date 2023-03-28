package com.xjhqre.iot.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xjhqre.common.domain.R;
import com.xjhqre.common.exception.ServiceException;
import com.xjhqre.common.utils.file.FileTypeUtils;
import com.xjhqre.common.utils.uuid.IdUtils;
import com.xjhqre.framework.utils.OSSUtil;
import com.xjhqre.framework.utils.OSSUtil.FileDirType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * FileUploadController
 * </p>
 *
 * @author xjhqre
 * @since 3月 20, 2023
 */
@Api(value = "文件操作接口", tags = "文件操作接口")
@RestController
@RequestMapping("/iot/file")
public class FileUploadController {

    @ApiOperation(value = "上传文件")
    @PostMapping(value = "/add")
    public R<Map<String, String>> add(@RequestParam("file") MultipartFile mFile) {
        if (mFile == null) {
            throw new ServiceException("上传文件为空");
        }

        String extension = FileTypeUtils.getExtension(mFile.getOriginalFilename());

        if (!extension.equals(".bin")) {
            throw new ServiceException("上传文件格式不支持！");
        }

        // 生成文件编号（唯一）
        String number = IdUtils.simpleUUID();

        // 上传OSS
        String fileUrl = OSSUtil.upload(mFile, FileDirType.FIRMWARE, number + extension);

        Map<String, String> retmap = new HashMap<>();
        retmap.put("name", number + extension);
        retmap.put("originalName", mFile.getOriginalFilename());
        retmap.put("url", fileUrl);

        return R.success(retmap);
    }

}
