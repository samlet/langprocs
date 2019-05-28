package com.samlet.langprocs.parsers;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.samlet.langprocs.util.NlpProtoUtil;
import com.samlet.nlpserv.NlAmount;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Singleton;
import java.util.List;
import java.util.Set;

import static com.samlet.langprocs.parsers.AmountFilter.full2Half;

@Singleton
public class AmountParser {
    public static class AmountHolder{
        public Term term;
        public String numericVal;

        public NlAmount proto(){
            return NlAmount.newBuilder()
                    .setEntity(NlpProtoUtil.convertTerm(term))
                    .setNumericVal(numericVal).build();
        }
    }

    Segment defaultSegment;
    Segment amountSegment;
    AmountConverter converter = new AmountConverter();

    public AmountParser(){
        this.defaultSegment=HanLP.newSegment().enableOffset(true);
        this.amountSegment=HanLP.newSegment().enableNumberQuantifierRecognize(true);
    }

    public List<Term> parseAmount(String text, boolean enableQuantifierRecognize){
        List<Term> termParts;
        List<Term> results= Lists.newArrayList();
        Set<Nature> filters= Sets.newHashSet(Nature.mq, Nature.m, Nature.q);
        if(enableQuantifierRecognize) {
            termParts=this.amountSegment.seg(text.toCharArray());
        }else {
            termParts = this.defaultSegment.seg(text.toCharArray());
        }

        for(Term term:termParts){
            // 只有amountSegment能分解出mq属性的term
            if(filters.contains(term.nature)){
                results.add(term);
            }
        }
        return results;
    }

    public List<AmountHolder> getAmountValues(String text){
        List<AmountHolder> result=Lists.newArrayList();
        List<Term> terms = parseAmount(text, false);
        for (Term term : terms) {
            if (term.nature == Nature.m) {
                AmountHolder holder=new AmountHolder();
                holder.term=term;

                String word=term.word;
                word=full2Half(word);
                if(StringUtils.isNumeric(word)){
                    holder.numericVal=word;
                }else {
                    word=AmountFilter.filter(term.word); // use the raw word
                    long longval = converter.convertToLongFromEnd(word);
                    holder.numericVal=Long.toString(longval);
                }

                result.add(holder);
            }
        }
        return result;
    }

}
