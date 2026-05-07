package com.example.csit228capstonesirjaysimulator.scene;

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

public class FinishScene extends SubScene {
    public FinishScene() {
        //temp cutscene
        //TODO add actual cutscene
        Rectangle bg = new Rectangle(1280, 720, Color.BLACK);

        Text text = new Text("GAME OVER!");
        text.setFill(Color.RED);
        text.setFont(Font.font("Monospaced", FontWeight.NORMAL, 20));
        text.setTextAlignment(TextAlignment.CENTER);

        //TODO add stuff to input leaderboard name

        VBox vbox = new VBox(30, text);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefSize(1280, 720);

        getContentRoot().getChildren().addAll(bg, vbox);
    }
}
