package com.studentportal.model;

public class Admin {
    private String username;
    private String passwordHash;

    public Admin() {}

    public Admin(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    @Override
    public String toString() {
        return "Admin{username='" + username + "'}";
    }
}
