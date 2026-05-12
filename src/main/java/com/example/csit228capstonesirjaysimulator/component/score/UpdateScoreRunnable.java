package com.example.csit228capstonesirjaysimulator.component.score;

import com.example.csit228capstonesirjaysimulator.component.student.CheatingState;
import com.example.csit228capstonesirjaysimulator.component.student.StudentState;

public class UpdateScoreRunnable implements Runnable{
    private ScoreComponent scoreComponent;
    private StudentState state;

    public UpdateScoreRunnable(ScoreComponent sc, StudentState state){
        scoreComponent = sc;
        this.state = state;
    }
    @Override
    public void run() {
        if(state instanceof CheatingState){
            scoreComponent.correctGuess();
            scoreComponent.updateScore(((CheatingState) state).sharpEyeBonus);
        }else{
            scoreComponent.wrongGuess();
        }
    }
}
