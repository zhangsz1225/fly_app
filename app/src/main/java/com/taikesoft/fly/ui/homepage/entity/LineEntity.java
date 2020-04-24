package com.taikesoft.fly.ui.homepage.entity;

/**
 * 儿童状况线形图实体
 */

public class LineEntity {
    private String id;
    private String time;
    private int ill;
    private int inHospital;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIll() {
        return ill;
    }

    public void setIll(int ill) {
        this.ill = ill;
    }

    public int getInHospital() {
        return inHospital;
    }

    public void setInHospital(int inHospital) {
        this.inHospital = inHospital;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
