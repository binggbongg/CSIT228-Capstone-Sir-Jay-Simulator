package com.example.csit228capstonesirjaysimulator.component.student;

import com.almasb.fxgl.dsl.FXGL;
import com.example.csit228capstonesirjaysimulator.util.AudioManager;

public class DistractingState extends StudentState {
    private int clicksRequired = 5;

    public DistractingState(StudentComponent student){
        super(student);
        student.setHue(0.2);

        student.playDistractAnimation();
//        Music s = FXGL.getAssetLoader().loadMusic("distractor_sfx.wav");
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

        //getStudent().setHue(0.5 + (0.1 * (5 - clicksRequired)));

        if (clicksRequired <= 0) {
            System.out.println("Student satisfied. Room unlocked.");
            FXGL.getWorldProperties().setValue("isLocked", false);
//            FXGL.getAudioPlayer().playSound(FXGL.getAssetLoader().loadSound("correct.mp3"));
            AudioManager.getInstance().playSound("anywho1.wav");

            FXGL.getAudioPlayer().stopAllMusic();
            // Return to the distractor's specific idle state
            getStudent().changeState(new DistractIdleState(getStudent()));
        } else {
            AudioManager.getInstance().playSound("tap.mp3");
        }
    }
}
