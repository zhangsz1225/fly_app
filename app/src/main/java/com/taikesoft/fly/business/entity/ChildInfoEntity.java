package com.taikesoft.fly.business.entity;

import com.taikesoft.fly.business.widget.children.CN;

import java.io.Serializable;

/**
 * 选择儿童时展示
 */

public class ChildInfoEntity implements CN, Serializable {
    private String name;
    private String childNumber;
    private String yearNumber;
    /*
    * 护理组
    */
    private String nurseGroup;
    /*
     * 项目组
     */
    private String projectGroup;
    /*
     * 护理室
     */
    private String nursingRoom;
    private String facePhoto;
    private String backgroundColor;
    private String id;
    private String taskId;
    private String idNumber;
    private String gender;
    private String sortLetters;  //显示数据拼音的首字母

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChildNumber() {
        return childNumber;
    }

    public void setChildNumber(String childNumber) {
        this.childNumber = childNumber;
    }

    public String getYearNumber() {
        return yearNumber;
    }

    public void setYearNumber(String yearNumber) {
        this.yearNumber = yearNumber;
    }

    public String getNurseGroup() {
        return nurseGroup;
    }

    public String getProjectGroup() {
        return projectGroup;
    }

    public void setProjectGroup(String projectGroup) {
        this.projectGroup = projectGroup;
    }

    public void setNurseGroup(String nurseGroup) {
        this.nurseGroup = nurseGroup;
    }

    public String getFacePhoto() {
        return facePhoto;
    }

    public void setFacePhoto(String facePhoto) {
        this.facePhoto = facePhoto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getNursingRoom() {
        return nursingRoom;
    }

    public void setNursingRoom(String nursingRoom) {
        this.nursingRoom = nursingRoom;
    }

    @Override
    public String chineseText() {
        return name;
    }

    @Override
    public String chineseContent() {
        return nurseGroup;
    }
}
