package com.studentportal.service;

import com.studentportal.dao.StudentDAO;
import com.studentportal.model.Student;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class StudentService {
    private final StudentDAO studentDAO = new StudentDAO();

    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    public List<Student> searchStudents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllStudents();
        }
        return studentDAO.searchStudentsByName(query.trim());
    }
    
    public Student getStudent(String rollNo) {
        return studentDAO.getStudentByRollNo(rollNo);
    }

    public boolean createStudent(Student student) {
        if (student.getPasswordHash() != null && !student.getPasswordHash().isEmpty()) {
            student.setPasswordHash(BCrypt.hashpw(student.getPasswordHash(), BCrypt.gensalt()));
        }
        return studentDAO.createStudent(student);
    }

    public boolean updateStudent(Student student) {
        Student existing = studentDAO.getStudentByRollNo(student.getRollNo());
        if (existing == null) return false;

        if (student.getPasswordHash() != null && !student.getPasswordHash().isEmpty()) {
            student.setPasswordHash(BCrypt.hashpw(student.getPasswordHash(), BCrypt.gensalt()));
        } else {
            student.setPasswordHash(existing.getPasswordHash());
        }
        return studentDAO.updateStudent(student);
    }

    public boolean deleteStudent(String rollNo) {
        return studentDAO.deleteStudent(rollNo);
    }
}
