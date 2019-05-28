package com.samlet.langprocs.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import scala.concurrent.ExecutionContextExecutor;

import java.util.concurrent.CompletionStage;

import static akka.pattern.PatternsCS.pipe;

public class HttpClientTestor {
    class SingleRequestInActorExample extends AbstractActor {
        final Http http = Http.get(context().system());
        final ExecutionContextExecutor dispatcher = context().dispatcher();
        final Materializer materializer = ActorMaterializer.create(context());

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(String.class, url -> pipe(fetch(url), dispatcher).to(self()))
                    .build();
        }

        CompletionStage<HttpResponse> fetch(String url) {
            return http.singleRequest(HttpRequest.create(url));
        }
    }

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create();
        final Materializer materializer = ActorMaterializer.create(system);

        final CompletionStage<HttpResponse> responseFuture =
                Http.get(system)
                        .singleRequest(HttpRequest.create("http://akka.io"), materializer);
    }
}
