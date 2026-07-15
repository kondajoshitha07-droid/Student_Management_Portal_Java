package com.studentportal.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.studentportal.service.LoginService;
import com.studentportal.utils.SessionManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class LoginController implements HttpHandler {
    private final LoginService loginService = new LoginService();
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);
                
                String role = "";
                String username = "";
                String password = "";

                if (jsonRequest != null) {
                    if (jsonRequest.has("role") && !jsonRequest.get("role").isJsonNull()) {
                        role = jsonRequest.get("role").getAsString().trim();
                    }
                    if (jsonRequest.has("username") && !jsonRequest.get("username").isJsonNull()) {
                        username = jsonRequest.get("username").getAsString().trim();
                    }
                    if (jsonRequest.has("password") && !jsonRequest.get("password").isJsonNull()) {
                        password = jsonRequest.get("password").getAsString().trim();
                    }
                }

                JsonObject responseJson = new JsonObject();
                int statusCode;
                
                if (!role.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
                    boolean authenticated = loginService.authenticate(role, username, password);
                    if (authenticated) {
                        String sessionId = SessionManager.createSession(username, role);
                        responseJson.addProperty("success", true);
                        responseJson.addProperty("role", role.toLowerCase());
                        responseJson.addProperty("sessionId", sessionId);
                        statusCode = 200;
                    } else {
                        responseJson.addProperty("success", false);
                        responseJson.addProperty("message", "Invalid Credentials");
                        statusCode = 401;
                    }
                } else {
                    responseJson.addProperty("success", false);
                    responseJson.addProperty("message", "Invalid Credentials");
                    statusCode = 401;
                }

                String response = gson.toJson(responseJson);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(statusCode, responseBytes.length);
                
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
