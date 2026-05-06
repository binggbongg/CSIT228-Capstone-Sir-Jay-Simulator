package com.example.csit228capstonesirjaysimulator.component.score;

import com.almasb.fxgl.dsl.FXGL;

public class ScoreComponent {
    int mult = 1;
    final int success = 120, sharpEye = 200;

    public void updateScore(boolean sharpEyeBonus) {
        int sc = FXGL.geti("score");
        mult = FXGL.geti("mult");
        if(sharpEyeBonus) FXGL.set("score", sc + (sharpEye * mult));
        else FXGL.set("score", sc + (success * mult));
    }
    public void wrongGuess(){
        int lives = FXGL.geti("lives");
        FXGL.set("lives", lives-1);
        FXGL.set("streak", 0);
        FXGL.set("mult", 1);
    }

    public void correctGuess(){
        int streak = FXGL.geti("streak");
        mult = FXGL.geti("mult");
        if(streak > 5){
            FXGL.set("mult", mult+1);
        }
        FXGL.set("streak", streak+1);
    }
}
