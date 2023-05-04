package com.xjhqre.iot.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.Product;
import com.xjhqre.iot.domain.entity.Scene;
import com.xjhqre.iot.domain.entity.SceneAction;
import com.xjhqre.iot.domain.entity.SceneTrigger;
import com.xjhqre.iot.domain.entity.ThingsModel;
import com.xjhqre.iot.mapper.SceneMapper;
import com.xjhqre.iot.service.DeviceService;
import com.xjhqre.iot.service.ProductService;
import com.xjhqre.iot.service.SceneActionService;
import com.xjhqre.iot.service.SceneService;
import com.xjhqre.iot.service.SceneTriggerService;
import com.xjhqre.iot.service.ThingsModelService;

/**
 * 场景联动Service业务层处理
 * 
 * @author xjhqre
 * @date 2022-01-13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SceneServiceImpl extends ServiceImpl<SceneMapper, Scene> implements SceneService {

    @Resource
    private SceneMapper sceneMapper;
    @Resource
    private SceneTriggerService sceneTriggerService;
    @Resource
    private SceneActionService sceneActionService;
    @Resource
    private ThingsModelService thingsModelService;
    @Resource
    private DeviceService deviceService;
    @Resource
    private ProductService productService;

    @Override
    public IPage<Scene> find(Scene scene, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Scene> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(scene.getSceneId() != null, Scene::getSceneId, scene.getSceneId())
            .like(scene.getSceneName() != null && !"".equals(scene.getSceneName()), Scene::getSceneName,
                scene.getSceneName())
            .eq(scene.getUserId() != null, Scene::getUserId, scene.getUserId())
            .like(scene.getUserName() != null && !"".equals(scene.getUserName()), Scene::getUserName,
                scene.getUserName())
                .orderByDesc(Scene::getCreateTime);
        return this.sceneMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 获取场景联动详情
     *
     */
    @Override
    public Scene getDetail(Long sceneId) {
        Scene scene = this.sceneMapper.selectById(sceneId);
        List<SceneTrigger> triggers = this.sceneTriggerService.listBySceneId(sceneId);
        for (SceneTrigger trigger : triggers) {
            ThingsModel thingsModel = this.thingsModelService.getById(trigger.getModelId());
            List<ThingsModel> thingsModelList =
                this.thingsModelService.listThingModelByProductId(thingsModel.getProductId(), 1);
            trigger.setDeviceThingModel(thingsModelList);
        }
        scene.setTriggers(triggers);
        List<SceneAction> actions = this.sceneActionService.listBySceneId(sceneId);
        for (SceneAction action : actions) {
            ThingsModel thingsModel = this.thingsModelService.getById(action.getModelId());
            List<ThingsModel> thingsModelList =
                this.thingsModelService.listThingModelByProductId(thingsModel.getProductId(), 2);
            action.setServiceModel(thingsModel);
            action.setDeviceThingModel(thingsModelList);
            action.setType(thingsModel.getInputParam().getType());
            action.setEnumList(thingsModel.getInputParam().getSpecs().getEnumList());
            action.setTrueText(thingsModel.getInputParam().getSpecs().getTrueText());
            action.setFalseText(thingsModel.getInputParam().getSpecs().getFalseText());
        }
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
            ThingsModel thingsModel = this.thingsModelService.getById(trigger.getModelId());
            trigger.setModelName(thingsModel.getModelName());
            trigger.setSceneId(scene.getSceneId());
            trigger.setSceneName(scene.getSceneName());
            trigger.setCreateBy(SecurityUtils.getUsername());
            trigger.setCreateTime(DateUtils.getNowDate());
        }
        this.sceneTriggerService.saveBatch(scene.getTriggers());
        // 动作
        for (SceneAction action : scene.getActions()) {
            Device device = this.deviceService.getById(action.getDeviceId());
            action.setDeviceNumber(device.getDeviceNumber());
            Product product = this.productService.getById(device.getProductId());
            action.setProductKey(product.getProductKey());
            action.setSceneId(scene.getSceneId());
            action.setSceneName(scene.getSceneName());
            ThingsModel thingsModel = this.thingsModelService.getById(action.getModelId());
            action.setIdentifier(thingsModel.getIdentifier());
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
            ThingsModel thingsModel = this.thingsModelService.getById(trigger.getModelId());
            trigger.setModelName(thingsModel.getModelName());
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
            Device device = this.deviceService.getById(action.getDeviceId());
            action.setDeviceNumber(device.getDeviceNumber());
            Product product = this.productService.getById(device.getProductId());
            action.setProductKey(product.getProductKey());
            action.setSceneId(scene.getSceneId());
            action.setSceneName(scene.getSceneName());
            ThingsModel thingsModel = this.thingsModelService.getById(action.getModelId());
            action.setIdentifier(thingsModel.getIdentifier());
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
