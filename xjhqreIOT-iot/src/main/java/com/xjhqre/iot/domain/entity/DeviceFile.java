package com.xjhqre.iot.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import lombok.Data;

/**
 * <p>
 * 设备文件
 * </p>
 *
 * @author xjhqre
 * @since 4月 18, 2023
 */
@Data
@TableName("iot_device_file")
public class DeviceFile extends BaseEntity {

    private static final long serialVersionUID = -4583324790435062747L;

    /**
     * 文件id
     */
    @TableId(value = "file_id", type = IdType.AUTO)
    private Long fileId;

    /**
     * 设备id
     */
    private Long deviceId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件oss地址
     */
    private String fileUrl;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private Integer fileType;

}
