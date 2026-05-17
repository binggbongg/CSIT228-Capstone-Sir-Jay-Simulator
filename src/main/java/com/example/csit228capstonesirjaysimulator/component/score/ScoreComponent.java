package com.example.csit228capstonesirjaysimulator.component.score;

import com.almasb.fxgl.dsl.FXGL;
import com.example.csit228capstonesirjaysimulator.component.mission.Mission;
import javafx.application.Platform;

import java.util.List;

public class ScoreComponent {
    private static final Object LOCK = new Object();
    int mult = 1;
    final int success = 120, sharpEye = 267, fail = 150;

    public void updateScore(boolean sharpEyeBonus) {
        Platform.runLater(() -> {
            synchronized (LOCK) {
                int sc = FXGL.geti("score");
                mult = FXGL.geti("mult");
                if (sharpEyeBonus) FXGL.set("score", sc + (sharpEye * mult));
                else               FXGL.set("score", sc + (success * mult));
                onScoreUpdated(FXGL.geti("score"));
            }
        });
    }

    public void failToCatch() {
        Platform.runLater(() -> {
            synchronized (LOCK) {
                int sc = FXGL.geti("score");
                FXGL.set("score", sc - fail);
                FXGL.set("mult", 1);
            }
        });
    }

    public void wrongGuess() {
        Platform.runLater(() -> {
            synchronized (LOCK) {
                int lives = FXGL.geti("lives");
                FXGL.set("lives", lives - 1);
                FXGL.set("streak", 0);
                FXGL.set("mult", 1);
                onFalseAccusation();
            }
        });
    }

    public void correctGuess() {
        Platform.runLater(() -> {
            synchronized (LOCK) {
                int streak = FXGL.geti("streak");
                if      (streak > 67) FXGL.set("mult", 100);
                else if (streak > 10) FXGL.set("mult", 5);
                else if (streak > 7)  FXGL.set("mult", 4);
                else if (streak > 5)  FXGL.set("mult", 3);
                else if (streak > 3)  FXGL.set("mult", 2);
                FXGL.set("streak", streak + 1);
                onCheaterCaught();
                onStreakUpdated(FXGL.geti("streak"));
                onMultiplierUpdated(FXGL.geti("mult"));
            }
        });
    }


    private void onCheaterCaught() {
        FXGL.inc("sessionCheatersCaught", +1);
        FXGL.inc("sessionTotalAttempts",  +1);
        FXGL.inc("sessionTotalCorrect",   +1);
        updateMissionsOnCheaterCaught();
    }

    private void onFalseAccusation() {
        FXGL.inc("sessionFalseAccusations", +1);
        FXGL.inc("sessionTotalAttempts",    +1);
    }

    private void onStreakUpdated(int streak) {
        updateMissionsOnStreak(streak);
    }

    private void onScoreUpdated(int score) {
        updateMissionsOnScore(score);
    }

    private void onMultiplierUpdated(int mult) {
        updateMissionsOnMultiplier(mult);
    }


    public void distractorResolved() {
        Platform.runLater(() -> {
            synchronized (LOCK) {
                updateMissionsOnDistractorResolved();
            }
        });
    }
    private void updateMissionsOnDistractorResolved() {
        List<Mission<?>> missions = getMissions();
        if (missions == null) return;
        for (Mission<?> m : missions) {
            if (m.getMissionId() == 5)
                ((Mission<Integer>) m).increment();
        }
    }

    private void updateMissionsOnCheaterCaught() {
        List<Mission<?>> missions = getMissions();
        if (missions == null) return;
        for (Mission<?> m : missions) {
            if (m.getMissionId() == 1 || m.getMissionId() == 2)
                ((Mission<Integer>) m).increment();
        }
    }


    private void updateMissionsOnStreak(int streak) {
        List<Mission<?>> missions = getMissions();
        if (missions == null) return;
        for (Mission<?> m : missions) {
            if (m.getMissionId() == 3 || m.getMissionId() == 4)
                ((Mission<Integer>) m).setCurrent(streak);
        }
    }


    private void updateMissionsOnScore(int score) {
        List<Mission<?>> missions = getMissions();
        if (missions == null) return;
        for (Mission<?> m : missions) {
            if (m.getMissionId() == 6 || m.getMissionId() == 7)
                ((Mission<Integer>) m).setCurrent(score);
        }
    }


    private void updateMissionsOnMultiplier(int mult) {
        List<Mission<?>> missions = getMissions();
        if (missions == null) return;
        for (Mission<?> m : missions) {
            if (m.getMissionId() == 8 && mult >= 3)
                ((Mission<Boolean>) m).complete();
            else if (m.getMissionId() == 9 && mult >= 5)
                ((Mission<Boolean>) m).complete();
        }
    }


    private List<Mission<?>> getMissions() {
        try {
            return (List<Mission<?>>) FXGL.getWorldProperties().getObject("sessionMissions");
        } catch (Exception e) {
            return null;
        }
    }
}