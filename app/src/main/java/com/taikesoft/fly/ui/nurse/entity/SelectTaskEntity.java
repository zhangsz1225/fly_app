package com.taikesoft.fly.ui.nurse.entity;

import java.io.Serializable;

/**
 * 所选择的护理项(含任务)
 */

public class SelectTaskEntity implements Serializable {
    private String id;
    private String itemId;
    private String nurseType;
    private String timeType;
    private String nurseItem;
    private String nurseTime;
    private String nurseBeginTime;
    private String nurseEndTime;
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

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getNurseType() {
        return nurseType;
    }

    public void setNurseType(String nurseType) {
        this.nurseType = nurseType;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public String getNurseItem() {
        return nurseItem;
    }

    public void setNurseItem(String nurseItem) {
        this.nurseItem = nurseItem;
    }

    public String getNurseTime() {
        return nurseTime;
    }

    public void setNurseTime(String nurseTime) {
        this.nurseTime = nurseTime;
    }

    public String getNurseBeginTime() {
        return nurseBeginTime;
    }

    public void setNurseBeginTime(String nurseBeginTime) {
        this.nurseBeginTime = nurseBeginTime;
    }

    public String getNurseEndTime() {
        return nurseEndTime;
    }

    public void setNurseEndTime(String nurseEndTime) {
        this.nurseEndTime = nurseEndTime;
    }
}
