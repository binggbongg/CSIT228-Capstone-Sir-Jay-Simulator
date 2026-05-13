package com.example.csit228capstonesirjaysimulator.scene;

import com.almasb.fxgl.dsl.FXGL;
import com.example.csit228capstonesirjaysimulator.component.mission.Mission;
import com.example.csit228capstonesirjaysimulator.database.Sessionstats;
import com.example.csit228capstonesirjaysimulator.database.UserDatabaseService;
import com.example.csit228capstonesirjaysimulator.database.UserProfile;
import javafx.geometry.Pos;
import com.almasb.fxgl.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class FinishScene extends SubScene {

    public FinishScene() {
        buildUI();
        persistSessionAsync();
    }
    private void buildUI() {
        Rectangle bg = new Rectangle(1280, 720, Color.BLACK);

        Text header = new Text("GAME OVER");
        header.setFill(Color.RED);
        header.setFont(Font.font("Monospaced", FontWeight.BOLD, 48));

        int score = FXGL.geti("score");
        Text scoreText = new Text("Final Score: " + score);
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font("Monospaced", FontWeight.NORMAL, 24));

        Button btnMenu = new Button("RETURN TO MENU");
        btnMenu.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        btnMenu.setTextFill(Color.BLACK);
        btnMenu.setStyle("-fx-background-color: gold; -fx-padding: 12 32 12 32;");

        btnMenu.setOnAction(e -> FXGL.getGameController().gotoMainMenu());

        VBox vbox = new VBox(30, header, scoreText, btnMenu);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefSize(1280, 720);

        getContentRoot().getChildren().addAll(bg, vbox);
    }

    @SuppressWarnings("unchecked")
    private void persistSessionAsync() {
        UserProfile profile = null;
        try {
            profile = (UserProfile) FXGL.getWorldProperties().getObject("activeProfile");
        } catch (Exception ignored) { }

        if (profile == null) {
            System.out.println("No active profile");
            return;
        }

        final String studentId = profile.getStudentId();


        Sessionstats ss = buildSessionStats();


        List<Mission<?>> missions = null;
        try {
            missions = (List<Mission<?>>) FXGL.getWorldProperties().getObject("sessionMissions");
        } catch (Exception ignored) {}

        final List<Mission<?>> finalMissions = (missions != null) ? missions : List.of();


        new Thread(() -> {
            System.out.println("[FinishScene] Saving session for " + studentId + " …");
            UserDatabaseService.getInstance().saveSession(studentId, ss, finalMissions);
            System.out.println("[FinishScene] Session saved.");
        }, "db-save-thread").start();
    }

    private Sessionstats buildSessionStats() {
        Sessionstats ss = new Sessionstats();
        ss.setFinalScore(FXGL.geti("score"));

        try { for (int i = 0; i < FXGL.geti("sessionCheatersCaught");   i++) ss.incrementCheatersCaught();   } catch (Exception ignored) {}
        try { for (int i = 0; i < FXGL.geti("sessionFalseAccusations"); i++) ss.incrementFalseAccusations(); } catch (Exception ignored) {}
        try { ss.updateHighestCombo(FXGL.geti("streak")); } catch (Exception ignored) {}


        try { ss.setDurationSeconds(FXGL.getd("sessionDuration")); } catch (Exception ignored) {}

 
        try { for (int i = 0; i < FXGL.geti("sessionTotalAttempts"); i++) ss.incrementAttempts(); } catch (Exception ignored) {}
        try { for (int i = 0; i < FXGL.geti("sessionTotalCorrect");  i++) ss.incrementCorrect();  } catch (Exception ignored) {}

        return ss;
    }

}
