package com.studentportal.dao;

import com.studentportal.database.DatabaseManager;
import com.studentportal.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public boolean createStudent(Student student) {
        String sql = "INSERT INTO students (roll_no, name, password_hash, department, year, email, phone) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, student.getRollNo());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getPasswordHash());
            stmt.setString(4, student.getDepartment());
            stmt.setInt(5, student.getYear());
            stmt.setString(6, student.getEmail());
            stmt.setString(7, student.getPhone());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET name = ?, password_hash = ?, department = ?, year = ?, email = ?, phone = ? WHERE roll_no = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getPasswordHash());
            stmt.setString(3, student.getDepartment());
            stmt.setInt(4, student.getYear());
            stmt.setString(5, student.getEmail());
            stmt.setString(6, student.getPhone());
            stmt.setString(7, student.getRollNo());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteStudent(String rollNo) {
        String sql = "DELETE FROM students WHERE roll_no = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, rollNo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Student getStudentByRollNo(String rollNo) {
        String sql = "SELECT * FROM students WHERE roll_no = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, rollNo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Student getStudentByEmail(String email) {
        String sql = "SELECT * FROM students WHERE email = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public List<Student> searchStudentsByName(String query) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE name LIKE ? OR roll_no LIKE ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
    
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setRollNo(rs.getString("roll_no"));
        student.setName(rs.getString("name"));
        student.setPasswordHash(rs.getString("password_hash"));
        student.setDepartment(rs.getString("department"));
        student.setYear(rs.getInt("year"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        try {
            student.setAttendance(rs.getInt("attendance"));
        } catch (SQLException ignored) {
            // In case column is missing in older queries somehow
        }
        return student;
    }
}
