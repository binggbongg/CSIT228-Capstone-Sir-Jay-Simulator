package com.example.csit228capstonesirjaysimulator.database;

import java.util.Objects;


public class UserProfile {

    private final String studentId, username, course, section, password;

    public UserProfile(String studentId, String username, String course,   String section, String password) {
        this.studentId = studentId;
        this.username = username;
        this.course= course;
        this.section = section;
        this.password= password;
    }


    public String getStudentId(){ return studentId;}
    public String getUsername() { return username;}
    public String getCourse(){ return course;}
    public String getSection() { return section;}
    public String getPassword(){ return password;}


    public boolean checkPassword(String input) {
        return password != null && password.equals(input);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProfile that)) return false;
        return Objects.equals(studentId, that.studentId);
    }

    @Override public int hashCode() { return Objects.hash(studentId); }

    @Override
    public String toString() {
        return username + " [" + studentId + "] " + course + "-" + section;
    }
}