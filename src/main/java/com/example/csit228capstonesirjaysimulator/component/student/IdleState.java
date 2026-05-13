package com.example.csit228capstonesirjaysimulator.component.student;

import com.almasb.fxgl.core.math.FXGLMath;

import com.almasb.fxgl.dsl.FXGL;
import com.example.csit228capstonesirjaysimulator.component.score.UpdateScoreRunnable;
import javafx.util.Duration;

public class IdleState extends StudentState {

    public IdleState(StudentComponent student){
        super(student);
        int cap = 30, sc = FXGL.geti("score");
        // adjust if score is greater than 5000
        if(sc > 20_000) cap = 15;
        else if(sc > 10_000) cap = 20;

        this.duration = Duration.seconds(FXGLMath.random(7, cap));
        //student.setHue(0);
        student.playIdleAnimation();
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
//        scoreComponent.wrongGuess();
        Thread t = new Thread(new UpdateScoreRunnable(scoreComponent,this));
        t.start();
    }
}
