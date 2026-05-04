package com.example.csit228capstonesirjaysimulator.element.entity;

public class TanawState extends StudentState{
    // random time for how long they are in this state
    // can have indicator for later on if this state gets shorter

    TanawState(Student s){
        super(s);
    }

    @Override
    public void handleRequest() {
        System.out.println("CURRENT STATE: Tanaw State");
        s.canBeHit = true;
    }

}
