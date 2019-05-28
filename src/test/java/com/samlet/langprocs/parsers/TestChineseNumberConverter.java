package com.samlet.langprocs.parsers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for ChineseNumberConverter
 * @author Haibo Yu on 09/27/2017.
 */
public class TestChineseNumberConverter {

    private static ChineseNumberConverter converter = new ChineseNumberConverter();

    @Test
    public void testSimpleUsingFromEndStrategy() throws Exception{
        String testString = "十";
        long expectedOutput = 10l;
        long realOutput = converter.convertToLongFromEnd(testString);
        assertEquals(expectedOutput,realOutput);

        testString = "十五";
        expectedOutput = 15l;
        realOutput = converter.convertToLongFromEnd(testString);
        assertEquals(expectedOutput,realOutput);

        testString = "二十五";
        expectedOutput = 25l;
        realOutput = converter.convertToLongFromEnd(testString);
        assertEquals(expectedOutput,realOutput);

        testString = "一百零五";
        expectedOutput = 105l;
        realOutput = converter.convertToLongFromEnd(testString);
        assertEquals(expectedOutput,realOutput);

        testString = "八万一千零三十五";
        expectedOutput = 81035l;
        realOutput = converter.convertToLongFromEnd(testString);
        assertEquals(expectedOutput,realOutput);

        testString = "八万零五";
        expectedOutput = 80005l;
        realOutput = converter.convertToLongFromEnd(testString);
        assertEquals(expectedOutput,realOutput);

        testString = "八万零一十五";
        expectedOutput = 80015l;
        realOutput = converter.convertToLongFromEnd(testString);
        assertEquals(expectedOutput,realOutput);

        testString = "八万五百零五";
        expectedOutput = 80505l;
        realOutput = converter.convertToLongFromEnd(testString);
        assertEquals(expectedOutput,realOutput);

        testString = "二十五万五百";
        expectedOutput = 250500l;
        realOutput = converter.convertToLongFromEnd(testString);
        assertEquals(expectedOutput,realOutput);
    }

    @Test
    public void testComplicatedUsingFromEndStrategy() throws Exception{
        String testString = "二十五万五百亿三千零八万一千零三十五";
        long expectedOutput = 25050030081035l;
        long realOutput = converter.convertToLongFromEnd(testString);
        assertEquals(expectedOutput,realOutput);
    }
}
