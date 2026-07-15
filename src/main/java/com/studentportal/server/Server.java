package com.studentportal.server;

import com.sun.net.httpserver.HttpServer;
import com.studentportal.controller.LoginController;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    private HttpServer server;

    public void start() throws IOException {

        server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/login", new LoginController());
        server.createContext("/", new StaticFileHandler("frontend"));

        server.setExecutor(null);

        server.start();

        System.out.println("-----------------------------------------");
        System.out.println("HTTP Server Started Successfully");
        System.out.println("Listening at: http://localhost:8080");
        System.out.println("-----------------------------------------");
    }
}