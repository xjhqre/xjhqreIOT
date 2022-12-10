//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xjhqre.common.translator;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjhqre.common.utils.BeanUtils;

import java.util.List;
import java.util.function.BiConsumer;

public class ComplexNameTranslator {

    public static ComplexNameTranslator instance() {
        return new ComplexNameTranslator();
    }

    /**
     * 翻译分页数据
     *
     * @param page
     * @param translator
     * @param <E>
     * @param <T>
     * @return
     */
    public <E, T> IPage<T> translate(IPage<E> page, BiConsumer<ComplexNameTranslator, T> translator) {
        IPage<T> ePage = new Page<>();
        BeanUtils.copyBeanProp(ePage, page);
        if (page != null && !CollectionUtils.isEmpty(page.getRecords())) {
            ePage.getRecords().forEach(d -> translator.accept(this, d));
        }
        return ePage;
    }

    /**
     * 翻译集合
     *
     * @param datas
     * @param translator
     * @param <T>
     * @return
     */
    public <T> List<T> translate(List<T> datas, BiConsumer<ComplexNameTranslator, T> translator) {
        if (!CollectionUtils.isEmpty(datas)) {
            datas.forEach(d -> translator.accept(this, d));
        }
        return datas;
    }

    ///**
    // * 翻译自定义类型
    // *
    // * @param data
    // * @param translator
    // * @param <T>
    // * @return
    // */
    //public <T> T translate(T data, BiConsumer<ComplexNameTranslator, T> translator) {
    //    if (data != null) {
    //        translator.accept(this, data);
    //    }
    //    return data;
    //}

}
