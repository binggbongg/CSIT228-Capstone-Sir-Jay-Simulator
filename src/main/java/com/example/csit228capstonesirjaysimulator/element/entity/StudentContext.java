package com.example.csit228capstonesirjaysimulator.element.entity;

public class StudentContext {
    // this class handles the changing only
    Student s;
    protected StudentState state;

    public StudentContext(Student s) {
        this.s = s;
    }

    public void setStudying(){
        setState(new StudyingState(s));
    }

    public void setTanawing(){
        setState(new TanawState(s));
    }

    private void setState(StudentState state){
        this.state = state;
    }

    private void request(){
        state.handleRequest();
    }
}
