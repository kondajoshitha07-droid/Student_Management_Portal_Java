package com.studentportal.utils;

import com.studentportal.model.UserSession;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final ConcurrentHashMap<String, UserSession> activeSessions = new ConcurrentHashMap<>();

    public static String createSession(String username, String role) {
        String sessionId = UUID.randomUUID().toString();
        UserSession session = new UserSession(sessionId, username, role, System.currentTimeMillis());
        activeSessions.put(sessionId, session);
        return sessionId;
    }

    public static boolean isValid(String sessionId) {
        return sessionId != null && activeSessions.containsKey(sessionId);
    }

    public static void removeSession(String sessionId) {
        if (sessionId != null) {
            activeSessions.remove(sessionId);
        }
    }
    
    public static UserSession getSession(String sessionId) {
        if (sessionId == null) return null;
        return activeSessions.get(sessionId);
    }
}
