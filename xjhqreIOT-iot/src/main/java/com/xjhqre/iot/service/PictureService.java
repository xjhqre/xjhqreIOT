package com.xjhqre.iot.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.common.domain.dto.PictureDTO;
import com.xjhqre.common.domain.entity.Picture;

/**
 * 图片 业务层
 *
 * @author xjhqre
 */
public interface PictureService extends IService<Picture> {

    /**
     * 分页查询图片列表
     *
     * @param picture
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<Picture> find(Picture picture, Integer pageNum, Integer pageSize);

    /**
     * 保存图片
     *
     * @param pictureDTO
     * @param mFile
     */
    Map<String, String> add(PictureDTO pictureDTO, MultipartFile mFile);

    // void audit(String pictureId, Integer result);
    //
    /// **
    // * 批量审核
    // *
    // * @param pictureIds
    // * @param result
    // */
    // void batchAudit(String[] pictureIds, Integer result);
}
