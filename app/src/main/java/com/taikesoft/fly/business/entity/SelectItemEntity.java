package com.taikesoft.fly.business.entity;

import java.io.Serializable;

/**
 *护理记录查询所选择的护理类别
 */

public class SelectItemEntity implements Serializable{
    private String id;
    private String nurseItem;
    private String nurseType;
    private String nurseTime;
    private boolean isSelect = false;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNurseItem() {
        return nurseItem;
    }

    public void setNurseItem(String nurseItem) {
        this.nurseItem = nurseItem;
    }

    public String getNurseType() {
        return nurseType;
    }

    public void setNurseType(String nurseType) {
        this.nurseType = nurseType;
    }

    public String getNurseTime() {
        return nurseTime;
    }

    public void setNurseTime(String nurseTime) {
        this.nurseTime = nurseTime;
    }
}
