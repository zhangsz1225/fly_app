package com.taikesoft.fly.ui.nurse.entity;

import com.taikesoft.fly.business.widget.children.CN;

import java.io.Serializable;

/**
 * 选择药品时展示
 */

public class MedicineInfoEntity implements CN, Serializable {
    private String parentId;
    private String name;
    private String facePhoto;
    private String backgroundColor;
    private String id;
    private String sortLetters;  //显示数据拼音的首字母

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String chineseText() {
        return name;
    }

    @Override
    public String chineseContent() {
        return name;
    }
}
