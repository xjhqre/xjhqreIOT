package com.xjhqre.iot.mybatisConfig;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.alibaba.fastjson2.JSON;
import com.xjhqre.iot.domain.model.DataType;

public class DataTypeTypeHandler extends BaseTypeHandler<DataType> {

    /**
     * @param ps
     *            SQL预编译对象
     * @param i
     *            需要赋值的索引位置(相当于在JDBC中对占位符的位置进行赋值)
     * @param parameter
     *            索引位置i需要赋的值(原本要给这个位置赋的值，在setNonNullParameter方法中主要解决的问题就是将这个自定义类型变成数据库认识的类型)
     * @param jdbcType
     *            jdbc的类型
     * @throws SQLException
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, DataType parameter, JdbcType jdbcType)
        throws SQLException {
        if (parameter == null) {
            ps.setString(i, null);
            return;
        }

        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public DataType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return JSON.parseObject(rs.getString(columnName), DataType.class);
    }

    @Override
    public DataType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return JSON.parseObject(rs.getString(columnIndex), DataType.class);
    }

    @Override
    public DataType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return JSON.parseObject(cs.getString(columnIndex), DataType.class);
    }
}
