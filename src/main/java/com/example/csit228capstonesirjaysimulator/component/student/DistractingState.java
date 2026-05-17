package com.example.csit228capstonesirjaysimulator.component.student;

import com.almasb.fxgl.dsl.FXGL;
import com.example.csit228capstonesirjaysimulator.util.AudioManager;

public class DistractingState extends StudentState {
    private int clicksRequired = 5;

    public DistractingState(StudentComponent student){
        super(student);
        student.setHue(0.2);

        student.playDistractAnimation();
        AudioManager.getInstance().playMusic("distractor_sfx.wav");

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

        if (clicksRequired <= 0) {
            System.out.println("Student satisfied. Room unlocked.");
            FXGL.getWorldProperties().setValue("isLocked", false);
            AudioManager.getInstance().playSound("anywho1.wav");
            AudioManager.getInstance().stopMusic("distractor_sfx.wav");

            scoreComponent.distractorResolved();

            // Return to the distractor's specific idle state
            getStudent().changeState(new DistractIdleState(getStudent()));
        } else {
            AudioManager.getInstance().playSound("tap.mp3");
        }
    }
}
