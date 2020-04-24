package com.taikesoft.fly.ui.homepage.entity;

/**
 *护理记录查询筛选类
 */

public class TaskSituationEntity {
    /**
     * 护理室
     */
    private String nurseGroup;
    /**
     * 护理日期
     */
    private String currentDay;
    /**
     * 总任务数
     */
    private int sum;
    /**
     * 已完成
     */
    private int finishedNum;
    /**
     * 未完成
     */
    private int notFinishedNum;
    /**
     * 已关闭
     */
    private int closedNum;

    public String getNurseGroup() {
        return nurseGroup;
    }

    public void setNurseGroup(String nurseGroup) {
        this.nurseGroup = nurseGroup;
    }

    public String getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(String currentDay) {
        this.currentDay = currentDay;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getFinishedNum() {
        return finishedNum;
    }

    public void setFinishedNum(int finishedNum) {
        this.finishedNum = finishedNum;
    }

    public int getNotFinishedNum() {
        return notFinishedNum;
    }

    public void setNotFinishedNum(int notFinishedNum) {
        this.notFinishedNum = notFinishedNum;
    }

    public int getClosedNum() {
        return closedNum;
    }

    public void setClosedNum(int closedNum) {
        this.closedNum = closedNum;
    }
}
