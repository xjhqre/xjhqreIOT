package com.xjhqre.iot.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.iot.domain.entity.Scene;
import com.xjhqre.iot.mapper.SceneMapper;
import com.xjhqre.iot.service.SceneService;

/**
 * 场景联动Service业务层处理
 * 
 * @author xjhqre
 * @date 2022-01-13
 */
@Service
public class SceneServiceImpl implements SceneService {

    @Resource
    private SceneMapper sceneMapper;

    @Override
    public IPage<Scene> find(Scene scene, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Scene> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(scene.getSceneId() != null, Scene::getSceneId, scene.getSceneId())
            .like(scene.getSceneName() != null && !"".equals(scene.getSceneName()), Scene::getSceneName,
                scene.getSceneName())
            .eq(scene.getUserId() != null, Scene::getUserId, scene.getUserId())
            .like(scene.getUserName() != null && !"".equals(scene.getUserName()), Scene::getUserName,
                scene.getUserName());
        return this.sceneMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 获取场景联动详情
     *
     */
    @Override
    public Scene getDetail(Long sceneId) {
        return this.sceneMapper.selectById(sceneId);
    }

    /**
     * 新增场景联动
     *
     */
    @Override
    public void add(Scene scene) {
        scene.setCreateTime(DateUtils.getNowDate());
        scene.setCreateBy(SecurityUtils.getUsername());
        this.sceneMapper.insert(scene);
    }

    /**
     * 修改场景联动
     *
     */
    @Override
    public void update(Scene scene) {
        scene.setUpdateTime(DateUtils.getNowDate());
        scene.setUpdateBy(SecurityUtils.getUsername());
        this.sceneMapper.updateById(scene);
    }

    /**
     * 批量删除场景联动
     *
     */
    @Override
    public void remove(List<Long> sceneIdList) {
        this.sceneMapper.deleteBatchIds(sceneIdList);
    }
}
