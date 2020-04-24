package com.taikesoft.fly.ui.myinfo.bean;

/**
 * 护理记录分页请求参数
 */

public class SelectBean {
    public int pageNo;
    public int pageSize;
    public SelectParams params;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public SelectParams getParams() {
        return params;
    }

    public void setParams(SelectParams params) {
        this.params = params;
    }
}
