package com.yzy.map3d.util;

public class StringUtils {

    /**
     * 判断字符串是否为空
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim())
                && !"null".equalsIgnoreCase(value.trim()) && !"".equals(value.trim())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断多个字符是否全相等 ，任何一个为空返回false
     *
     * @param agrs
     * @return
     */
    public static boolean isEquals(String... agrs) {
        String last = null;
        for (int i = 0; i < agrs.length; i++) {
            String str = agrs[i];
            if (isEmpty(str)) {
                return false;
            }
            if (last != null && !str.equalsIgnoreCase(last)) {
                return false;
            }
            last = str;
        }
        return true;
    }
}
