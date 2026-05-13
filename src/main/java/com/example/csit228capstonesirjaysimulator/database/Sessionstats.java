package com.example.csit228capstonesirjaysimulator.database;

public class Sessionstats {

    private int    finalScore = 0;
    private int    cheatersCaught = 0;
    private int    falseAccusations = 0;
    private int    highestCombo = 0;
    private int    totalAttempts = 0;
    private int    totalCorrect = 0;
    private double durationSeconds = 0.0;

    public void setFinalScore(int v){ finalScore= v; }
    public void incrementCheatersCaught(){ cheatersCaught++;}
    public void incrementFalseAccusations(){falseAccusations++;}
    public void updateHighestCombo(int combo){
        if (combo > highestCombo) highestCombo = combo;
    }
    public void incrementAttempts(){ totalAttempts++;}
    public void incrementCorrect(){ totalCorrect++;}
    public void setDurationSeconds(double d){ durationSeconds = d;}

    public int    getFinalScore(){ return finalScore;}
    public int    getCheatersCaught(){ return cheatersCaught;}
    public int    getFalseAccusations(){ return falseAccusations;}
    public int    getHighestCombo(){ return highestCombo;}
    public int    getTotalAttempts(){ return totalAttempts;}
    public int    getTotalCorrect(){ return totalCorrect;}
    public double getDurationSeconds(){ return durationSeconds;}

    public double getCatchAccuracy() {
        return totalAttempts == 0 ? 0.0 : (double) totalCorrect / totalAttempts;
    }
}
