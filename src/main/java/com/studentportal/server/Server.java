package com.studentportal.server;

import com.sun.net.httpserver.HttpServer;
import com.studentportal.controller.LoginController;
import com.studentportal.controller.StudentController;
import com.studentportal.controller.DashboardController;
import com.studentportal.controller.MarksController;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    private HttpServer server;

    public void start() throws IOException {

        server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/login", new LoginController());
        server.createContext("/students", new StudentController());
        server.createContext("/api/admin", new DashboardController());
        server.createContext("/api/student", new DashboardController());
        server.createContext("/api/marks", new MarksController());
        server.createContext("/", new StaticFileHandler("frontend"));

        server.setExecutor(null);

        server.start();

        System.out.println("-----------------------------------------");
        System.out.println("HTTP Server Started Successfully");
        System.out.println("Listening at: http://localhost:8080");
        System.out.println("-----------------------------------------");
    }
}