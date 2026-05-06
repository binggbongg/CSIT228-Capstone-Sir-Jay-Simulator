package com.example.csit228capstonesirjaysimulator.component.student;

import com.almasb.fxgl.core.math.FXGLMath;
import javafx.util.Duration;

public class CheatingState extends StudentState{

    public CheatingState(StudentComponent student){
        super(student);
        this.duration = Duration.seconds(FXGLMath.random(2, 6));
        student.setHue(0.7); // rainbowdash turn pink
    }

    @Override
    public void onUpdate(double tpf) {
        if(timer.elapsed(duration)){
            getStudent().changeState(new IdleState(getStudent()));
        }
    }

    @Override
    public void onAction() {
        System.out.println("yes this is a cheater!! Not Good");
        super.playSound("correct.mp3");
        // later on add mechanics for sharpEyeBonus
        scoreComponent.correctGuess();
        scoreComponent.updateScore(false);
        // reset it to idle
        getStudent().changeState(new IdleState(getStudent()));
    }
}
