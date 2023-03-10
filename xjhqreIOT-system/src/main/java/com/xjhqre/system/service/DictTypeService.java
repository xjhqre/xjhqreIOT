package com.xjhqre.system.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.domain.entity.DictData;
import com.xjhqre.common.domain.entity.DictType;

/**
 * 字典 业务层
 * 
 * @author xjhqre
 */
public interface DictTypeService {
    /**
     * 根据条件分页查询字典类型
     * 
     * @param dictType
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<DictType> find(DictType dictType, Integer pageNum, Integer pageSize);

    /**
     * 查询字典列表
     */
    List<DictType> list(DictType dictType);

    /**
     * 根据所有字典类型
     * 
     * @return 字典类型集合信息
     */
    List<DictType> selectDictTypeAll();

    /**
     * 根据字典类型查询字典数据
     * 
     * @param dictType
     *            字典类型
     * @return 字典数据集合信息
     */
    List<DictData> getByDictType(String dictType);

    /**
     * 根据字典类型ID查询信息
     * 
     * @param dictId
     *            字典类型ID
     * @return 字典类型
     */
    DictType getDetail(Long dictId);

    /**
     * 根据字典类型查询信息
     * 
     * @param dictType
     *            字典类型
     * @return 字典类型
     */
    DictType selectDictTypeByType(String dictType);

    /**
     * 批量删除字典信息
     * 
     * @param dictIds
     *            需要删除的字典ID
     */
    void delete(List<Long> dictIds);

    /**
     * 加载字典缓存数据
     */
    void loadingDictCache();

    /**
     * 重置字典缓存数据
     */
    void resetDictCache();

    /**
     * 新增保存字典类型信息
     * 
     * @param dictType
     *            字典类型信息
     * @return 结果
     */
    void add(DictType dictType);

    /**
     * 修改保存字典类型信息
     * 
     * @param dictType
     *            字典类型信息
     * @return 结果
     */
    void update(DictType dictType);

    /**
     * 校验字典类型称是否唯一
     * 
     * @param dictType
     *            字典类型
     * @return 结果
     */
    Boolean checkDictTypeUnique(DictType dictType);
}
