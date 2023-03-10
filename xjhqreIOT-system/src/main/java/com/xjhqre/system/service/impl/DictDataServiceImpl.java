package com.xjhqre.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjhqre.common.domain.entity.DictData;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.DictUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.system.mapper.DictDataMapper;
import com.xjhqre.system.service.DictDataService;

/**
 * 字典 业务层处理
 * 
 * @author xjhqre
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DictDataServiceImpl implements DictDataService {
    @Autowired
    private DictDataMapper dictDataMapper;

    /**
     * 根据条件分页查询字典数据
     * 
     * @param dictData
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public IPage<DictData> find(DictData dictData, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dictData.getDictCode() != null, DictData::getDictCode, dictData.getDictCode())
            .eq(dictData.getDictLabel() != null && !"".equals(dictData.getDictLabel()), DictData::getDictLabel,
                dictData.getDictLabel())
            .like(dictData.getDictType() != null && !"".equals(dictData.getDictType()), DictData::getDictType,
                dictData.getDictType())
            .eq(dictData.getStatus() != null && !"".equals(dictData.getStatus()), DictData::getStatus,
                dictData.getStatus());
        return this.dictDataMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<DictData> list(DictData dictData) {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dictData.getDictCode() != null, DictData::getDictCode, dictData.getDictCode())
            .eq(dictData.getDictLabel() != null && !"".equals(dictData.getDictLabel()), DictData::getDictLabel,
                dictData.getDictLabel())
            .like(dictData.getDictType() != null && !"".equals(dictData.getDictType()), DictData::getDictType,
                dictData.getDictType())
            .eq(dictData.getStatus() != null && !"".equals(dictData.getStatus()), DictData::getStatus,
                dictData.getStatus());
        return this.dictDataMapper.selectList(wrapper);
    }

    /// **
    // * 根据字典类型和字典键值查询字典数据信息
    // *
    // * @param dictType
    // * 字典类型
    // * @param dictValue
    // * 字典键值
    // * @return 字典标签
    // */
    // @Override
    // public String selectDictLabel(String dictType, String dictValue) {
    // return this.dictDataMapper.selectDictLabel(dictType, dictValue);
    // }

    /**
     * 根据字典数据ID查询信息
     * 
     * @param dictCode
     *            字典数据ID
     * @return 字典数据
     */
    @Override
    public DictData getDetail(Long dictCode) {
        return this.dictDataMapper.selectById(dictCode);
    }

    /**
     * 新增保存字典数据信息
     *
     */
    @Override
    public void add(DictData dictData) {
        dictData.setCreateBy(SecurityUtils.getUsername());
        dictData.setCreateTime(DateUtils.getNowDate());
        this.dictDataMapper.insert(dictData);
        // 更新缓存
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dictData.getDictType());
        List<DictData> dictDataList = this.dictDataMapper.selectList(wrapper);
        DictUtils.setDictCache(dictData.getDictType(), dictDataList);
    }

    /**
     * 修改保存字典数据信息
     *
     */
    @Override
    public void update(DictData dictData) {
        dictData.setUpdateBy(SecurityUtils.getUsername());
        dictData.setCreateTime(DateUtils.getNowDate());
        this.dictDataMapper.updateById(dictData);
        // 更新缓存
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dictData.getDictType());
        List<DictData> dictDataList = this.dictDataMapper.selectList(wrapper);
        DictUtils.setDictCache(dictData.getDictType(), dictDataList);
    }

    /**
     * 批量删除字典数据信息
     *
     */
    @Override
    public void delete(List<Long> dictCodes) {
        for (Long dictCode : dictCodes) {
            // 根据 dictCode 删除对应字典数据
            DictData data = this.getDetail(dictCode);
            this.dictDataMapper.deleteById(dictCode);
            // 每次删除字典数据后查询对应的字典类型，更新缓存
            LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DictData::getDictType, data.getDictType());
            List<DictData> dictDatas = this.dictDataMapper.selectList(wrapper);
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
    }
}
