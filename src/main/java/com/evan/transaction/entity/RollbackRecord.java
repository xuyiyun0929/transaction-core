package com.evan.transaction.entity;

import java.util.ArrayList;
import java.util.List;

public class RollbackRecord {
    private String tableName;
    private String dynamicSQL;
    private List<Object[]> paramList;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDynamicSQL() {
        return dynamicSQL;
    }

    public void setDynamicSQL(String dynamicSQL) {
        this.dynamicSQL = dynamicSQL;
    }

    public List<Object[]> getParamList() {
        return paramList;
    }

    public void setParamList(List<Object[]> paramList) {
        this.paramList = paramList;
    }
}
