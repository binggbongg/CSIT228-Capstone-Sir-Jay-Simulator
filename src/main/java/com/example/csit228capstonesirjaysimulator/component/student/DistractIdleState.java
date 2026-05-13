package com.example.csit228capstonesirjaysimulator.component.student;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.example.csit228capstonesirjaysimulator.component.score.UpdateScoreRunnable;
import javafx.util.Duration;

public class DistractIdleState extends StudentState {
    public DistractIdleState(StudentComponent student){
        super(student);
        this.duration = Duration.seconds(FXGLMath.random(5, 15));
//        student.setHue(0);
        student.playIdleAnimation();
    }

    @Override
    public void onUpdate(double tpf) {
        if (timer.elapsed(this.duration)) {
            boolean studentIsOnRight = getStudent().getEntity().getProperties().getBoolean("isRightSide");
            boolean currentViewIsRight = FXGL.getb("isRight");

            if (studentIsOnRight == currentViewIsRight) {
                getStudent().changeState(new DistractingState(getStudent()));
            } else {
                this.duration = Duration.seconds(FXGLMath.random(2, 5));
                timer.capture();
            }
        }
    }

    @Override
    void onAction() {
        System.out.println("Distractor just idle..");
        super.playSound("wrong.mp3");
        Thread t = new Thread(new UpdateScoreRunnable(scoreComponent,this));
        t.start();
    }
}
