package com.taikesoft.fly.ui.myinfo.bean;

import java.io.Serializable;

/**
 * 护理记录类
 */

public class RecordBean implements Serializable {
    /**
     * 护理大类
     */
    private String nurseType;
    /**
     * 护理类型
     */
    private String nurseItem;
    /**
     * 备注
     */
    private String remark;
    /**
     * 护理内容串
     */
    private String nurseContent;
    /**
     * 衣服等业务内容
     */
    private String content;
    /**
     * 护理值
     */
    private String nurseValue;
    /**
     * 护理值单位
     */
    private String nurseValueUnit;
    /**
     * 儿童姓名
     */
    private String childName;
    /**
     * 护理记录的时间
     */
    private String operateTime;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 开始时间
     */
    private String nurseBeginTime;
    /**
     * 结束时间
     */
    private String nurseEndTime;
    /**
     * 护理项id
     */
    private String nurseItemId;
    /**
     * 护理记录id
     */
    private String id;
    /**
     * 护理员
     */
    private String nurses;
    /**
     * 精神状态
     */
    private String spirit;

    public String getNurseType() {
        return nurseType;
    }

    public void setNurseType(String nurseType) {
        this.nurseType = nurseType;
    }

    public String getNurseItem() {
        return nurseItem;
    }

    public void setNurseItem(String nurseItem) {
        this.nurseItem = nurseItem;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getNurseContent() {
        return nurseContent;
    }

    public void setNurseContent(String nurseContent) {
        this.nurseContent = nurseContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNurseValue() {
        return nurseValue;
    }

    public void setNurseValue(String nurseValue) {
        this.nurseValue = nurseValue;
    }

    public String getNurseValueUnit() {
        return nurseValueUnit;
    }

    public void setNurseValueUnit(String nurseValueUnit) {
        this.nurseValueUnit = nurseValueUnit;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
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

    public String getNurseItemId() {
        return nurseItemId;
    }

    public void setNurseItemId(String nurseItemId) {
        this.nurseItemId = nurseItemId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNurses() {
        return nurses;
    }

    public void setNurses(String nurses) {
        this.nurses = nurses;
    }

    public String getSpirit() {
        return spirit;
    }

    public void setSpirit(String spirit) {
        this.spirit = spirit;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
