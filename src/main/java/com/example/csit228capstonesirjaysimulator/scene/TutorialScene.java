package com.example.csit228capstonesirjaysimulator.scene;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TutorialScene extends SubScene {

    public TutorialScene() {
        Rectangle bg = new Rectangle(1280, 720, Color.color(0, 0, 0, 0.5));

        Image tutorialImg = new Image("assets/textures/tutorial-bg.png");
        ImageView tutorialView = new ImageView(tutorialImg);
        
        // Resize so its height does not take up the entire screen (720px)
        tutorialView.setFitHeight(600);
        tutorialView.setPreserveRatio(true);

        // Center the image manually or use StackPane
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(bg, tutorialView);
        root.setPrefSize(1280, 720);

        // Click anywhere to close the tutorial
        root.setOnMouseClicked(e -> FXGL.getSceneService().popSubScene());

        getContentRoot().getChildren().add(root);

        // Animation: rising from the bottom
        tutorialView.setTranslateY(720); // Start off-screen at the bottom
        javafx.animation.TranslateTransition tt = new javafx.animation.TranslateTransition(javafx.util.Duration.seconds(0.5), tutorialView);
        tt.setToY(0); // End at the centered position
        tt.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
        tt.play();
    }
}
