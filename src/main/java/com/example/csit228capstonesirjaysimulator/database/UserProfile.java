package com.example.csit228capstonesirjaysimulator.database;

import java.util.Objects;

public class UserProfile {

    private final String teacherId, username, course, section, password;

    public UserProfile(String teacherId, String username, String course, String section, String password) {
        this.teacherId = teacherId;
        this.username  = username;
        this.course    = course;
        this.section   = section;
        this.password  = password;
    }

    public String getTeacherId() { return teacherId; }
    public String getUsername()  { return username;  }
    public String getCourse()    { return course;    }
    public String getSection()   { return section;   }
    public String getPassword()  { return password;  }

    public boolean checkPassword(String input) {
        return password != null && password.equals(input);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProfile that)) return false;
        return Objects.equals(teacherId, that.teacherId);
    }

    @Override public int hashCode() { return Objects.hash(teacherId); }

    @Override
    public String toString() {
        return username + " [" + teacherId + "] " + course + "-" + section;
    }
}