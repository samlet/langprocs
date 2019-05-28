package com.samlet.langprocs.chinese;

import org.junit.Test;

import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.Word2VecTrainer;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;

import java.io.IOException;
import java.util.Map;
public class TestWord2Vec {
    private static final String MODEL_FILE_NAME = "/pi/ai/hanlp/hanlp-wiki-vec-zh/hanlp-wiki-vec-zh.txt";
//    private static final String MODEL_FILE_NAME = "/pi/ai/models/wiki.zh/wiki.zh.vec";
    static WordVectorModel loadModel() throws IOException
    {
        return new WordVectorModel(MODEL_FILE_NAME);
    }

    @Test
    public void testWord2vec() throws IOException {
        WordVectorModel wordVectorModel = loadModel();
        printNearest("中国", wordVectorModel);
        printNearest("美丽", wordVectorModel);
        printNearest("购买", wordVectorModel);

        // 文档向量
        DocVectorModel docVectorModel = new DocVectorModel(wordVectorModel);
        String[] documents = new String[]{
                "山东苹果丰收",
                "农民在江苏种水稻",
                "奥运会女排夺冠",
                "世界锦标赛胜出",
                "中国足球失败",
        };

        System.out.println(docVectorModel.similarity(documents[0], documents[1]));
        System.out.println(docVectorModel.similarity(documents[0], documents[4]));

        for (int i = 0; i < documents.length; i++)
        {
            docVectorModel.addDocument(i, documents[i]);
        }

        printNearestDocument("体育", documents, docVectorModel);
        printNearestDocument("农业", documents, docVectorModel);
        printNearestDocument("我要看比赛", documents, docVectorModel);
        printNearestDocument("要不做饭吧", documents, docVectorModel);

        //+ 引用: https://github.com/huyingxi/Synonyms/blob/master/demo.py
        String sen1 = "旗帜引领方向";
        String sen2 = "旗帜指引道路";
        System.out.println(docVectorModel.similarity(sen1, sen2));
        sen1 = "旗帜引领方向";
        sen2 = "道路决定命运";
        System.out.println(docVectorModel.similarity(sen1, sen2));
    }

    static void printNearest(String word, WordVectorModel model)
    {
        System.out.printf("\n                                                Word     Cosine\n------------------------------------------------------------------------\n");
        for (Map.Entry<String, Float> entry : model.nearest(word))
        {
            System.out.printf("%50s\t\t%f\n", entry.getKey(), entry.getValue());
        }
    }

    static void printNearestDocument(String document, String[] documents, DocVectorModel model)
    {
        printHeader(document);
        for (Map.Entry<Integer, Float> entry : model.nearest(document))
        {
            System.out.printf("%50s\t\t%f\n", documents[entry.getKey()], entry.getValue());
        }
    }

    private static void printHeader(String query)
    {
        System.out.printf("\n%50s          Cosine\n------------------------------------------------------------------------\n", query);
    }
}
