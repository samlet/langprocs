package com.samlet.langprocs;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.utility.LexiconUtility;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class HanlpHelper {
    public String[] getHanlpInfo(){
        return new String[]{"portable-1.7.2"};
    }
    public void setNature(String nature, List<String> words){
        Nature pcNature = Nature.create(nature);
        words.forEach(word->{
            LexiconUtility.setAttribute(word, pcNature);
        });
    }
}
