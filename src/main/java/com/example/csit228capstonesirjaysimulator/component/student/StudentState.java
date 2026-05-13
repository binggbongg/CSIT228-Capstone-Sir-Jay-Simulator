package com.example.csit228capstonesirjaysimulator.component.student;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.time.LocalTimer;

import com.example.csit228capstonesirjaysimulator.application.GameLevelApp;
import com.example.csit228capstonesirjaysimulator.component.score.ScoreComponent;
import javafx.util.Duration;

public abstract class StudentState {
    private StudentComponent student;
    protected LocalTimer timer;
    protected Duration duration;
    protected ScoreComponent scoreComponent;

    public StudentState(StudentComponent student){
        this.student = student;
        this.timer = FXGL.newLocalTimer();
        this.timer.capture();
        this.scoreComponent = new ScoreComponent();
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
