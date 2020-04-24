package com.taikesoft.fly.business.widget.children;

import java.io.Serializable;

/**
 */

public class CNPinyinIndex <T extends CN> implements Serializable {

    public final CNPinyin<T> cnPinyin;

    //对名字进行变色处理
    public final int startText;
    public final int endText;

    //此处是加了内容后对内容的处理
    public final int startContent;
    public final int endContent;

    CNPinyinIndex(CNPinyin cnPinyin, int startText, int endText, int startContent, int endContent) {
        this.cnPinyin = cnPinyin;
        this.startText = startText;
        this.endText = endText;
        this.startContent = startContent;
        this.endContent = endContent;
    }

    @Override
    public String toString() {
        return cnPinyin.toString()+"  start " + startText+"  end " + endText;
    }
}
