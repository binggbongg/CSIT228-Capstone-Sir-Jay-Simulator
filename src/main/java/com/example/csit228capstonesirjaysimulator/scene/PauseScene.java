package com.example.csit228capstonesirjaysimulator.scene;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import com.example.csit228capstonesirjaysimulator.util.AudioManager;
import javafx.geometry.Pos;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PauseScene extends SubScene {

    public PauseScene() {
        AudioManager.getInstance().pauseMusic("chocolate-milk.mp3");

        Image bgImg = new Image("assets/textures/polka_dot_bg.jpg");
        Image resumeImg = new Image("assets/textures/button-resume.png");
        Image menuImg = new Image("assets/textures/button-back-to-menu.png");

        ImageView background = new ImageView(bgImg);
        background.setOpacity(.5);

        ImageView resumeBtn = new ImageView(resumeImg);
        resumeBtn.setFitWidth(200);
        resumeBtn.setPreserveRatio(true);
        resumeBtn.setOnMouseClicked(e -> {
            AudioManager.getInstance().resumeMusic("chocolate-milk.mp3");
            FXGL.getSceneService().popSubScene();
        });

        ImageView menuBtn = new ImageView(menuImg);
        menuBtn.setFitWidth(200);
        menuBtn.setPreserveRatio(true);
        menuBtn.setOnMouseClicked(e -> FXGL.getGameController().gotoMainMenu());

        VBox vbox = new VBox(20, resumeBtn, menuBtn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefSize(1280, 720);

        getContentRoot().getChildren().addAll(background, vbox);
    }
}
