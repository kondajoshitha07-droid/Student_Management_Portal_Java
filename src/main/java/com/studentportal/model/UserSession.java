package com.studentportal.model;

public class UserSession {
    private String sessionId;
    private String username;
    private String role;
    private long loginTime;

    public UserSession(String sessionId, String username, String role, long loginTime) {
        this.sessionId = sessionId;
        this.username = username;
        this.role = role;
        this.loginTime = loginTime;
    }

    public String getSessionId() { return sessionId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public long getLoginTime() { return loginTime; }
}
