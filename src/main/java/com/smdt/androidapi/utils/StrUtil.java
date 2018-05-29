package com.smdt.androidapi.utils;

/**
 * Description: 有关于String的工具类
 * AUTHOR: Champion Dragon
 * created at 2018/5/22
 **/
public class StrUtil {
    /**
     * @param str       原字符串
     * @param indexStr  指定字符串
     * @param isInclude 是否包括指定字符串
     * @return 截取后的字符串
     */
    /*截取最后一部分字符串*/
    public static String getLastindexStr(String str, String indexStr, boolean isInclude) {
        String result = "";
        int start = str.lastIndexOf(indexStr);
        if (start == -1) {
            result = "未找到指定字符串";
        } else {
            if (isInclude) {
                result = str.substring(start);
            } else {
                result = str.substring(start + indexStr.length());
            }
        }
        return result;
    }

    /*截取开始一部分字符串*/
    public static String getFirstindexStr(String str, String firstStr, boolean isInclude) {
        String result = "";
        int start = str.indexOf(firstStr);
        if (start == -1) {
            result = "未找到指定字符串";
        } else {
            if (isInclude) {
                result = str.substring(start);
            } else {
                result = str.substring(start + firstStr.length());
            }
        }
        return result;
    }


    /**
     * @param string    原字符串
     * @param indexStr  指定字符串
     * @param isInclude 是否包括指定字符串
     * @param index     截取第几个
     * @return 截取后的字符串
     */
    /*截取指定分割第几部分字符串*/
    public static String getIndexStr(String string, String indexStr, int index, boolean isInclude) {
        String result = "";
        int end = 0, start = 0;
        for (int i = 0; i < index; i++) {
            start = string.indexOf(indexStr, end);
            end = start + 1;
            if (start == -1) {
                return "未找到指定字符串";
            }
            Logs.v(string.substring(start));
        }
        end = string.indexOf(indexStr, end);
        if (end == -1) {
            if (isInclude) {
                result = string.substring(start);
            } else {
                result = string.substring(start + indexStr.length());
            }
        } else {
            if (isInclude) {
                result = string.substring(start, end);
            } else {
                result = string.substring(start + indexStr.length(), end);
            }
        }
        return result;
    }

    public static String getIndexStr(String str, String indexStr, int index) {
        String result = "";
        String[] split = str.split(indexStr);
        result = split[index];
        return result;
    }
}
