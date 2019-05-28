package com.samlet.langprocs.parsers;

import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.List;

import static com.samlet.langprocs.parsers.AmountFilter.full2Half;
import static org.junit.Assert.*;

public class AmountParserTest {

    @Test
    public void convertStringFromFullWidthToHalfWidth() {
        System.out.println(full2Half("９０１２３４５６７８只蚂蚁"));
        System.out.println(full2Half("2"));
        System.out.println(full2Half("9.0123"));
        System.out.println(Float.toString(Float.valueOf(full2Half("９０１２３４５６７８"))));
        System.out.println(Long.valueOf(full2Half("９０１２３４５６７８")));
        System.out.println(StringUtils.isNumeric(full2Half("９０１２３４５６７８")));
    }

    @Test
    public void parseAmount() {
        String[] testCase = new String[]
                {
                        "十九元套餐包括什么",
                        "九千九百九十九朵玫瑰",
                        "壹佰块都不给我",
                        "９０１２３４５６７８只蚂蚁",
                        "牛奶三〇〇克*2",
                        "ChinaJoy“扫黄”细则露胸超2厘米罚款",
                };
        AmountParser parser = new AmountParser();
        AmountConverter converter = new AmountConverter();
        for (String t : testCase) {
            List<Term> result = parser.parseAmount(t, false);
            System.out.println(result);
            for (Term term : result) {
                if (term.nature == Nature.m) {
                    System.out.print("\t" + term.word);

                    String word=term.word;
                    word=full2Half(word);
                    if(StringUtils.isNumeric(word)){
                        System.out.format("\t\t☈ %s\n", word);
                    }else {
                        word=AmountFilter.filter(term.word); // use the raw word
                        long longval = converter.convertToLongFromEnd(word);
                        System.out.format("\t\t☈ %d\n", longval);
                    }
                }
            }
        }
    }

    @Test
    public void getAmountValues() {
        String[] testCase = new String[]
                {
                        "十九元套餐包括什么",
                        "九千九百九十九朵玫瑰",
                        "壹佰块都不给我",
                        "９０１２３４５６７８只蚂蚁",
                        "牛奶三〇〇克*2",
                        "ChinaJoy“扫黄”细则露胸超2厘米罚款",
                };
        AmountParser parser = new AmountParser();
        for (String t : testCase) {
            List<AmountParser.AmountHolder> items=parser.getAmountValues(t);
            for (AmountParser.AmountHolder item:items){
                System.out.format("%s[%d] - %s\n", item.term.word, item.term.offset, item.numericVal);
            }
        }
    }
}