package com.samlet.generic;

import org.junit.Test;

public class ValueHelperTest {
    @Test
    public void testValues(){
        // 2014-10-22
        java.sql.Date date=java.sql.Date.valueOf("2014-10-22");
        System.out.println(date);
    }
}
