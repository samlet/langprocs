package com.samlet.langprocs.parsers;

public class AmountFilter {
    public static String filter(String text){
        String result=text
                .replaceAll("〇〇〇〇", "万")
                .replaceAll("〇〇〇", "千")
                .replaceAll("〇〇", "百");
        return result;
    }

    /**
     * 全角空格为12288，半角空格为32
     * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
     *
     * 将字符串中的全角字符转为半角
     * @param src 要转换的包含全角的任意字符串
     * @return  转换之后的字符串
     */
    public static String full2Half(String src) {

        char[] c = src.toCharArray();
        for (int index = 0; index < c.length; index++) {
            if (c[index] == 12288) {// 全角空格
                c[index] = (char) 32;
            } else if (c[index] > 65280 && c[index] < 65375) {// 其他全角字符
                c[index] = (char) (c[index] - 65248);
            }
        }
        return String.valueOf(c);
    }

    /**
     * 全角空格为12288，半角空格为32
     * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
     *
     * 将字符串中的半角字符转为全角
     * @param src 要转换的包含半角的任意字符串
     * @return  转换之后的字符串
     */
    public static String halfToFull(String src) {
        char[] c = src.toCharArray();
        for (int index = 0; index < c.length; index++) {
            if (c[index] == 32) {// 半角空格
                c[index] = (char) 12288;
            } else if (c[index] > 32 && c[index] < 127) {// 其他半角字符
                c[index] = (char) (c[index] + 65248);
            }
        }
        return String.valueOf(c);
    }
}
