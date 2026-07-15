package com.studentportal.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.studentportal.model.Student;
import com.studentportal.service.StudentService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class StudentController implements HttpHandler {
    private final StudentService studentService = new StudentService();
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            if ("GET".equalsIgnoreCase(method)) {
                handleGet(exchange);
            } else if ("POST".equalsIgnoreCase(method)) {
                handlePost(exchange);
            } else if ("PUT".equalsIgnoreCase(method)) {
                handlePut(exchange);
            } else if ("DELETE".equalsIgnoreCase(method)) {
                handleDelete(exchange);
            } else {
                sendJsonResponse(exchange, 405, false, "Method Not Allowed", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(exchange, 500, false, "Internal Server Error", null);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        List<Student> students;
        if (query != null && query.contains("search=")) {
            String searchTerm = query.split("search=")[1].split("&")[0]; 
            students = studentService.searchStudents(java.net.URLDecoder.decode(searchTerm, StandardCharsets.UTF_8.name()));
        } else {
            students = studentService.getAllStudents();
        }
        
        JsonObject response = new JsonObject();
        response.addProperty("success", true);
        response.add("data", gson.toJsonTree(students));
        sendJsonResponse(exchange, 200, response);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Student student = gson.fromJson(reader, Student.class);
            if (student == null || student.getRollNo() == null || student.getRollNo().isEmpty()) {
                sendJsonResponse(exchange, 400, false, "Invalid student data", null);
                return;
            }
            boolean success = studentService.createStudent(student);
            if (success) {
                sendJsonResponse(exchange, 200, true, "Student created successfully", null);
            } else {
                sendJsonResponse(exchange, 400, false, "Failed to create student. Roll number might already exist.", null);
            }
        } catch (JsonSyntaxException e) {
            sendJsonResponse(exchange, 400, false, "Invalid JSON", null);
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Student student = gson.fromJson(reader, Student.class);
            if (student == null || student.getRollNo() == null || student.getRollNo().isEmpty()) {
                sendJsonResponse(exchange, 400, false, "Invalid student data", null);
                return;
            }
            boolean success = studentService.updateStudent(student);
            if (success) {
                sendJsonResponse(exchange, 200, true, "Student updated successfully", null);
            } else {
                sendJsonResponse(exchange, 400, false, "Failed to update student", null);
            }
        } catch (JsonSyntaxException e) {
            sendJsonResponse(exchange, 400, false, "Invalid JSON", null);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.contains("rollNo=")) {
            String rollNo = query.split("rollNo=")[1].split("&")[0];
            rollNo = java.net.URLDecoder.decode(rollNo, StandardCharsets.UTF_8.name());
            boolean success = studentService.deleteStudent(rollNo);
            if (success) {
                sendJsonResponse(exchange, 200, true, "Student deleted successfully", null);
            } else {
                sendJsonResponse(exchange, 400, false, "Failed to delete student", null);
            }
        } else {
            sendJsonResponse(exchange, 400, false, "Roll number is required", null);
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
