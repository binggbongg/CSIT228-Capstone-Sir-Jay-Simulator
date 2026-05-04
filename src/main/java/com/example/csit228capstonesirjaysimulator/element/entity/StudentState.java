package com.example.csit228capstonesirjaysimulator.element.entity;

public abstract class StudentState {
    //this class handles the actual behavior and changes
    Student s;

    StudentState(Student s) {
        this.s = s;
    }

    public abstract void handleRequest();
}
