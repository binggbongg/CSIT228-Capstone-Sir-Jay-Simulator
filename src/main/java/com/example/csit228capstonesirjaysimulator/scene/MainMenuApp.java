package com.example.csit228capstonesirjaysimulator.scene;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.dsl.FXGL;
import com.example.csit228capstonesirjaysimulator.database.DatabaseConnection;
import com.example.csit228capstonesirjaysimulator.util.AudioManager;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MainMenuApp extends FXGLMenu {
    public MainMenuApp(){
        super(MenuType.MAIN_MENU);

        Image image = new Image("assets/textures/bg_main_menu.png");
        ImageView bg = new ImageView(image);
        bg.setFitHeight(FXGL.getAppHeight());
        bg.setFitWidth(FXGL.getAppWidth());

        Image logo = new Image("assets/textures/logo.png");
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(650);
        logoView.setPreserveRatio(true);

        getContentRoot().getChildren().add(bg);

        Image playImg = new Image("assets/textures/button_play.png");
        Image leaderboardImg = new Image("assets/textures/button_leaderboard.png");
        Image exitImg = new Image("assets/textures/button_exit.png");

        ImageView btnPlay = new ImageView(playImg);
        ImageView btnLeaderboard = new ImageView(leaderboardImg);
        ImageView btnExit = new ImageView(exitImg);

        btnPlay.setFitWidth(200);
        btnPlay.setPreserveRatio(true);
        btnLeaderboard.setFitWidth(200);
        btnLeaderboard.setPreserveRatio(true);
        btnExit.setFitWidth(200);
        btnExit.setPreserveRatio(true);

        btnPlay.setVisible(false);
        btnLeaderboard.setVisible(false);
        btnExit.setVisible(false);

        btnPlay.setOnMouseClicked( e -> {
            AudioManager.getInstance().stopMusic("classroom.wav");
            btnPlay.getParent().requestFocus();

            FXGL.getSceneService().pushSubScene(new ProfileSelectScene(getContentRoot(), () -> {
                IntroCutScene cutScene = new IntroCutScene(() -> fireNewGame());
                FXGL.getSceneService().pushSubScene(cutScene);
            }));

        });

        btnLeaderboard.setOnMouseClicked(e -> {

            // Running database status check in a background thread to avoid blocking the UI
            // Using Platform.runLater to safely update the scene once the check completes
            new Thread(() -> {
                if (DatabaseConnection.checkStatus()) {
                    Platform.runLater(() -> {
                        FXGL.getSceneService().pushSubScene(new LeaderboardScene(getContentRoot()));
                    });
                } else {
                    Platform.runLater(() -> {
                        FXGL.getSceneService().pushSubScene(new DatabaseErrorScene());
                    });
                }
            }).start();
        });

        btnExit.setOnMouseClicked(e -> fireExit());

        HBox menuBox = new HBox(20, btnLeaderboard, btnPlay, btnExit);
        menuBox.setAlignment(Pos.CENTER);

        VBox rootBox = new VBox(40, logoView, menuBox);
        rootBox.setAlignment(Pos.CENTER);

        StackPane stack = new StackPane(rootBox);
        stack.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());
        stack.setAlignment(Pos.CENTER);

        getContentRoot().getChildren().add(stack);

        logoView.setScaleX(0.0);
        logoView.setScaleY(0.0);

        ScaleTransition grow = new ScaleTransition(Duration.seconds(0.8), logoView);
        grow.setFromX(0.0);
        grow.setFromY(0.0);
        grow.setToX(1.0);
        grow.setToY(1.0);
        grow.setInterpolator(Interpolator.LINEAR);

        ScaleTransition bounce = new ScaleTransition(Duration.seconds(.6), logoView);
        bounce.setFromX(1.0);
        bounce.setFromY(1.0);
        bounce.setToX(1.2);
        bounce.setToY(1.2);
        bounce.setAutoReverse(true);
        bounce.setCycleCount(4);
        bounce.setInterpolator(Interpolator.EASE_OUT);

        SequentialTransition intro = new SequentialTransition(grow, bounce);

        Music music = FXGL.getAssetLoader().loadMusic("intro_music.wav");

        intro.setOnFinished(e -> {
            btnPlay.setVisible(true);
            btnLeaderboard.setVisible(true);
            btnExit.setVisible(true);
        });

        FXGL.getAudioPlayer().playMusic(music);

        intro.play();
    }
}
