package com.samlet.langprocs.parsers;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

public class AmountConverter {

    //存放数量级中文数字信息 {十，百。。。亿。。。}
    private final static Map<String,Long> magnitudeMap = getMagnitudeMap();
    //存放0~9基本中文数字信息, {一，二。。。九，零}
    private final static Map<String,Long> dataMap = getDataMap();

    /**
     * 从后往前遍历字符串的方式将中文数字转换为阿拉伯数字
     * @param inputStr 源字符串
     * @return 转换后的阿拉伯数字
     * @throws Exception 如果字符串中有不能识别的（不在dataMap和operatorMap）字符，抛出异常
     */
    public long convertToLongFromEnd(String inputStr) {
        //存储遇到该数字前的最大一个数量值，这个值是累乘之前所有数量级，
        //比如二百万，到二的时候最高数量级就是100*10000
        long currentMaxLevel = 1l;
        //存储之前一次执行过乘操作的数量级
        long previousOpeMagnitude = 1l;
        //存储当前字符所对应的数量级
        long currentMagnitude = 1l;
        //存储当前所有字符仲最大的单个字符的数量级，
        long maxMagnitude = 1l;

        long sumVal = 0l;

        int len = inputStr.length();
        //倒序循环整个字符串，从最低位开始计算整个数值
        for(int i=len-1;i>=0;i--){
            String currentTxt = String.valueOf(inputStr.charAt(i));

            //如果当前值是数量级
            if(magnitudeMap.containsKey(currentTxt)){
                currentMagnitude = magnitudeMap.get(currentTxt);
                //如果第一位是一个数量级（比如十二）, 将当前值相加
                if(i == 0){
                    sumVal = sumVal + currentMagnitude;
                    return  sumVal;
                }
                //比较当前数量级与当前最大数量值，如果大于当前最大值，将当然最大数量值更新为当前数量级
                if(currentMagnitude > currentMaxLevel){
                    currentMaxLevel = currentMagnitude;
                }else{
                    if(currentMagnitude < maxMagnitude && currentMagnitude > previousOpeMagnitude){
                        //如果当前数量级小于当前最大数量级并且大于之前的数量级,比如二十五万五百亿，抵达"万"的时候因为之前的百
                        //已经与亿相乘，所以应该除以之前的百才能得到当前真正的最大数量值
                        currentMaxLevel = currentMaxLevel*currentMagnitude/previousOpeMagnitude;
                    }else{
                        currentMaxLevel = currentMaxLevel*currentMagnitude;
                    }
                    previousOpeMagnitude = currentMagnitude;
                }

                //将当前最大单数量级更新为当前数量级
                if(currentMagnitude > maxMagnitude){
                    maxMagnitude = currentMagnitude;
                }
            }else if(dataMap.containsKey(currentTxt)){
                //如果是0~9之间的数字，与前面一位数量级相乘，并累加到当前sumVal
                long data = dataMap.get(currentTxt);
                if(data == 0){
                    //跳过0
                    continue;
                }else{
                    sumVal = sumVal + data*currentMaxLevel;
                }
            }else{
                throw new NumberFormatException("Find illegal character in the input string: "+currentTxt);
            }
        }
        return sumVal;
    }

    /**
     * 数量级map，存储对应的数量级文字和对应的阿拉伯数字量值
     * @return The magnitude map
     */
    private static Map<String,Long> getMagnitudeMap(){
        Map<String,Long> magnitudeMap = Maps.newHashMap();
        magnitudeMap.put("十", 10l);
        magnitudeMap.put("百", 100l);
        magnitudeMap.put("千", 1000l);
        magnitudeMap.put("万", 10000l);
        magnitudeMap.put("亿", 100000000l);
        magnitudeMap.put("兆", 1000000000000l);
        magnitudeMap.put("京", 10000000000000000l);

        magnitudeMap.put("拾", 10l);
        magnitudeMap.put("佰", 100l);
        magnitudeMap.put("仟", 1000l);
        magnitudeMap.put("萬", 10000l);
        magnitudeMap.put("億", 100000000l);
        return magnitudeMap;
    }

    /**
     * 基本数据map，存储对应的基本数据及对应的阿拉伯数字量值
     * @return
     */
    private static Map<String,Long> getDataMap(){
        Map<String,Long> dataMap = Maps.newHashMap();
        dataMap.put("一",1l);
        dataMap.put("二",2l);
        dataMap.put("三",3l);
        dataMap.put("四",4l);
        dataMap.put("五",5l);
        dataMap.put("六",6l);
        dataMap.put("七",7l);
        dataMap.put("八",8l);
        dataMap.put("九",9l);
        dataMap.put("零",0l);

        dataMap.put("壹",1l);
        dataMap.put("贰",2l);
        dataMap.put("叁",3l);
        dataMap.put("肆",4l);
        dataMap.put("伍",5l);
        dataMap.put("陆",6l);
        dataMap.put("柒",7l);
        dataMap.put("捌",8l);
        dataMap.put("玖",9l);
        dataMap.put("〇",0l);
        return dataMap;
    }

}
