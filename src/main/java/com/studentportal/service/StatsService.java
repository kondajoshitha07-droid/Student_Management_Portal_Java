package com.studentportal.service;

import com.studentportal.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StatsService {
    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", 0);
        stats.put("cseStudents", 0);
        stats.put("eceStudents", 0);
        stats.put("averageAttendance", 0);
        
        String sql = "SELECT COUNT(*) as total, " +
                     "SUM(CASE WHEN department='CSE' THEN 1 ELSE 0 END) as cse, " +
                     "SUM(CASE WHEN department='ECE' THEN 1 ELSE 0 END) as ece, " +
                     "AVG(attendance) as avg_att " +
                     "FROM students";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                stats.put("totalStudents", rs.getInt("total"));
                stats.put("cseStudents", rs.getInt("cse"));
                stats.put("eceStudents", rs.getInt("ece"));
                stats.put("averageAttendance", Math.round(rs.getDouble("avg_att")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
    
    public boolean updateAttendance(String rollNo, int attendance) {
        String sql = "UPDATE students SET attendance = ? WHERE roll_no = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attendance);
            stmt.setString(2, rollNo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
