package com.xjhqre.system.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjhqre.common.constant.Constants;
import com.xjhqre.common.domain.entity.DictData;
import com.xjhqre.common.domain.entity.DictType;
import com.xjhqre.common.exception.ServiceException;
import com.xjhqre.common.utils.DictUtils;
import com.xjhqre.common.utils.StringUtils;
import com.xjhqre.system.mapper.DictDataMapper;
import com.xjhqre.system.mapper.DictTypeMapper;
import com.xjhqre.system.service.DictTypeService;

/**
 * 字典 业务层处理
 * 
 * @author xjhqre
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DictTypeServiceImpl implements DictTypeService {
    @Autowired
    private DictTypeMapper dictTypeMapper;

    @Autowired
    private DictDataMapper dictDataMapper;

    /**
     * 项目启动时，初始化字典到缓存
     */
    @PostConstruct
    public void init() {
        this.loadingDictCache();
    }

    /**
     * 根据条件分页查询字典类型
     * 
     * @param dictType
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public IPage<DictType> listDictType(DictType dictType, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dictType.getDictId() != null, DictType::getDictId, dictType.getDictId())
            .eq(dictType.getDictType() != null, DictType::getDictType, dictType.getDictType())
            .eq(dictType.getDictName() != null, DictType::getDictName, dictType.getDictName());
        return this.dictTypeMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 根据所有字典类型
     * 
     * @return 字典类型集合信息
     */
    @Override
    public List<DictType> selectDictTypeAll() {
        return this.dictTypeMapper.selectList(null);
    }

    /**
     * 根据字典类型查询字典数据
     * 
     * @param dictType
     *            字典类型
     * @return 字典数据集合信息
     */
    @Override
    public List<DictData> selectDictDataByType(String dictType) {
        // 查缓存
        List<DictData> dictDatas = DictUtils.getDictCache(dictType);
        if (StringUtils.isNotEmpty(dictDatas)) {
            return dictDatas;
        }
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dictType);
        dictDatas = this.dictDataMapper.selectList(wrapper);
        if (StringUtils.isNotEmpty(dictDatas)) {
            DictUtils.setDictCache(dictType, dictDatas);
            return dictDatas;
        }
        return null;
    }

    /**
     * 根据字典类型ID查询信息
     * 
     * @param dictId
     *            字典类型ID
     * @return 字典类型
     */
    @Override
    public DictType selectDictTypeById(Long dictId) {
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictType::getDictId, dictId).last("limit 1");
        return this.dictTypeMapper.selectOne(wrapper);
    }

    /**
     * 根据字典类型查询信息
     * 
     * @param dictType
     *            字典类型
     * @return 字典类型
     */
    @Override
    public DictType selectDictTypeByType(String dictType) {
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictType::getDictType, dictType).last("limit 1");
        return this.dictTypeMapper.selectOne(wrapper);
    }

    /**
     * 批量删除字典类型信息
     * 
     * @param dictIds
     *            需要删除的字典ID
     */
    @Override
    public void deleteDictTypeByIds(Long[] dictIds) {
        for (Long dictId : dictIds) {
            DictType dictType = this.selectDictTypeById(dictId);
            if (this.dictDataMapper.countDictDataByType(dictType.getDictType()) > 0) {
                throw new ServiceException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
            }
            this.dictTypeMapper.deleteById(dictId);
            // 删除redis中的字典缓存
            DictUtils.removeDictCache(dictType.getDictType());
        }
    }

    /**
     * 加载字典缓存数据
     */
    @Override
    public void loadingDictCache() {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getStatus, "0");
        // 以dictType为键，List<DictData>为值
        Map<String, List<DictData>> dictDataMap =
            this.dictDataMapper.selectList(wrapper).stream().collect(Collectors.groupingBy(DictData::getDictType));
        // 对每个map里的键值对的值进行排序，并以 dictType --> dictDatas 的形式存入redis
        for (Map.Entry<String, List<DictData>> entry : dictDataMap.entrySet()) {
            DictUtils.setDictCache(entry.getKey(), entry.getValue().stream()
                .sorted(Comparator.comparing(DictData::getDictSort)).collect(Collectors.toList()));
        }
    }

    /**
     * 重置字典缓存数据
     */
    @Override
    public void resetDictCache() {
        // 清空字典缓存
        DictUtils.clearDictCache();
        // 加载字典缓存数据
        this.loadingDictCache();
    }

    /**
     * 新增保存字典类型信息
     * 
     * @param dict
     *            字典类型信息
     * @return 结果
     */
    @Override
    public void insertDictType(DictType dict) {
        this.dictTypeMapper.insert(dict);
        DictUtils.setDictCache(dict.getDictType(), null);
    }

    /**
     * 修改保存字典类型信息
     * 
     * @param dict
     *            字典类型信息
     * @return 结果
     */
    @Override
    public void updateDictType(DictType dict) {
        DictType oldDict = this.dictTypeMapper.selectById(dict.getDictId());
        this.dictDataMapper.updateDictDataType(oldDict.getDictType(), dict.getDictType());
        this.dictTypeMapper.updateById(dict);
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dict.getDictType());
        List<DictData> dictDatas = this.dictDataMapper.selectList(wrapper);
        DictUtils.setDictCache(dict.getDictType(), dictDatas);
    }

    /**
     * 校验字典类型称是否唯一
     * 
     * @param dict
     *            字典类型
     * @return 结果
     */
    @Override
    public Boolean checkDictTypeUnique(DictType dict) {
        Long dictId = StringUtils.isNull(dict.getDictId()) ? -1L : dict.getDictId();
        DictType dictType = this.dictTypeMapper.checkDictTypeUnique(dict.getDictType());
        if (StringUtils.isNotNull(dictType) && dictType.getDictId().longValue() != dictId.longValue()) {
            return Constants.NOT_UNIQUE;
        }
        return Constants.UNIQUE;
    }
}
