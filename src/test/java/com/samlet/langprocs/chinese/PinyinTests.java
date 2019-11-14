package com.samlet.langprocs.chinese;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.py.Pinyin;
import org.junit.Test;

import java.util.List;

public class PinyinTests {
    @Test
    public void testPinyin(){
        // List<Pinyin> pinyinList = HanLP.convertToPinyinList(text);
        System.out.println(HanLP.convertToPinyinString("截至2012年，",
                " ", false));
    }
}
