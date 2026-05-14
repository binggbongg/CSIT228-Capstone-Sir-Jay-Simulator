package com.example.csit228capstonesirjaysimulator.component.student;

import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.dsl.FXGL;

public class DistractingState extends StudentState {
    private int clicksRequired = 5;

    public DistractingState(StudentComponent student){
        super(student);
//        student.setHue(0.5);

        student.playDistractAnimation();
        // sound indicator for distractor (for now)
        Music s = FXGL.getAssetLoader().loadMusic("distractor_sfx.wav");
        FXGL.getAudioPlayer().loopMusic(s);

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
            FXGL.getAudioPlayer().playSound(FXGL.getAssetLoader().loadSound("correct.mp3")); //

            FXGL.getAudioPlayer().stopAllMusic();
            // Return to the distractor's specific idle state
            getStudent().changeState(new DistractIdleState(getStudent()));
        } else {
            FXGL.getAudioPlayer().playSound(FXGL.getAssetLoader().loadSound("tap.mp3")); // Short feedback sound
        }
    }
}
