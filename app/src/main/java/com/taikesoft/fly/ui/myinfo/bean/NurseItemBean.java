package com.taikesoft.fly.ui.myinfo.bean;

import java.io.Serializable;

/**
 * 护理项:查询记录时用(不含任务id)
 */

public class NurseItemBean implements Serializable {
    /**
     * 任务总数
     */
    private int cnt;
    /**
     * 已完成任务总数
     */
    private int finishCnt;
    /**
     * 已关闭
     */
    private int closesCnt;
    private String nurseItem;
    private String nurseBeginTime;
    private String nurseEndTime;
    private String nurseType;
    private String nurseTime;
    private String timeType;
    private String id;
    private int count;
    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public int getFinishCnt() {
        return finishCnt;
    }

    public void setFinishCnt(int finishCnt) {
        this.finishCnt = finishCnt;
    }

    public int getClosesCnt() {
        return closesCnt;
    }

    public void setClosesCnt(int closesCnt) {
        this.closesCnt = closesCnt;
    }

    public String getNurseItem() {
        return nurseItem;
    }

    public void setNurseItem(String nurseItem) {
        this.nurseItem = nurseItem;
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
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
