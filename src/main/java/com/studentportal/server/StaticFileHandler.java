package com.studentportal.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticFileHandler implements HttpHandler {

    private final String rootDirectory;

    public StaticFileHandler(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String requestPath = exchange.getRequestURI().getPath();

        if (requestPath.equals("/")) {
            requestPath = "/index.html";
        }

        Path filePath = Paths.get(rootDirectory + requestPath);

        if (!Files.exists(filePath)) {

            String response = "404 - File Not Found";

            exchange.sendResponseHeaders(404, response.length());

            exchange.getResponseBody().write(response.getBytes());

            exchange.close();

            return;
        }

        String contentType = Files.probeContentType(filePath);

        if (contentType == null)
            contentType = "application/octet-stream";

        byte[] data = Files.readAllBytes(filePath);

        exchange.getResponseHeaders().set("Content-Type", contentType);

        exchange.sendResponseHeaders(200, data.length);

        exchange.getResponseBody().write(data);

        exchange.close();

    }
}