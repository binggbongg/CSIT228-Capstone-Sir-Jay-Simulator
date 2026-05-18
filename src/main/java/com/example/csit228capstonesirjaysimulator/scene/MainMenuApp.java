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
import javafx.scene.effect.GaussianBlur;
import javafx.util.Duration;

public class MainMenuApp extends FXGLMenu {

    private ImageView bg, logoView, btnPlay, btnLeaderboard, btnExit;
    private SequentialTransition introAnimation;

    public MainMenuApp() {
        super(MenuType.MAIN_MENU);

        initUI();
        initListeners();
        initAnimations();

        introAnimation.play();
    }

    private void initUI() {
        bg = new ImageView(new Image("assets/textures/bg_main_menu.png"));
        bg.setFitHeight(FXGL.getAppHeight());
        bg.setFitWidth(FXGL.getAppWidth());

        logoView = new ImageView(new Image("assets/textures/logo.png"));
        logoView.setFitWidth(650);
        logoView.setPreserveRatio(true);
        logoView.setScaleX(0.0);
        logoView.setScaleY(0.0);

        btnPlay = createMenuButton("assets/textures/button_play.png");
        btnLeaderboard = createMenuButton("assets/textures/button_leaderboard.png");
        btnExit = createMenuButton("assets/textures/button_exit.png");

        HBox buttonBox = new HBox(20, btnLeaderboard, btnPlay, btnExit);
        buttonBox.setAlignment(Pos.CENTER);

        VBox centerLayout = new VBox(40, logoView, buttonBox);
        centerLayout.setAlignment(Pos.CENTER);

        StackPane rootStack = new StackPane(centerLayout);
        rootStack.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());

        getContentRoot().getChildren().addAll(bg, rootStack);
    }

    private ImageView createMenuButton(String path) {
        ImageView iv = new ImageView(new Image(path));
        iv.setFitWidth(200);
        iv.setPreserveRatio(true);
        iv.setVisible(false);
        return iv;
    }

    private void initListeners() {
        btnPlay.setOnMouseClicked(e -> {
            // using another thread to check on the status of the database to minimize ui lag
            new Thread(() -> {
                boolean online = DatabaseConnection.checkStatus();
                Platform.runLater(() -> {
                    if (online) {
                        AudioManager.getInstance().stopMusic("classroom.wav");
                        btnPlay.getParent().requestFocus();

                        FXGL.getSceneService().pushSubScene(new ProfileSelectScene(getContentRoot(), () -> {
                            IntroCutScene cutScene = new IntroCutScene(() -> fireNewGame());
                            FXGL.getSceneService().pushSubScene(cutScene);
                        }));
                    } else {
                        FXGL.getSceneService().pushSubScene(new DatabaseErrorScene());
                    }
                });
            }).start();
        });

        btnLeaderboard.setOnMouseClicked(e -> {
            // using another thread to check on the status of the database to minimize ui lag
            new Thread(() -> {
                boolean online = DatabaseConnection.checkStatus();
                Platform.runLater(() -> {
                    if (online) {
                        getContentRoot().setEffect(new GaussianBlur(50));
                        FXGL.getSceneService().pushSubScene(new LeaderboardScene(getContentRoot()));
                    } else {
                        FXGL.getSceneService().pushSubScene(new DatabaseErrorScene());
                    }
                });
            }).start();
        });

        btnExit.setOnMouseClicked(e -> fireExit());
    }

    private void initAnimations() {
        ScaleTransition grow = new ScaleTransition(Duration.seconds(0.8), logoView);
        grow.setFromX(0.0);
        grow.setFromY(0.0);
        grow.setToX(1.0);
        grow.setToY(1.0);

        ScaleTransition bounce = new ScaleTransition(Duration.seconds(0.6), logoView);
        bounce.setToX(1.2);
        bounce.setToY(1.2);
        bounce.setAutoReverse(true);
        bounce.setCycleCount(4);
        bounce.setInterpolator(Interpolator.EASE_OUT);

        introAnimation = new SequentialTransition(grow, bounce);
        introAnimation.setOnFinished(e -> {
            btnPlay.setVisible(true);
            btnLeaderboard.setVisible(true);
            btnExit.setVisible(true);
        });

        Music music = FXGL.getAssetLoader().loadMusic("intro_music.wav");
        FXGL.getAudioPlayer().playMusic(music);
    }
}