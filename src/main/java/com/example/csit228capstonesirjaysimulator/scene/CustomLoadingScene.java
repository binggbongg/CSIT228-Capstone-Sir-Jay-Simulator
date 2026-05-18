package com.example.csit228capstonesirjaysimulator.scene;

import com.almasb.fxgl.app.scene.LoadingScene;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * overrides the default loading screen and adds a custom one
 */

public class CustomLoadingScene extends LoadingScene {
    public CustomLoadingScene(){

        // initializes the background and the logo
        ImageView bg = new ImageView(new Image("assets/textures/bg_polka_dot.jpg"));
        ImageView logo = new ImageView(new Image("assets/textures/sir_serato_icon.png"));
        logo.setFitWidth(300);
        logo.setPreserveRatio(true);

        // animates logo side to side
        RotateTransition sway = new RotateTransition(Duration.seconds(1.0), logo);
        sway.setFromAngle(-15);
        sway.setToAngle(15);
        sway.setCycleCount(Animation.INDEFINITE);
        sway.setAutoReverse(true);
        sway.setInterpolator(Interpolator.EASE_BOTH);

        sway.play();

        VBox vbox = new VBox(logo);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefSize(1280, 720);

        getContentRoot().getChildren().addAll(bg, vbox);
    }
}
