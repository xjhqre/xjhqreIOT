package com.xjhqre.iot.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.iot.domain.entity.Scene;
import com.xjhqre.iot.domain.entity.SceneAction;
import com.xjhqre.iot.domain.entity.SceneTrigger;
import com.xjhqre.iot.mapper.SceneMapper;
import com.xjhqre.iot.service.SceneActionService;
import com.xjhqre.iot.service.SceneService;
import com.xjhqre.iot.service.SceneTriggerService;

/**
 * 场景联动Service业务层处理
 * 
 * @author xjhqre
 * @date 2022-01-13
 */
@Service
public class SceneServiceImpl extends ServiceImpl<SceneMapper, Scene> implements SceneService {

    @Resource
    private SceneMapper sceneMapper;
    @Resource
    private SceneTriggerService sceneTriggerService;
    @Resource
    private SceneActionService sceneActionService;

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
        Scene scene = this.sceneMapper.selectById(sceneId);
        LambdaQueryWrapper<SceneTrigger> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneTrigger::getSceneId, scene.getSceneId());
        List<SceneTrigger> triggers = this.sceneTriggerService.list(wrapper);
        scene.setTriggers(triggers);
        LambdaQueryWrapper<SceneAction> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(SceneAction::getSceneId, scene.getSceneId());
        List<SceneAction> actions = this.sceneActionService.list(wrapper1);
        scene.setActions(actions);
        return scene;
    }

    /**
     * 新增场景联动
     *
     */
    @Override
    public void add(Scene scene) {
        scene.setUserId(SecurityUtils.getUserId());
        scene.setUserName(SecurityUtils.getUsername());
        scene.setCreateTime(DateUtils.getNowDate());
        scene.setCreateBy(SecurityUtils.getUsername());
        this.sceneMapper.insert(scene);
        // 触发器
        for (SceneTrigger trigger : scene.getTriggers()) {
            // Long modelId = trigger.getModelId();
            // ThingsModel thingsModel = this.thingsModelService.getById(modelId);
            // trigger.setModelName(thingsModel.getModelName());
            trigger.setSceneId(scene.getSceneId());
            trigger.setSceneName(scene.getSceneName());
            trigger.setCreateBy(SecurityUtils.getUsername());
            trigger.setCreateTime(DateUtils.getNowDate());
        }
        this.sceneTriggerService.saveBatch(scene.getTriggers());
        // 动作
        for (SceneAction action : scene.getActions()) {
            action.setSceneId(scene.getSceneId());
            action.setSceneName(scene.getSceneName());
            action.setCreateBy(SecurityUtils.getUsername());
            action.setCreateTime(DateUtils.getNowDate());
        }
        this.sceneActionService.saveBatch(scene.getActions());
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
        // 触发器更新，先删后增
        LambdaQueryWrapper<SceneTrigger> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneTrigger::getSceneId, scene.getSceneId());
        this.sceneTriggerService.remove(wrapper);
        for (SceneTrigger trigger : scene.getTriggers()) {
            // Long modelId = trigger.getModelId();
            // ThingsModel thingsModel = this.thingsModelService.getById(modelId);
            // trigger.setModelName(thingsModel.getModelName());
            trigger.setSceneId(scene.getSceneId());
            trigger.setSceneName(scene.getSceneName());
            trigger.setCreateBy(SecurityUtils.getUsername());
            trigger.setCreateTime(DateUtils.getNowDate());
        }
        this.sceneTriggerService.saveBatch(scene.getTriggers());

        // 动作更新，先删后增
        LambdaQueryWrapper<SceneAction> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.eq(SceneAction::getSceneId, scene.getSceneId());
        this.sceneActionService.remove(wrapper2);
        for (SceneAction action : scene.getActions()) {
            action.setSceneId(scene.getSceneId());
            action.setSceneName(scene.getSceneName());
            action.setCreateBy(SecurityUtils.getUsername());
            action.setCreateTime(DateUtils.getNowDate());
        }
        this.sceneActionService.saveBatch(scene.getActions());
    }

    /**
     * 批量删除场景联动
     *
     */
    @Override
    public void delete(Long[] sceneIdList) {
        for (Long sceneId : sceneIdList) {
            // 先删除触发器和动作，最后删除场景
            LambdaQueryWrapper<SceneTrigger> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SceneTrigger::getSceneId, sceneId);
            this.sceneTriggerService.remove(wrapper);
            LambdaQueryWrapper<SceneAction> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(SceneAction::getSceneId, sceneId);
            this.sceneActionService.remove(wrapper1);
            this.sceneMapper.deleteById(sceneId);
        }
    }

    @Override
    public void changeStatus(Long sceneId, Integer status) {
        Scene scene = this.sceneMapper.selectById(sceneId);
        scene.setStatus(status);
        this.sceneMapper.updateById(scene);
    }
}
