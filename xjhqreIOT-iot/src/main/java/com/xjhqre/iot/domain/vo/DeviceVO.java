package com.xjhqre.iot.domain.vo;

import java.util.ArrayList;
import java.util.List;

import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.model.thingsModelItem.ArrayModel;
import com.xjhqre.iot.domain.model.thingsModelItem.BoolModel;
import com.xjhqre.iot.domain.model.thingsModelItem.DoubleModel;
import com.xjhqre.iot.domain.model.thingsModelItem.EnumModel;
import com.xjhqre.iot.domain.model.thingsModelItem.IntegerModel;
import com.xjhqre.iot.domain.model.thingsModelItem.ReadOnlyModelOutput;
import com.xjhqre.iot.domain.model.thingsModelItem.StringModel;

import lombok.Data;

/**
 * 设备VO展示对象
 * 
 * @author xjhqre
 * @date 2023-1-2
 */
@Data
public class DeviceVO extends Device {

    private static final long serialVersionUID = 1L;

    public DeviceVO() {
        this.stringList = new ArrayList<>();
        this.integerList = new ArrayList<>();
        this.doubleList = new ArrayList<>();
        this.enumList = new ArrayList<>();
        this.arrayList = new ArrayList<>();
        this.readOnlyList = new ArrayList<>();
        this.boolList = new ArrayList<>();
    }

    private List<StringModel> stringList;

    private List<IntegerModel> integerList;

    private List<DoubleModel> doubleList;

    private List<EnumModel> enumList;

    private List<ArrayModel> arrayList;

    private List<BoolModel> boolList;

    private List<ReadOnlyModelOutput> readOnlyList;
}
