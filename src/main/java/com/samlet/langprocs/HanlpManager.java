package com.samlet.langprocs;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Singleton
public class HanlpManager {
    private CRFLexicalAnalyzer analyzer;
    private Segment segment;

    public HanlpManager() throws IOException {
        this.segment = HanLP.newSegment()
                .enableAllNamedEntityRecognize(true)
                .enableNumberQuantifierRecognize(true)
                .enableOffset(true);

        this.analyzer = new CRFLexicalAnalyzer();
    }

    public CRFLexicalAnalyzer getAnalyzer() {
        return analyzer;
    }
    public List<Term> parse(String sentence){
        return this.segment.seg(sentence);
    }
}

