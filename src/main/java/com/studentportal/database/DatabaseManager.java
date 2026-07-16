package com.studentportal.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        insertMockAuthData();
        insertMockData();
        ensureSemesterMarks();
    }

    private static void createTables() {
        String createPrincipalsTable = "CREATE TABLE IF NOT EXISTS principals (" +
                "username TEXT PRIMARY KEY, " +
                "password_hash TEXT NOT NULL" +
                ");";

        String createFacultyTable = "CREATE TABLE IF NOT EXISTS faculty (" +
                "email TEXT PRIMARY KEY, " +
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
                "semester INTEGER NOT NULL, " +
                "subject_name TEXT NOT NULL, " +
                "marks INTEGER NOT NULL, " +
                "FOREIGN KEY (roll_no) REFERENCES students(roll_no), " +
                "UNIQUE(roll_no, semester, subject_name)" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("PRAGMA foreign_keys = ON;");
            
            stmt.execute(createPrincipalsTable);
            stmt.execute(createFacultyTable);
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

    private static void insertMockAuthData() {
        String checkPrincipal = "SELECT COUNT(*) FROM principals WHERE username = ?";
        String insertPrincipal = "INSERT INTO principals (username, password_hash) VALUES (?, ?)";
        
        String checkFaculty = "SELECT COUNT(*) FROM faculty";
        String insertFaculty = "INSERT INTO faculty (email, password_hash) VALUES (?, ?)";

        try (Connection conn = getConnection()) {
            // Principal
            try (PreparedStatement checkStmt = conn.prepareStatement(checkPrincipal)) {
                checkStmt.setString(1, "principal");
                var rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertPrincipal)) {
                        insertStmt.setString(1, "principal");
                        String hashed = BCrypt.hashpw("principal123", BCrypt.gensalt());
                        insertStmt.setString(2, hashed);
                        insertStmt.executeUpdate();
                    }
                }
            }
            
            // Faculty
            try (Statement stmt = conn.createStatement()) {
                var rs = stmt.executeQuery(checkFaculty);
                if (rs.next() && rs.getInt(1) == 0) {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertFaculty)) {
                        String hashed = BCrypt.hashpw("faculty123", BCrypt.gensalt());
                        String[] emails = {"faculty1@university.edu", "faculty2@university.edu", "faculty3@university.edu"};
                        for (String email : emails) {
                            insertStmt.setString(1, email);
                            insertStmt.setString(2, hashed);
                            insertStmt.executeUpdate();
                        }
                    }
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
                String insertMark = "INSERT INTO marks (roll_no, semester, subject_name, marks) VALUES (?, ?, ?, ?)";
                
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
                        for (int sem = 1; sem <= 4; sem++) {
                            for (String sub : subjects) {
                                pstMark.setString(1, rollNo);
                                pstMark.setInt(2, sem);
                                pstMark.setString(3, sub + " (Sem " + sem + ")");
                                pstMark.setInt(4, 60 + (int)(Math.random() * 39)); 
                                pstMark.executeUpdate();
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void ensureSemesterMarks() {
        String getStudents = "SELECT roll_no FROM students";
        String checkStudentMarks = "SELECT COUNT(*) FROM marks WHERE roll_no = ?";
        String insertMark = "INSERT INTO marks (roll_no, semester, subject_name, marks) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rsStudents = stmt.executeQuery(getStudents);
             PreparedStatement checkStmt = conn.prepareStatement(checkStudentMarks);
             PreparedStatement insertStmt = conn.prepareStatement(insertMark)) {
             
            String[][] semesterSubjects = {
                {"Mathematics", "Physics", "Programming Fundamentals"},
                {"Data Structures", "Digital Logic", "Discrete Mathematics"},
                {"Operating Systems", "Database Systems", "Java Programming"},
                {"Software Engineering", "Algorithms", "Web Technologies"}
            };
            
            while (rsStudents.next()) {
                String rollNo = rsStudents.getString("roll_no");
                checkStmt.setString(1, rollNo);
                try (ResultSet rsMarks = checkStmt.executeQuery()) {
                    if (rsMarks.next() && rsMarks.getInt(1) == 0) {
                        // Insert 12 records for this student
                        for (int sem = 1; sem <= 4; sem++) {
                            for (String sub : semesterSubjects[sem - 1]) {
                                insertStmt.setString(1, rollNo);
                                insertStmt.setInt(2, sem);
                                insertStmt.setString(3, sub);
                                insertStmt.setInt(4, 60 + (int)(Math.random() * 39)); // 60 to 98
                                insertStmt.executeUpdate();
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
