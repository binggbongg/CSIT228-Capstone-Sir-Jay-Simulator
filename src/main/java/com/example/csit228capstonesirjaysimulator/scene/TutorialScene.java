package com.example.csit228capstonesirjaysimulator.scene;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TutorialScene extends SubScene {

    public TutorialScene() {
        Rectangle bg = new Rectangle(1280, 720, Color.color(0, 0, 0, 0.5));

        Image tutorialImg = new Image("assets/textures/tutorial-bg.png");
        ImageView tutorialView = new ImageView(tutorialImg);
        
        tutorialView.setFitHeight(600);
        tutorialView.setPreserveRatio(true);

        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(bg, tutorialView);
        root.setPrefSize(1280, 720);

        root.setOnMouseClicked(e -> FXGL.getSceneService().popSubScene());

        getContentRoot().getChildren().add(root);

        // Animation: rising from the bottom
        tutorialView.setTranslateY(720);
        javafx.animation.TranslateTransition tt = new javafx.animation.TranslateTransition(javafx.util.Duration.seconds(0.5), tutorialView);
        tt.setToY(0);
        tt.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
        tt.play();
    }
}
