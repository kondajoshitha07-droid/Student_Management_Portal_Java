package com.studentportal.controller;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HomeController {

    public void handle(HttpExchange exchange) throws IOException {

        byte[] response = Files.readAllBytes(
                Paths.get("frontend/pages/index.html")
        );

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");

        exchange.sendResponseHeaders(200, response.length);

        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }
}