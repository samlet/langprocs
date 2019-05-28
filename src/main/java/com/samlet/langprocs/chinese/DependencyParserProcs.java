/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/12/7 20:14</create-date>
 *
 * <copyright file="DemoPosTagging.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package com.samlet.langprocs.chinese;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.samlet.nlpserv.*;
import com.samlet.nlpserv.NlWord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 依存句法分析（CRF句法模型需要-Xms512m -Xmx512m -Xmn256m，MaxEnt和神经网络句法模型需要-Xms1g -Xmx1g -Xmn512m）
 * @author hankcs
 */
public class DependencyParserProcs
{
    public static NlSentence parseDependency(String raw)  {
        NlSentence.Builder nlSentence=NlSentence.newBuilder();
        CoNLLSentence sentence = HanLP.parseDependency(raw);
        for (CoNLLWord word : sentence) {
            // System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
            NlWord nlWord=NlWord.newBuilder().setId(word.ID)
                    .setLemma(word.LEMMA)
                    .setPostag1(word.POSTAG)
                    .setPostag2(word.CPOSTAG)
                    .setDeprel(word.DEPREL)
                    .setHeadId(word.HEAD.ID)
                    .setName(word.NAME)
                    .build();
            nlSentence.addWords(nlWord);
        }

        // NlSentences sentences=NlSentences.newBuilder().addSentences(nlSentence).build();
        return nlSentence.build();
    }

    public static void main(String[] args) throws IOException {
        NlSentence.Builder nlSentence=NlSentence.newBuilder();
        CoNLLSentence sentence = HanLP.parseDependency("徐先生还具体帮助他确定了把画雄鹰、松鼠和麻雀作为主攻目标。");
        System.out.println(sentence);
        // 可以方便地遍历它
        for (CoNLLWord word : sentence)
        {
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
            NlWord nlWord=NlWord.newBuilder().setId(word.ID)
                    .setLemma(word.LEMMA)
                    .setPostag1(word.POSTAG)
                    .setPostag2(word.CPOSTAG)
                    .setDeprel(word.DEPREL)
                    .setHeadId(word.HEAD.ID)
                    .setName(word.NAME)
                    .build();
            nlSentence.addWords(nlWord);
        }

        // persist the sentence to file
        NlSentences sentences=NlSentences.newBuilder().addSentences(nlSentence).build();
        // nlSentence.build().writeDelimitedTo();
        File folder=new File("./dataset");
        if(!folder.exists()){
            folder.mkdirs();
        }
        sentences.writeTo(new FileOutputStream(new File("./dataset/simple.bin")));
    }
}
