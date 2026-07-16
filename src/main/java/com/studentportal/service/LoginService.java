package com.studentportal.service;

import com.studentportal.dao.AdminDAO;
import com.studentportal.dao.StudentDAO;
import com.studentportal.model.Student;
import org.mindrot.jbcrypt.BCrypt;

public class LoginService {
    private final AdminDAO adminDAO = new AdminDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    public boolean authenticate(String role, String username, String password) {
        if ("principal".equalsIgnoreCase(role)) {
            return adminDAO.validatePrincipal(username, password);
        } else if ("faculty".equalsIgnoreCase(role)) {
            return adminDAO.validateFaculty(username, password);
        } else if ("student".equalsIgnoreCase(role)) {
            Student student;
            if (username.contains("@")) {
                student = studentDAO.getStudentByEmail(username);
            } else {
                student = studentDAO.getStudentByRollNo(username);
            }
            if (student != null) {
                return BCrypt.checkpw(password, student.getPasswordHash());
            }
        }
        return false;
    }
}
