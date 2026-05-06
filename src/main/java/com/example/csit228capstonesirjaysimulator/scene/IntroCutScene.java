package com.example.csit228capstonesirjaysimulator.scene;

import com.almasb.fxgl.scene.SubScene;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class IntroCutScene extends SubScene {
    public IntroCutScene(Runnable onFinished) {

        //temp cutscene
        //TODO add actual cutscene
        Rectangle bg = new Rectangle(1280, 720, Color.BLACK);

        Text text = new Text("Sir Jay: Quiz! Get one fourth sheet of paper.");
        text.setFill(Color.WHITE);
        text.setFont(Font.font("Monospaced", FontWeight.NORMAL, 20));
        text.setTextAlignment(TextAlignment.CENTER);

        Button btnContinue = new Button("End Cutscene");
        btnContinue.setOnAction(e -> {
            onFinished.run();
        });

        VBox vbox = new VBox(30, text, btnContinue);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefSize(1280, 720);

        getContentRoot().getChildren().addAll(bg, vbox);
    }
}
