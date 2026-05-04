package com.example.csit228capstonesirjaysimulator.component.student;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.time.LocalTimer;

import javafx.util.Duration;

public abstract class StudentState {
    private StudentComponent student;
    protected LocalTimer timer;
    protected Duration duration;

    public StudentState(StudentComponent student){
        this.student = student;
        this.timer = FXGL.newLocalTimer();
        this.timer.capture();
    }

    public StudentComponent getStudent(){
        return student;
    }

    abstract void onUpdate(double tpf);
    abstract void onAction();

    public void playSound(String name){
        FXGL.getAudioPlayer().playSound(FXGL.getAssetLoader().loadSound(name));
    }
}
