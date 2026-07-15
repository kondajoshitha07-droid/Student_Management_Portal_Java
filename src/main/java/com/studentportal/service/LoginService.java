package com.studentportal.service;

import com.studentportal.dao.AdminDAO;
import com.studentportal.dao.StudentDAO;
import com.studentportal.model.Student;
import org.mindrot.jbcrypt.BCrypt;

public class LoginService {
    private final AdminDAO adminDAO = new AdminDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    public boolean authenticate(String role, String username, String password) {
        if ("admin".equalsIgnoreCase(role)) {
            return adminDAO.validateLogin(username, password);
        } else if ("student".equalsIgnoreCase(role)) {
            Student student = studentDAO.getStudentByRollNo(username);
            if (student != null) {
                return BCrypt.checkpw(password, student.getPasswordHash());
            }
        }
        return false;
    }
}
