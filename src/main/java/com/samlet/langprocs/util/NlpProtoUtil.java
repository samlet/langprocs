package com.samlet.langprocs.util;

import com.hankcs.hanlp.seg.common.Term;
import com.samlet.nlpserv.NlEntity;

public class NlpProtoUtil {
    public static NlEntity convertTerm (Term word){
        return NlEntity.newBuilder()
                .setEntity(word.nature.toString())
                .setValue(word.word)
                .setStart(word.offset)
                .setEnd(word.offset+word.length())
                .build();
    }
}
