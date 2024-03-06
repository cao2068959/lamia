package com.chy.lamia.utils;

public class StringUtils {

    /**
     * 将一个字符串转换为驼峰命名
     * @param data
     * @return
     */
    public static String toCamelCase(String data) {
        if (data == null || data.length() < 1) {
            return null;
        }

        char[] chars = data.toCharArray();
        chars[0] = toLow(chars[0]);
        return new String(chars);
    }

    public static char toLow(char c) {
        if (c >= 'A' && c <= 'Z') {
            c += 32;
        }
        return c;
    }

}
