package com.taikesoft.fly.ui.child.bean;

import java.io.Serializable;

/**
 * 任务项
 */

public class NurseTaskBean implements Serializable {
    private String nurseItem;
    private String nurseType;
    private String timeType;
    private String nurseTime;
    private String nurseBeginTime;
    private String nurseEndTime;
    private String itemId;
    private String id;

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

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public String getNurseTime() {
        return nurseTime;
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

    public void setNurseTime(String nurseTime) {
        this.nurseTime = nurseTime;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
