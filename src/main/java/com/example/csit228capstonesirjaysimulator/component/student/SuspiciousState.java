package com.example.csit228capstonesirjaysimulator.component.student;

import com.almasb.fxgl.core.math.FXGLMath;
import javafx.util.Duration;

public class SuspiciousState extends StudentState {

    public SuspiciousState(StudentComponent student){
        super(student);
        this.duration = Duration.seconds(FXGLMath.random(3, 7));
        student.setHue(0.2); // rainbowdash turn purple ish idk
    }

    @Override
    public void onUpdate(double tpf) {
        if(timer.elapsed(this.duration)){
            getStudent().changeState(new CheatingState(getStudent()));
        }
    }

    @Override
    public void onAction() {
        System.out.println("student not cheating! state is idle");
        super.playSound("wrong.mp3");

        // change back to idle state
        getStudent().changeState(new IdleState(getStudent()));
    }
}
