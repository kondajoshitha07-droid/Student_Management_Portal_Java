package com.studentportal.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.studentportal.model.Mark;
import com.studentportal.dao.MarksDAO;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MarksController implements HttpHandler {
    private final MarksDAO marksDAO = new MarksDAO();
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            if ("GET".equalsIgnoreCase(method)) {
                handleGet(exchange);
            } else if ("POST".equalsIgnoreCase(method)) {
                handlePost(exchange);
            } else {
                sendJsonResponse(exchange, 405, false, "Method Not Allowed", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(exchange, 500, false, "Internal Error", null);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.contains("rollNo=")) {
            String rollNo = "";
            int semester = -1;
            
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("rollNo=")) {
                    rollNo = java.net.URLDecoder.decode(param.split("=")[1], StandardCharsets.UTF_8.name());
                } else if (param.startsWith("semester=")) {
                    semester = Integer.parseInt(param.split("=")[1]);
                }
            }
            
            List<Mark> marks;
            if (semester != -1) {
                marks = marksDAO.getMarksBySemester(rollNo, semester);
            } else {
                marks = marksDAO.getMarksByRollNo(rollNo);
            }
            
            sendJsonResponse(exchange, 200, true, "Marks retrieved successfully", gson.toJsonTree(marks));
        } else {
            sendJsonResponse(exchange, 400, false, "rollNo required", null);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Mark mark = gson.fromJson(reader, Mark.class);
            if (mark == null || mark.getRollNo() == null || mark.getSubjectName() == null) {
                sendJsonResponse(exchange, 400, false, "Invalid data", null);
                return;
            }
            
            List<Mark> existing = marksDAO.getMarksByRollNo(mark.getRollNo());
            boolean exists = false;
            for (Mark m : existing) {
                if (m.getSubjectName().equalsIgnoreCase(mark.getSubjectName()) && m.getSemester() == mark.getSemester()) {
                    mark.setId(m.getId());
                    exists = true;
                    break;
                }
            }
            
            boolean success;
            if (exists) {
                success = marksDAO.updateMarks(mark);
            } else {
                success = marksDAO.insertMarks(mark);
            }
            
            if (success) {
                sendJsonResponse(exchange, 200, true, "Marks saved", null);
            } else {
                sendJsonResponse(exchange, 400, false, "Failed to save marks", null);
            }
        }
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, boolean success, String message, com.google.gson.JsonElement data) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("success", success);
        if (message != null) json.addProperty("message", message);
        if (data != null) json.add("data", data);
        String response = gson.toJson(json);
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
