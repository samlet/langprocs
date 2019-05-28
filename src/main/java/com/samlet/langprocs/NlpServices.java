/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.samlet.langprocs;

import com.google.inject.*;
import com.google.protobuf.Empty;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.document.sentence.Sentence;
import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.hankcs.hanlp.dictionary.py.Pinyin;
import com.hankcs.hanlp.seg.common.Term;
import com.samlet.langprocs.chinese.DependencyParserProcs;
import com.samlet.langprocs.parsers.AmountParser;
import com.samlet.langprocs.util.NlpProtoUtil;
import com.samlet.nlpserv.*;
import common.CommonTypes;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static com.samlet.nlpserv.NlPinyinRequest.PinyinPresentation.*;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * Server that manages startup/shutdown of a {@code NlpProcs} server.
 */
@javax.inject.Singleton
public class NlpServices {
    private static final Logger logger = Logger.getLogger(NlpServices.class.getName());

    private Server server;
    @Inject
    private Provider<NlpProcsImpl> nlpProcs;

    public void start() throws IOException {
        /* The port on which the server should run */
        int port = 10052;
        server = ServerBuilder.forPort(port)
                .addService(nlpProcs.get())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                NlpServices.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    static class NlpProcsImpl extends NlpProcsGrpc.NlpProcsImplBase {
        @Inject
        private Provider<SimilarityManager> similarityManagerProvider;
        @Inject
        private Provider<HanlpManager> hanlpManagerProvider;
        @Inject
        private Provider<AmountParser> amountParserProvider;

        @Override
        public void ping(CommonTypes.PingRequest req, StreamObserver<CommonTypes.PingReply> responseObserver) {
            CommonTypes.PingReply reply = CommonTypes.PingReply.newBuilder().setMessage("Welcome " + req.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void parseDependency(com.samlet.nlpserv.NlParseRequest request,
                                    io.grpc.stub.StreamObserver<com.samlet.nlpserv.NlSentence> responseObserver) {
            NlSentence result=DependencyParserProcs.parseDependency(request.getText());
            responseObserver.onNext(result);
            responseObserver.onCompleted();
        }

        @Override
        public void getPinyin(NlPinyinRequest request, StreamObserver<NlText> responseObserver) {
            String text=request.getText();
            List<Pinyin> pinyinList = HanLP.convertToPinyinList(text);
            StringBuilder result=new StringBuilder();
            switch (request.getPresentation()){
                case NUMBER:
                    for (Pinyin pinyin : pinyinList){
                        result.append(pinyin.toString());
                        result.append(' ');
                    }
                    break;
                case WITH_TONE_MARK:
                    for (Pinyin pinyin : pinyinList) {
                        result.append(pinyin.getPinyinWithToneMark());
                        result.append(' ');
                    }
                    break;
                case WITHOUT_TONE:
                    for (Pinyin pinyin : pinyinList) {
                        result.append(pinyin.getPinyinWithoutTone());
                        result.append(' ');
                    }
                    break;
            }
            responseObserver.onNext(NlText.newBuilder().setText(result.toString()).build());
            responseObserver.onCompleted();
        }

        public static void done(StreamObserver<NlResult> responseObserver){
            responseObserver.onNext(NlResult.newBuilder().setCode(0).build());
            responseObserver.onCompleted();
        }

        @Override
        public void addDocuments(NlDocumentSet request, StreamObserver<NlResult> responseObserver) {
            for(int i=0;i<request.getTextListCount();++i) {
                this.similarityManagerProvider.get().addDocument(request.getTextList(i));
            }
            done(responseObserver);
        }

        @Override
        public void getNearestDocuments(NlText request, StreamObserver<NlDocumentSimilaritySet> responseObserver) {
            List<SimilarityManager.DocumentSimilarity> result=this.similarityManagerProvider.get().getNearestDocuments(request.getText());
            NlDocumentSimilaritySet.Builder builder=NlDocumentSimilaritySet.newBuilder();
            for (SimilarityManager.DocumentSimilarity doc:result){
                builder.addDocs(NlDocumentSimilarity.newBuilder().setDocumentId(doc.documentId)
                        .setContent(doc.content)
                        .setSimilarity(doc.similarity)
                );
            }
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        }

        @Override
        public void tokenizer(NlTokenizerRequest request, StreamObserver<NlTokens> responseObserver) {
            Sentence sent=hanlpManagerProvider.get().getAnalyzer().analyze(request.getText().getText());
            NlTokens.Builder builder=NlTokens.newBuilder();
            for(IWord word:sent.wordList){
                builder.addTokens(NlToken.newBuilder().setText(word.getValue())
                    .setLabel(word.getLabel())
                    .setLength(word.length()));
            }
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        }

        @Override
        public void entityExtractor(NlTokenizerRequest request, StreamObserver<NlEntities> responseObserver) {
            List<Term> sents=hanlpManagerProvider.get().parse(request.getText().getText());
            NlEntities.Builder builder=NlEntities.newBuilder();
            for(Term word:sents){
                builder.addEntities(NlpProtoUtil.convertTerm(word));
            }
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        }

        @Override
        public void parseAmountTerms(NlText request, StreamObserver<NlAmountList> responseObserver) {
            List<AmountParser.AmountHolder> amountHolderList=amountParserProvider.get().getAmountValues(request.getText());
            NlAmountList.Builder builder=NlAmountList.newBuilder();
            for(AmountParser.AmountHolder item:amountHolderList){
                builder.addAmount(item.proto());
            }
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        }
    }
}
