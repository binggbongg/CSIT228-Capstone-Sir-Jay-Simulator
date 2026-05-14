package com.example.csit228capstonesirjaysimulator.scene;

import com.almasb.fxgl.dsl.FXGL;
import com.example.csit228capstonesirjaysimulator.component.mission.Mission;
import com.example.csit228capstonesirjaysimulator.database.Sessionstats;
import com.example.csit228capstonesirjaysimulator.database.UserDatabaseService;
import com.example.csit228capstonesirjaysimulator.database.UserProfile;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import com.almasb.fxgl.scene.SubScene;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

public class FinishScene extends SubScene {

    public FinishScene() {
        buildUI();
        persistSessionAsync();
    }
    
    private void buildUI() {
        // Initial Background
        Image imgBg1 = new Image("assets/textures/game-over-bg-1.png", 1280, 0, true, true);
        ImageView bg1 = new ImageView(imgBg1);
        bg1.setTranslateY((720 - imgBg1.getHeight()) / 2.0 + 50);

        // Fade in Background 2
        Image imgBg2 = new Image("assets/textures/game-over-bg-2.png", 1280, 0, true, true);
        ImageView bg2 = new ImageView(imgBg2);
        bg2.setTranslateY((720 - imgBg2.getHeight()) / 2.0 + 50);
        bg2.setOpacity(0);

        FadeTransition fadeBg2 = new FadeTransition(Duration.seconds(3), bg2);
        fadeBg2.setFromValue(0);
        fadeBg2.setToValue(1);
        fadeBg2.play();

        // Pull down Background 3
        Image imgBg3 = new Image("assets/textures/game-over-bg-3.png", 1280, 0, true, true);
        ImageView bg3 = new ImageView(imgBg3);
        bg3.setTranslateX(-300);
        bg3.setTranslateY(-imgBg3.getHeight());

        TranslateTransition pullDownBg3 = new TranslateTransition(Duration.seconds(1), bg3);
        pullDownBg3.setFromY(-imgBg3.getHeight());
        pullDownBg3.setToY(720 - imgBg3.getHeight());
        pullDownBg3.setDelay(Duration.seconds(3));
        pullDownBg3.play();

        // Fonts
        Font jelleeHeader = Font.loadFont(getClass().getResourceAsStream("/fonts/Jellee-Roman.ttf"), 64);
        Font jelleeText = Font.loadFont(getClass().getResourceAsStream("/fonts/Jellee-Roman.ttf"), 25);
        Font jelleeScore = Font.loadFont(getClass().getResourceAsStream("/fonts/Jellee-Roman.ttf"), 32);

        if (jelleeHeader == null) jelleeHeader = Font.font("Monospaced", FontWeight.BOLD, 64);
        if (jelleeText == null) jelleeText = Font.font("Monospaced", FontWeight.NORMAL, 25);
        if (jelleeScore == null) jelleeScore = Font.font("Monospaced", FontWeight.NORMAL, 32);

        Text header = new Text("GAME OVER");
        header.setFill(Color.WHITE);
        header.setStroke(Color.rgb(242, 142, 2));
        header.setStrokeWidth(10);
        header.setStrokeType(StrokeType.OUTSIDE);
        header.setFont(jelleeHeader);

        Text scoreTextTitle = new Text("SCORE");
        scoreTextTitle.setFill(Color.WHITE);
        scoreTextTitle.setFont(jelleeText);

        int score = FXGL.geti("score");
        Text scoreText = new Text("" + score);
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(jelleeScore);

        // Retry Button
        Image imgBtnRetry = new Image("assets/textures/button-retry.png");
        ImageView btnRetry = new ImageView(imgBtnRetry);
        btnRetry.setFitWidth(200);
        btnRetry.setPreserveRatio(true);
        btnRetry.setCursor(Cursor.HAND);
        btnRetry.setOnMouseClicked(e -> FXGL.getGameController().startNewGame());

        // Leaderboard Button
        Image imgBtnLeaderboard = new Image("assets/textures/button-leaderboard.png");
        ImageView btnLeaderboard = new ImageView(imgBtnLeaderboard);
        btnLeaderboard.setFitWidth(200);
        btnLeaderboard.setPreserveRatio(true);
        btnLeaderboard.setCursor(Cursor.HAND);
        btnLeaderboard.setOnMouseClicked(e -> FXGL.getSceneService().pushSubScene(new LeaderboardScene(getContentRoot())));

        // Back to Menu Button
        Image imgBtnMenu = new Image("assets/textures/button-back-to-menu.png");
        ImageView btnMenu = new ImageView(imgBtnMenu);
        btnMenu.setFitWidth(200);
        btnMenu.setPreserveRatio(true);
        btnMenu.setCursor(Cursor.HAND);
        btnMenu.setOnMouseClicked(e -> FXGL.getGameController().gotoMainMenu());

        javafx.scene.layout.HBox buttonsBox = new javafx.scene.layout.HBox(30, btnRetry, btnLeaderboard, btnMenu);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setTranslateY(120);

        VBox vbox = new VBox(30, header, scoreTextTitle, scoreText, buttonsBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefSize(1280, 720);

        getContentRoot().getChildren().addAll(bg1, bg2, bg3, vbox);
    }

    @SuppressWarnings("unchecked")
    private void persistSessionAsync() {
        UserProfile profile = null;
        try {
            profile = UserDatabaseService.getInstance().getCurrentUser();
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
            System.out.println("Saving session for " + studentId + " …");
            UserDatabaseService.getInstance().saveSession(studentId, ss, finalMissions);
            System.out.println("Session saved.");
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
