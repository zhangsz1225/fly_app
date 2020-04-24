package com.taikesoft.fly.business.widget.children;

import java.io.Serializable;

import static com.taikesoft.fly.business.widget.children.CNPinyinFactory.DEF_CHAR;

/**
 */

public class CNPinyin <T extends CN> implements Serializable, Comparable<CNPinyin<T>> {

    /**
     * 对应首字首拼音字母
     */
    char firstCharText;
    /**
     * 所有字符中的拼音首字母
     */
    String firstCharsText;
    /**
     * 对应的所有字母拼音
     */
    String[] pinyinsText;

    /**
     * 拼音总长度
     */
    int pinyinsTextTotalLength;


    //此处是加了内容后对内容的处理
    char firstCharContent;
    String firstCharsContent;
    String[] pinyinsContent;
    int pinyinsContentTotalLength;


    public final T data;

    CNPinyin(T data) {
        this.data = data;
    }

    public char getFirstCharText() {
        return firstCharText;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("--firstChar--").append(firstCharText).append("--pinyins:");
        for (String str : pinyinsText) {
            sb.append(str);
        }
        return sb.toString();
    }

    int compareValue() {
        if (firstCharText == DEF_CHAR) {
            return 'Z' + 1;
        }
        return firstCharText;
    }

    @Override
    public int compareTo(CNPinyin<T> tcnPinyin) {
        int compare = compareValue() - tcnPinyin.compareValue();
        if (compare == 0) {
            String chinese1 = data.chineseText();
            String chinese2 = tcnPinyin.data.chineseText();
            return chinese1.compareTo(chinese2);
        }
        return compare;
    }
}
