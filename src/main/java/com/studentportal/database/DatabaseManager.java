package com.studentportal.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.mindrot.jbcrypt.BCrypt;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:database/student_portal.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        createTables();
        insertDefaultAdmin();
        insertMockData();
    }

    private static void createTables() {
        String createAdminsTable = "CREATE TABLE IF NOT EXISTS admins (" +
                "username TEXT PRIMARY KEY, " +
                "password_hash TEXT NOT NULL" +
                ");";

        String createStudentsTable = "CREATE TABLE IF NOT EXISTS students (" +
                "roll_no TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "password_hash TEXT NOT NULL, " +
                "department TEXT NOT NULL, " +
                "year INTEGER NOT NULL, " +
                "email TEXT, " +
                "phone TEXT" +
                ");";

        String createMarksTable = "CREATE TABLE IF NOT EXISTS marks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "roll_no TEXT NOT NULL, " +
                "subject_name TEXT NOT NULL, " +
                "marks INTEGER NOT NULL, " +
                "FOREIGN KEY (roll_no) REFERENCES students(roll_no), " +
                "UNIQUE(roll_no, subject_name)" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("PRAGMA foreign_keys = ON;");
            
            stmt.execute(createAdminsTable);
            stmt.execute(createStudentsTable);
            stmt.execute(createMarksTable);
            
            try {
                stmt.execute("ALTER TABLE students ADD COLUMN attendance INTEGER DEFAULT 0;");
            } catch (SQLException ignored) {
                // Column might already exist
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertDefaultAdmin() {
        String checkAdmin = "SELECT COUNT(*) FROM admins WHERE username = ?";
        String insertAdmin = "INSERT INTO admins (username, password_hash) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkAdmin)) {
            checkStmt.setString(1, "admin");
            var rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertAdmin)) {
                    insertStmt.setString(1, "admin");
                    String hashed = BCrypt.hashpw("admin123", BCrypt.gensalt());
                    insertStmt.setString(2, hashed);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void insertMockData() {
        String checkStudents = "SELECT COUNT(*) FROM students";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            var rs = stmt.executeQuery(checkStudents);
            if (rs.next() && rs.getInt(1) == 0) {
                String hashed = BCrypt.hashpw("password123", BCrypt.gensalt());
                String insertStudent = "INSERT INTO students (roll_no, name, password_hash, department, year, email, phone, attendance) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                String insertMark = "INSERT INTO marks (roll_no, subject_name, marks) VALUES (?, ?, ?)";
                
                try (PreparedStatement pstStudent = conn.prepareStatement(insertStudent);
                     PreparedStatement pstMark = conn.prepareStatement(insertMark)) {
                    
                    for (int i = 1; i <= 10; i++) {
                        String rollNo = "STU" + String.format("%03d", i);
                        String dept = (i <= 5) ? "CSE" : "ECE";
                        
                        pstStudent.setString(1, rollNo);
                        pstStudent.setString(2, "Demo Student " + i);
                        pstStudent.setString(3, hashed);
                        pstStudent.setString(4, dept);
                        pstStudent.setInt(5, 3);
                        pstStudent.setString(6, rollNo.toLowerCase() + "@university.edu");
                        pstStudent.setString(7, "555-010" + i);
                        pstStudent.setInt(8, 80 + (int)(Math.random() * 19)); 
                        pstStudent.executeUpdate();
                        
                        String[] subjects = {"Data Structures", "Algorithms", "Operating Systems", "Databases"};
                        for (String sub : subjects) {
                            pstMark.setString(1, rollNo);
                            pstMark.setString(2, sub);
                            pstMark.setInt(3, 40 + (int)(Math.random() * 61)); 
                            pstMark.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
