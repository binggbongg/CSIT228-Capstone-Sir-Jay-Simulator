package com.example.csit228capstonesirjaysimulator;

import com.almasb.fxgl.app.GameApplication;
import com.example.csit228capstonesirjaysimulator.application.GameLevelApp;
import com.example.csit228capstonesirjaysimulator.database.DatabaseConnection;

public class SirSeratoSimulator {
    public static void main(String[] args) {

        DatabaseConnection.initialize();
        GameApplication.launch(GameLevelApp.class, args);
    }
}
