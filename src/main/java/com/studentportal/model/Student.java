package com.studentportal.model;

public class Student {
    private String rollNo;
    private String name;
    private String passwordHash;
    private String department;
    private int year;
    private String email;
    private String phone;
    private int attendance;

    public Student() {}

    public Student(String rollNo, String name, String passwordHash, String department, int year, String email, String phone) {
        this.rollNo = rollNo;
        this.name = name;
        this.passwordHash = passwordHash;
        this.department = department;
        this.year = year;
        this.email = email;
        this.phone = phone;
    }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getAttendance() { return attendance; }
    public void setAttendance(int attendance) { this.attendance = attendance; }

    @Override
    public String toString() {
        return "Student{rollNo='" + rollNo + "', name='" + name + "', department='" + department + "', year=" + year + ", email='" + email + "', phone='" + phone + "', attendance=" + attendance + "}";
    }
}
