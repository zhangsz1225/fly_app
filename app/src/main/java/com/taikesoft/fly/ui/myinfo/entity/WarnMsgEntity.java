package com.taikesoft.fly.ui.myinfo.entity;

import java.io.Serializable;

/**
 * 消息
 */

public class WarnMsgEntity implements Serializable {
    public String msgId;
    public String warnType;
    public String warnTitle;
    public String warnContent;
    public String warnDate;
    public String warnPart;
    public int isRead;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getWarnPart() {
        return warnPart;
    }

    public void setWarnPart(String warnPart) {
        this.warnPart = warnPart;
    }

    public String getWarnType() {
        return warnType;
    }

    public void setWarnType(String warnType) {
        this.warnType = warnType;
    }

    public String getWarnTitle() {
        return warnTitle;
    }

    public void setWarnTitle(String warnTitle) {
        this.warnTitle = warnTitle;
    }

    public String getWarnContent() {
        return warnContent;
    }

    public void setWarnContent(String warnContent) {
        this.warnContent = warnContent;
    }

    public String getWarnDate() {
        return warnDate;
    }

    public void setWarnDate(String warnDate) {
        this.warnDate = warnDate;
    }
}
