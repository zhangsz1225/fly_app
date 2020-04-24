package com.taikesoft.fly.business.widget.children;

import android.os.Handler;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */

public final class CNPinyinIndexFactory {
    /**
     * 转换搜索拼音集合, 考虑在子线程中运行
     *
     * @param cnPinyinList
     * @param keyword
     * @return
     */
    public static <T extends CN> void indexList(List<CNPinyin<T>> cnPinyinList, String keyword, int requestCode, Handler handler) {
        ArrayList<CNPinyinIndex<T>> cnPinyinIndexArrayList = new ArrayList<>();
        if (cnPinyinList != null && cnPinyinList.size() != 0) {
            for (CNPinyin<T> cnPinyin : cnPinyinList) {
                CNPinyinIndex<T> index = index(cnPinyin, keyword);
                if (index != null) {
                    cnPinyinIndexArrayList.add(index);
                }
            }
        }
        handler.obtainMessage(requestCode, cnPinyinIndexArrayList).sendToTarget();
    }

    /**
     * 匹配拼音
     *
     * @param cnPinyin
     * @return null代表没有匹配
     */
    public static <T extends CN> CNPinyinIndex<T> index(CNPinyin<T> cnPinyin, String keyword) {
        if (TextUtils.isEmpty(keyword)) return null;
        //匹配中文搜索
        CNPinyinIndex cnPinyinIndex = matcherChinese(cnPinyin, keyword);
        if (isContainChinese(keyword)) {//包含中文只匹配原字符
            return cnPinyinIndex;
        }
        if (cnPinyinIndex == null) {
            //匹配首字母搜索
            cnPinyinIndex = matcherFirst(cnPinyin, keyword);
            if (cnPinyinIndex == null) {
                //匹配全拼音搜索
                cnPinyinIndex = matchersPinyins(cnPinyin, keyword);
            }
        }
        return cnPinyinIndex;
    }

    /**
     * 匹配中文
     *
     * @param cnPinyin
     * @param keyword
     * @return
     */
    static CNPinyinIndex matcherChinese(CNPinyin cnPinyin, String keyword) {
        if (cnPinyin.data.chineseText() != null) {
            if (keyword.length() <= cnPinyin.data.chineseText().length()) {
                Matcher matcher = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE).matcher(cnPinyin.data.chineseText());
                if (matcher.find()) {
                    if (cnPinyin.data.chineseContent() != null) {
                        if (keyword.length() <= cnPinyin.data.chineseContent().length()) {
                            Matcher matcher1 = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE).matcher(cnPinyin.data.chineseContent());
                            if (matcher1.find()) {
                                return new CNPinyinIndex(cnPinyin, matcher.start(), matcher.end(), matcher1.start(), matcher1.end());
                            }
                        }
                    }
                    return new CNPinyinIndex(cnPinyin, matcher.start(), matcher.end(), 0, 0);
                }
            }
        }
        if (cnPinyin.data.chineseContent() != null) {
            if (keyword.length() <= cnPinyin.data.chineseContent().length()) {
                Matcher matcher1 = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE).matcher(cnPinyin.data.chineseContent());
                if (matcher1.find()) {
                    return new CNPinyinIndex(cnPinyin, 0, 0, matcher1.start(), matcher1.end());
                }
            }
        }
        return null;
    }


    /**
     * 匹配首字母
     *
     * @param cnPinyin
     * @param keyword
     * @return
     */
    static CNPinyinIndex matcherFirst(CNPinyin cnPinyin, String keyword) {
        if (cnPinyin.pinyinsText != null) {
            if (keyword.length() <= cnPinyin.pinyinsText.length) {
                Matcher matcher = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE).matcher(cnPinyin.firstCharsText);
                if (matcher.find()) {
                    if (cnPinyin.pinyinsContent != null) {
                        if (keyword.length() <= cnPinyin.pinyinsContent.length) {
                            Matcher matcher1 = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE).matcher(cnPinyin.firstCharsContent);
                            if (matcher1.find()) {
                                return new CNPinyinIndex(cnPinyin, matcher.start(), matcher.end(), matcher1.start(), matcher1.end());
                            }
                        }
                    }
                    return new CNPinyinIndex(cnPinyin, matcher.start(), matcher.end(), 0, 0);
                }
            }
        }
        if (cnPinyin.pinyinsContent != null) {
            if (keyword.length() <= cnPinyin.pinyinsContent.length) {
                Matcher matcher1 = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE).matcher(cnPinyin.firstCharsContent);
                if (matcher1.find()) {
                    return new CNPinyinIndex(cnPinyin, 0, 0, matcher1.start(), matcher1.end());
                }
            }
        }
        return null;
    }

    /**
     * 所有拼音匹配
     * @param cnPinyin
     * @param keyword
     * @return
     */
    static CNPinyinIndex matchersPinyins(CNPinyin cnPinyin, String keyword) {
        if (keyword.length() <= cnPinyin.pinyinsTextTotalLength) {
            String startAndEnd = startAndEnd(cnPinyin.pinyinsText, keyword);
            String[] split = startAndEnd.split("\\.");
            int start = Integer.parseInt(split[0]);
            int end = Integer.parseInt(split[1]);
            if (start >= 0 && end >= start) {
                if (keyword.length() <= cnPinyin.pinyinsContentTotalLength) {
                    String start1AndEnd1 = startAndEnd(cnPinyin.pinyinsContent, keyword);
                    String[] split1 = start1AndEnd1.split("\\.");
                    int start1 = Integer.parseInt(split1[0]);
                    int end1 = Integer.parseInt(split1[1]);
                    if (start1 >= 0 && end1 >= start1) {
                        return new CNPinyinIndex(cnPinyin, start, end, start1, end1);
                    }
                }
                return new CNPinyinIndex(cnPinyin, start, end, 0, 0);
            }
        }
        if (keyword.length() <= cnPinyin.pinyinsContentTotalLength) {
            String start1AndEnd1 = startAndEnd(cnPinyin.pinyinsContent, keyword);
            String[] split1 = start1AndEnd1.split("\\.");
            int start1 = Integer.parseInt(split1[0]);
            int end1 = Integer.parseInt(split1[1]);
            if (start1 >= 0 && end1 >= start1) {
                return new CNPinyinIndex(cnPinyin, 0, 0, start1, end1);
            }
        }
        return null;
    }

    private static String startAndEnd(String[] pinyins, String keyword) {
        int start = -1;
        int end = -1;
        for (int i = 0; i < pinyins.length; i++) {
            String pat = pinyins[i];
            if (pat.length() >= keyword.length()) {//首个位置索引
                Matcher matcher = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE).matcher(pat);
                if (matcher.find() && matcher.start() == 0) {
                    start = i;
                    end = i + 1;
                    break;
                }
            } else {
                Matcher matcher = Pattern.compile(pat, Pattern.CASE_INSENSITIVE).matcher(keyword);
                if (matcher.find() && matcher.start() == 0) {//全拼匹配第一个必须在0位置
                    start = i;
                    String left = matcher.replaceFirst("");
                    end = end(pinyins, left, ++i);
                    break;
                }
            }
        }
        return start + "." + end;
    }

    /**
     * 根据匹配字符递归查找下一结束位置
     *
     * @param pinyinGroup
     * @param pattern
     * @param index
     * @return -1 匹配失败
     */
    private static int end(String[] pinyinGroup, String pattern, int index) {
        if (index < pinyinGroup.length) {
            String pinyin = pinyinGroup[index];
            if (pinyin.length() >= pattern.length()) {//首个位置索引
                Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(pinyin);
                if (matcher.find() && matcher.start() == 0) {
                    return index + 1;
                }
            } else {
                Matcher matcher = Pattern.compile(pinyin, Pattern.CASE_INSENSITIVE).matcher(pattern);
                if (matcher.find() && matcher.start() == 0) {//全拼匹配第一个必须在0位置
                    String left = matcher.replaceFirst("");
                    return end(pinyinGroup, left, index + 1);
                }
            }
        }
        return -1;
    }

    private static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.find();
    }

}
