package com.taikesoft.fly.ui.myinfo.bean;

import java.io.Serializable;

/**
 * 任务
 */

public class NurseTaskBean implements Serializable {
    private String nurseItem;
    private String nurseType;
    private String nurseTime;
    private String executionCycle;
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

    public String getNurseTime() {
        return nurseTime;
    }

    public void setNurseTime(String nurseTime) {
        this.nurseTime = nurseTime;
    }

    public String getExecutionCycle() {
        return executionCycle;
    }

    public void setExecutionCycle(String executionCycle) {
        this.executionCycle = executionCycle;
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
