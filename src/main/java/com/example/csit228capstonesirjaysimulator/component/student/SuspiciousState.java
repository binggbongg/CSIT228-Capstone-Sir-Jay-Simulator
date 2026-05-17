package com.example.csit228capstonesirjaysimulator.component.student;

import com.almasb.fxgl.core.math.FXGLMath;
import com.example.csit228capstonesirjaysimulator.component.score.UpdateScoreRunnable;
import com.example.csit228capstonesirjaysimulator.util.AudioManager;
import javafx.util.Duration;

public class SuspiciousState extends StudentState {

    public SuspiciousState(StudentComponent student){
        super(student);
        this.duration = Duration.seconds(FXGLMath.random(5, 10));
        student.playSuspiciousAnimation();
    }

    @Override
    public void onUpdate(double tpf) {
        if(timer.elapsed(this.duration)){
            getStudent().changeState(new CheatingState(getStudent()));
        }
    }

    @Override
    public void onAction() {
        AudioManager.getInstance().playSound("mahbadmahbad2.wav");
        //Create a new thread to update the score
        Thread t = new Thread(new UpdateScoreRunnable(scoreComponent,this));
        t.start();

        getStudent().changeState(new IdleState(getStudent()));
    }
}
