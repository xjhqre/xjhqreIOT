package com.xjhqre.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.common.domain.entity.Picture;

/**
 * <p>
 * PictureMapper
 * </p>
 *
 * @author xjhqre
 * @since 10月 11, 2022
 */
@Mapper
public interface PictureMapper extends BaseMapper<Picture> {

    /**
     * 批量修改
     * 
     * @param pictures
     */
    void updateBatchByIds(@Param("list") List<Picture> pictures);
}
