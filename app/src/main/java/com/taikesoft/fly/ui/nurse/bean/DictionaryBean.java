package com.taikesoft.fly.ui.nurse.bean;

import java.io.Serializable;

/**
 * 字典类
 */
public class DictionaryBean implements Serializable {
    public String parentId;

    public String name;

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

}
