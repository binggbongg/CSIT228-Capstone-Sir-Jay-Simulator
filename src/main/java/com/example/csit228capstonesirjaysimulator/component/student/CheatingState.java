package com.example.csit228capstonesirjaysimulator.component.student;

import com.almasb.fxgl.core.math.FXGLMath;
import com.example.csit228capstonesirjaysimulator.component.score.UpdateScoreRunnable;
import javafx.util.Duration;

public class CheatingState extends StudentState{
    public boolean sharpEyeBonus = true;
    Duration bonus = Duration.seconds(1.5);
    public CheatingState(StudentComponent student){
        super(student);
        this.duration = Duration.seconds(FXGLMath.random(5, 9));
        //student.setHue(0.7); // rainbowdash turn pink
        student.playCheatingAnimation();
    }

    @Override
    public void onUpdate(double tpf) {
        if(timer.elapsed(bonus)) sharpEyeBonus = false;
        if(timer.elapsed(duration)){
            scoreComponent.failToCatch();
            getStudent().changeState(new IdleState(getStudent()));
        }
    }

    @Override
    public void onAction() {
        System.out.println("yes this is a cheater!! Not Good");
        super.playSound("correct.mp3");
        // later on add mechanics for sharpEyeBonus
//        scoreComponent.correctGuess();
//        scoreComponent.updateScore(sharpEyeBonus);
        Thread t = new Thread(new UpdateScoreRunnable(scoreComponent,this));
        t.start();
        // reset it to idle
        getStudent().changeState(new IdleState(getStudent()));
    }
}
