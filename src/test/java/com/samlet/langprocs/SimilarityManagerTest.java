package com.samlet.langprocs;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;

import java.util.List;

public class SimilarityManagerTest {
    @Test
    public void testManager(){
        Injector injector=Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {

            }
        });
        SimilarityManager manager=injector.getInstance(SimilarityManager.class);
        String[] documents = new String[]{
                "山东苹果丰收",
                "农民在江苏种水稻",
                "奥运会女排夺冠",
                "世界锦标赛胜出",
                "中国足球失败",
        };
        manager.addDocuments(documents);
        List<SimilarityManager.DocumentSimilarity> result= manager.getNearestDocuments("体育");
        for (SimilarityManager.DocumentSimilarity doc:result) {
            System.out.println(doc);
        }
    }
}
