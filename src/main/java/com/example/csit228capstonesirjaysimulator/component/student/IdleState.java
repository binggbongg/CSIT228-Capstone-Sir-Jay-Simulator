package com.example.csit228capstonesirjaysimulator.component.student;

import com.almasb.fxgl.core.math.FXGLMath;

import javafx.util.Duration;

public class IdleState extends StudentState {

    public IdleState(StudentComponent student){
        super(student);
        this.duration = Duration.seconds(FXGLMath.random(7, 12));
        student.setHue(0);
    }

    @Override
    public void onUpdate(double tpf) {
        if(timer.elapsed(this.duration)){
            getStudent().changeState(new SuspiciousState(getStudent()));
        }
    }

    @Override
    public void onAction() {
        System.out.println("student not cheating! state is idle");
        super.playSound("wrong.mp3");
    }
}
