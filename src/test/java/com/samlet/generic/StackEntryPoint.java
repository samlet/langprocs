package com.samlet.generic;

import py4j.CallbackClient;
import py4j.GatewayServer;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static py4j.GatewayServer.DEFAULT_CONNECT_TIMEOUT;
import static py4j.GatewayServer.DEFAULT_READ_TIMEOUT;

public class StackEntryPoint {

    private Stack stack;

    public StackEntryPoint() {
        stack = new Stack();
        stack.push("Initial Item");
    }

    public Stack getStack() {
        return stack;
    }

    public static void main(String[] args) throws UnknownHostException {
        InetAddress defaultAddress = InetAddress.getByName("0.0.0.0");
        int port = 2333;
        int callbackPort = 2334;
        GatewayServer gatewayServer = new GatewayServer(new StackEntryPoint(),
                port, defaultAddress,
                DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, null,
                new CallbackClient(callbackPort, defaultAddress));
        gatewayServer.start();
        System.out.println(" [✔] Gateway Server Started");
    }

}

