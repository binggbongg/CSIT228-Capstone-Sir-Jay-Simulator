package com.example.csit228capstonesirjaysimulator.component.student;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import javafx.util.Duration;

public class DistractingState extends StudentState {
    private int clicksRequired = 5;

    public DistractingState(StudentComponent student){
        super(student);
       //this.duration = Duration.seconds(FXGLMath.random(2, 6));
        //student.setHue(0.5);

        student.playDistractAnimation();

        FXGL.getWorldProperties().setValue("isLocked", true);
    }

    @Override
    void onUpdate(double tpf) {
        // will not update unless player resolves current state
    }

    @Override
    void onAction() {
        clicksRequired--;
        System.out.println("Student assisted! Clicks remaining: " + clicksRequired);

        //getStudent().setHue(0.5 + (0.1 * (5 - clicksRequired)));

        if (clicksRequired <= 0) {
            System.out.println("Student satisfied. Room unlocked.");
            FXGL.getWorldProperties().setValue("isLocked", false); //
            playSound("correct.mp3"); //

            // Return to the distractor's specific idle state
            getStudent().changeState(new DistractIdleState(getStudent()));
        } else {
            playSound("tap.mp3"); // Short feedback sound
        }
    }
}
