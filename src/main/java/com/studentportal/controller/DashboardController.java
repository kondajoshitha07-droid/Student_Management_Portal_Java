package com.studentportal.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.studentportal.service.StatsService;
import com.studentportal.service.StudentService;
import com.studentportal.utils.SessionManager;
import com.studentportal.model.UserSession;
import com.studentportal.model.Student;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DashboardController implements HttpHandler {
    private final StatsService statsService = new StatsService();
    private final StudentService studentService = new StudentService();
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        
        try {
            if ("/api/admin/stats".equals(path) && "GET".equalsIgnoreCase(method)) {
                Map<String, Object> stats = statsService.getAdminStats();
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.add("data", gson.toJsonTree(stats));
                sendJsonResponse(exchange, 200, response);
            } 
            else if ("/api/admin/attendance".equals(path) && "POST".equalsIgnoreCase(method)) {
                try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                    JsonObject req = gson.fromJson(reader, JsonObject.class);
                    String rollNo = req.get("rollNo").getAsString();
                    int attendance = req.get("attendance").getAsInt();
                    boolean ok = statsService.updateAttendance(rollNo, attendance);
                    sendJsonResponse(exchange, 200, ok, ok ? "Saved" : "Failed", null);
                }
            }
            else if ("/api/student/profile".equals(path) && "GET".equalsIgnoreCase(method)) {
                String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String sessionId = authHeader.substring(7);
                    UserSession session = SessionManager.getSession(sessionId);
                    if (session != null && "student".equals(session.getRole())) {
                        Student student = studentService.getStudent(session.getUsername());
                        if (student != null) {
                            JsonObject response = new JsonObject();
                            response.addProperty("success", true);
                            // Avoid sending hash to frontend
                            student.setPasswordHash(null);
                            response.add("data", gson.toJsonTree(student));
                            sendJsonResponse(exchange, 200, response);
                            return;
                        }
                    }
                }
                sendJsonResponse(exchange, 401, false, "Unauthorized", null);
            }
            else {
                sendJsonResponse(exchange, 404, false, "Not Found", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(exchange, 500, false, "Server Error", null);
        }
    }
    
    private void sendJsonResponse(HttpExchange exchange, int statusCode, boolean success, String message, JsonObject data) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("success", success);
        if (message != null) json.addProperty("message", message);
        if (data != null) json.add("data", data);
        sendJsonResponse(exchange, statusCode, json);
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, JsonObject responseJson) throws IOException {
        String response = gson.toJson(responseJson);
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
