package com.studentportal.model;

public class Mark {
    private int id;
    private String rollNo;
    private String subjectName;
    private int marks;

    public Mark() {}

    public Mark(int id, String rollNo, String subjectName, int marks) {
        this.id = id;
        this.rollNo = rollNo;
        this.subjectName = subjectName;
        this.marks = marks;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public int getMarks() { return marks; }
    public void setMarks(int marks) { this.marks = marks; }

    @Override
    public String toString() {
        return "Mark{id=" + id + ", rollNo='" + rollNo + "', subjectName='" + subjectName + "', marks=" + marks + "}";
    }
}
