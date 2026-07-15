package com.studentportal.dao;

import com.studentportal.database.DatabaseManager;
import com.studentportal.model.Mark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MarksDAO {

    public boolean insertMarks(Mark mark) {
        String sql = "INSERT INTO marks (roll_no, subject_name, marks) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, mark.getRollNo());
            stmt.setString(2, mark.getSubjectName());
            stmt.setInt(3, mark.getMarks());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateMarks(Mark mark) {
        String sql = "UPDATE marks SET roll_no = ?, subject_name = ?, marks = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, mark.getRollNo());
            stmt.setString(2, mark.getSubjectName());
            stmt.setInt(3, mark.getMarks());
            stmt.setInt(4, mark.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Mark> getMarksByRollNo(String rollNo) {
        List<Mark> marks = new ArrayList<>();
        String sql = "SELECT * FROM marks WHERE roll_no = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, rollNo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Mark mark = new Mark();
                    mark.setId(rs.getInt("id"));
                    mark.setRollNo(rs.getString("roll_no"));
                    mark.setSubjectName(rs.getString("subject_name"));
                    mark.setMarks(rs.getInt("marks"));
                    marks.add(mark);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marks;
    }

    public boolean deleteMarks(int id) {
        String sql = "DELETE FROM marks WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
