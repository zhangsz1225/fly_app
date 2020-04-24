package com.taikesoft.fly.ui.homepage.entity;

/**
 * 儿童状况统计list
 */

public class ChildSituationStaticListEntity {
    /**
     * 统计日期
     */
    private String staticDate;
    /**
     * 儿童数量
     */
    private int childCount;

    public String getStaticDate() {
        return staticDate;
    }

    public void setStaticDate(String staticDate) {
        this.staticDate = staticDate;
    }

    public int getChildCount() {
        return childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }
}
