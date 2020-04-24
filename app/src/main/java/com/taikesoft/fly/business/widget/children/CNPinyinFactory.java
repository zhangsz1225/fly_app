package com.taikesoft.fly.business.widget.children;

import android.os.Handler;
import android.text.TextUtils;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;
import java.util.List;

import androidx.collection.ArrayMap;

/**
 */

public final class CNPinyinFactory {

    static final ArrayMap<Character, String> SURNAMES = new ArrayMap<>();

    static {
        SURNAMES.put('仇', "QIU");
        SURNAMES.put('柏', "BO");
        SURNAMES.put('牟', "MU");
        SURNAMES.put('颉', "XIE");
        SURNAMES.put('解', "XIE");
        SURNAMES.put('尉', "YU");
        SURNAMES.put('奇', "JI");
        SURNAMES.put('单', "SHAN");
        SURNAMES.put('谌', "SHEN");
        SURNAMES.put('乐', "YUE");
        SURNAMES.put('召', "SHAO");
        SURNAMES.put('朴', "PIAO");
        SURNAMES.put('区', "OU");
        SURNAMES.put('查', "ZHA");
        SURNAMES.put('曾', "ZENG");
        SURNAMES.put('缪', "MIAO");
    }

    static final char DEF_CHAR = '#';

    /**
     * 转换拼音, 考虑在子线程中运行
     * @param tList
     * @param <T>
     * @return
     */
    public static <T extends CN> void createCNPinyinList(List<T> tList, int requestCode, Handler handler) {
        if (tList != null && !tList.isEmpty()) {
            ArrayList<CNPinyin<T>> cnPinyinArrayList = new ArrayList<>(tList.size());
            for (T t : tList) {
                CNPinyin<T> pinyin = createCNPinyin(t);
                if (pinyin != null) {
                    cnPinyinArrayList.add(pinyin);
                }
            }
            handler.obtainMessage(requestCode, cnPinyinArrayList).sendToTarget();
        } else {
            handler.obtainMessage(requestCode, null).sendToTarget();
        }
    }

    public static <T extends CN> CNPinyin<T> createCNPinyin(T t) {
        if (t == null || (t.chineseText() == null && t.chineseContent() == null)) return null;
        String chinese = "";
        if (t.chineseText() != null) {
            chinese = t.chineseText().trim();
        }
        String chinese1 = "";
        if (t.chineseContent() != null) {
            chinese1 = t.chineseContent().trim();
        }
        if (TextUtils.isEmpty(chinese) && TextUtils.isEmpty(chinese1)) return null;
        CNPinyin cnPinyin = new CNPinyin(t);
        if (!TextUtils.isEmpty(chinese)) {
            char[] chars = chinese.toCharArray();
            String[] charPinyins = new String[chars.length];
            StringBuilder stringBuilder = new StringBuilder();
            int pinyinsTotalLength = 0;
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                //将输入的字符串转换为拼音，并区分多音字
                String pinyin = charToPinyin(chars[i], i);
                charPinyins[i] = pinyin;
                if (pinyin.length() > 0) {
                    stringBuilder.append(pinyin.charAt(0));
                } else {
                    stringBuilder.append(c);
                }
                pinyinsTotalLength += pinyin.length();
            }
            cnPinyin.pinyinsText = charPinyins;
            cnPinyin.firstCharText = getFirstChar(charPinyins);
            cnPinyin.firstCharsText = stringBuilder.toString();
            cnPinyin.pinyinsTextTotalLength = pinyinsTotalLength;
        }
        if (!TextUtils.isEmpty(chinese1)) {
            char[] chars = chinese1.toCharArray();
            String[] charPinyins = new String[chars.length];
            StringBuilder stringBuilder = new StringBuilder();
            int pinyinsTotalLength = 0;
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                //将输入的字符串转换为拼音，并区分多音字
                String pinyin = charToPinyin(chars[i], i);
                charPinyins[i] = pinyin;
                if (pinyin.length() > 0) {
                    stringBuilder.append(pinyin.charAt(0));
                } else {
                    stringBuilder.append(c);
                }
                pinyinsTotalLength += pinyin.length();
            }
            cnPinyin.pinyinsContent = charPinyins;
            cnPinyin.firstCharContent = getFirstChar(charPinyins);
            cnPinyin.firstCharsContent = stringBuilder.toString();
            cnPinyin.pinyinsContentTotalLength = pinyinsTotalLength;
        }
        return cnPinyin;
    }

    /**
     *
     * @param c
     * @param index
     * @return
     */
    private static String charToPinyin(char c, int index) {
        if (index == 0) {
            String pinyin = SURNAMES.get(c);
            if (pinyin != null) {
                return pinyin;
            }
        }
        String pinyin =  Pinyin.toPinyin(c);
        if (pinyin == null) {
            pinyin = String.valueOf(c);
        }
        return pinyin;
    }

    /**
     * 拼音首个字母
     * @param pinyins
     * @return
     */
    private static char getFirstChar(String[] pinyins) {
        if (pinyins != null && pinyins.length > 0) {
            String firstPinying = pinyins[0];
            if (firstPinying.length() > 0) {
                return charToUpperCase(firstPinying.charAt(0));
            }
        }
        return DEF_CHAR;
    }

    /**
     * 字符转大写
     * @param c
     * @return
     */
    private static char charToUpperCase(char c) {
        if (c >= 'a' && c <= 'z') {
            c -= 32;
        }
        return c;
    }

}
