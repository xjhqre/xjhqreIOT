package com.xjhqre.system.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.domain.entity.DictData;

/**
 * 字典 业务层
 * 
 * @author xjhqre
 */
public interface DictDataService {

    /**
     * 根据条件分页查询字典数据
     * 
     * @param dictData
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<DictData> find(DictData dictData, Integer pageNum, Integer pageSize);

    /**
     * 查询字典条目列表
     *
     */
    List<DictData> list(DictData dictData);

    /**
     * 根据字典类型和字典键值查询字典数据信息
     * 
     * @param dictType
     *            字典类型
     * @param dictValue
     *            字典键值
     * @return 字典标签
     */
    // String selectDictLabel(String dictType, String dictValue);

    /**
     * 根据字典数据ID查询信息
     * 
     * @param dictCode
     *            字典数据ID
     * @return 字典数据
     */
    DictData getDetail(Long dictCode);

    /**
     * 批量删除字典数据信息
     * 
     * @param dictCodes
     *            需要删除的字典数据ID
     */
    void delete(List<Long> dictCodes);

    /**
     * 新增保存字典数据信息
     * 
     * @param dictData
     *            字典数据信息
     * @return 结果
     */
    void add(DictData dictData);

    /**
     * 修改保存字典数据信息
     * 
     * @param dictData
     *            字典数据信息
     * @return 结果
     */
    void update(DictData dictData);
}
