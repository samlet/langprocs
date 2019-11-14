package com.samlet.generic;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.samlet.langprocs.NlpServices;
import com.samlet.nlpserv.NlpProcsGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class NlpServicesTester {
    static class SimpleImpl extends NlpProcsGrpc.NlpProcsImplBase {

    }
    public static void startSimple() throws IOException, InterruptedException {
        /* The port on which the server should run */
        int port = 11052;
        Server server = ServerBuilder.forPort(port)
                .addService(new SimpleImpl())
                .build()
                .start();
        // logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                server.shutdown();
                System.err.println("*** server shut down");
            }
        });

        System.out.println(" [✔] Simple NlpServices Started");
        server.awaitTermination();
    }

    static void startDefault() throws InterruptedException, IOException {
        Injector injector= Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                super.configure();
            }
        });
        NlpServices server = injector.getInstance(NlpServices.class);
        server.start();
        System.out.println(" [✔] NlpServices Started");
        server.blockUntilShutdown();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        startSimple();
    }
}
