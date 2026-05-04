package com.example.csit228capstonesirjaysimulator.element.entity;

public class StudyingState extends StudentState {

    StudyingState(Student s){
        super(s);
    }

    @Override
    public void handleRequest() {
        System.out.println("CURRENT STATE: Studying State");
        s.canBeHit = false;
    }
}
