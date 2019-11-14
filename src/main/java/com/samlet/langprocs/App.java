package com.samlet.langprocs;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.samlet.langprocs.chinese.DependencyParserProcs;
import py4j.CallbackClient;
import py4j.GatewayServer;

import java.io.IOException;
import java.net.InetAddress;

import static py4j.GatewayServer.DEFAULT_CONNECT_TIMEOUT;
import static py4j.GatewayServer.DEFAULT_READ_TIMEOUT;

/**
 * Hello world!
 */
public class App {
    private Injector injector;
    private GatewayServer gatewayServer;

    public HanlpHelper helper(){
        return injector.getInstance(HanlpHelper.class);
    }

    /**
     * Main launches the server from the command line.
     */
    private void startNlpServices(boolean supportPy4j) throws IOException, InterruptedException {
        if(supportPy4j) {
            InetAddress defaultAddress = InetAddress.getByName("0.0.0.0");
            int port = 2333;
            int callbackPort = 2334;
            gatewayServer = new GatewayServer(this,
                    port, defaultAddress,
                    DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, null,
                    new CallbackClient(callbackPort, defaultAddress));
            gatewayServer.start();
            System.out.println(String.format(" [✔] Gateway Server Started in port %d and callback port %d",
                    port, callbackPort));
        }

        this.injector= Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                if(gatewayServer!=null) {
                    bind(GatewayServer.class).toInstance(gatewayServer);
                }
            }
        });

        // final NlpServices server = new NlpServices();
        NlpServices server = injector.getInstance(NlpServices.class);
        server.start();
        if(!supportPy4j) {
            System.out.println(" [✔] NlpServices Started");
            server.blockUntilShutdown();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // System.out.println(args.length);
        if (args.length > 0) {
            System.out.format("with arg %s\n", args[0]);
            String cmd = args[0];
            if (cmd.equalsIgnoreCase("test")) {
                DependencyParserProcs.main(args);
            } else if (cmd.equalsIgnoreCase("nlp")) {
                new App().startNlpServices(true);
            }
        } else {
            System.out.println("* start nlp services");
            // new App().startNlpServices(false);
            new App().startNlpServices(true);
        }
    }
}
