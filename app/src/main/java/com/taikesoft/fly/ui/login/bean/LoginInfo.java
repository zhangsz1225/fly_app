package com.taikesoft.fly.ui.login.bean;

/**
 * 登录接收类
 */

public class LoginInfo {
    /**
     * 账号id
     */
    public String id;
    /**
     * 员工id
     */
    public String employeeId;

    /**
     * 登录账号
     */
    public String userName;
    /**
     * 真实姓名
     */
    public String realName;
    /**
     * 手机号
     */
    public String phone;
    /**
     * 所在部门
     */
    public String dept;
    public String password;
    public String token;
    public Long expireAt;
    /**
     * 登录员工头像地址
     */
    public String img;
    public String loginTime;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public Long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Long expireAt) {
        this.expireAt = expireAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }
}