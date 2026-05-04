package com.example.csit228capstonesirjaysimulator.element.entity;

public class Student {
    StudentContext context;
    boolean canBeHit;

    public Student(){
        canBeHit = false;
        context = new StudentContext(this);
    }

    public void stateChange(){
        // logic should be here for how long the student is vuln
    }

}
