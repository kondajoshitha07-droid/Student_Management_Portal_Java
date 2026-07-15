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
}
