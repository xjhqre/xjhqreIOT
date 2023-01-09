package com.xjhqre.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.iot.domain.entity.Scene;

/**
 * SceneService
 * 
 * @author xjhqre
 * @since 2023-01-7
 */
public interface SceneService {

    /**
     * 分页查询场景联动列表
     */
    IPage<Scene> find(Scene scene, Integer pageNum, Integer pageSize);

    /**
     * 获取场景联动详情
     *
     */
    Scene getDetail(Long sceneId);

    /**
     * 新增场景联动
     *
     */
    void add(Scene scene);

    /**
     * 修改场景联动
     *
     */
    void update(Scene scene);

    /**
     * 批量删除场景联动
     *
     */
    void remove(List<Long> sceneIds);
}
