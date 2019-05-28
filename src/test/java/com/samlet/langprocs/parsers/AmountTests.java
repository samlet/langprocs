package com.samlet.langprocs.parsers;

public class AmountTests {
    private static void parseAndPrint(String testString){
        System.out.println("The input value is: "+testString);
        long convertedValue = 0;
        try {
            ChineseNumberConverter converter = new ChineseNumberConverter();
            convertedValue = converter.convertToLongFromEnd(testString);
            System.out.println("The output value is\t"+Long.toString(convertedValue));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void main(String[] args) {
        String testString = "二十五万五百亿三千零八万一千零三十五";
        parseAndPrint(testString);
        parseAndPrint("一万零二百三十亿四千零七千八百九十");
        System.out.format("The expect value is\t%d", 1023000007890L);
    }
}
