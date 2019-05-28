package com.samlet.langprocs;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Singleton
public class SimilarityManager {
    public static class TextDocument{
        public TextDocument(String content) {
            this.content = content;
        }

        public String content;
    }

    public static class DocumentSimilarity{
        public int documentId;
        public String content;
        public float similarity;

        public DocumentSimilarity(int documentId, String content, float similarity) {
            this.documentId = documentId;
            this.content = content;
            this.similarity = similarity;
        }

        @Override
        public String toString() {
            return "DocumentSimilarity{" +
                    "documentId=" + documentId +
                    ", content='" + content + '\'' +
                    ", similarity=" + similarity +
                    '}';
        }
    }

    private static final String MODEL_FILE_NAME = "/pi/ai/hanlp/hanlp-wiki-vec-zh/hanlp-wiki-vec-zh.txt";
    //    private static final String MODEL_FILE_NAME = "/pi/ai/models/wiki.zh/wiki.zh.vec";
    private WordVectorModel wordVectorModel;
    private Map<Integer, TextDocument> documents= Maps.newConcurrentMap();
    private int serialNumber=0;

    public DocVectorModel getDocVectorModel() {
        return docVectorModel;
    }

    private DocVectorModel docVectorModel;
    public SimilarityManager() throws IOException {
        this.wordVectorModel= new WordVectorModel(MODEL_FILE_NAME);
        this.docVectorModel = new DocVectorModel(wordVectorModel);
    }

    public void addDocuments(String[] documents){
        for (String doc:documents){
            addDocument(doc);
        }
    }

    public void addDocument(String doc) {
        docVectorModel.addDocument(serialNumber, doc);
        this.documents.put(serialNumber, new TextDocument(doc));
        serialNumber++;
    }

    public List<DocumentSimilarity> getNearestDocuments(String text){
        List<DocumentSimilarity> result= Lists.newArrayList();
        for (Map.Entry<Integer, Float> entry : docVectorModel.nearest(text)) {
            result.add(new DocumentSimilarity(entry.getKey(),
                    documents.get(entry.getKey()).content,
                    entry.getValue()));
        }

        return result;
    }

    public float similarity(String what, String with){
        return docVectorModel.similarity(what,with);
    }
    public WordVectorModel getWordVectorModel(){
        return wordVectorModel;
    }
}
