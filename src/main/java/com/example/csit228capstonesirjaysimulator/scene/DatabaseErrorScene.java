package com.example.csit228capstonesirjaysimulator.scene;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import com.example.csit228capstonesirjaysimulator.database.DatabaseConnection;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DatabaseErrorScene extends SubScene {
    private Text statusText;
    private Button btnRetry;

    public DatabaseErrorScene() {
        Rectangle bg = new Rectangle(1280, 720, Color.rgb(0, 0, 0, 0.9));

        Font jelleeTextTitle = Font.loadFont(getClass().getResourceAsStream("/fonts/Jellee-Roman.ttf"), 32);
        Font jelleeTextBody = Font.loadFont(getClass().getResourceAsStream("/fonts/Jellee-Roman.ttf"), 20);

        Text title = new Text("CONNECTION ERROR");
        title.setFill(Color.RED);
        title.setFont(jelleeTextTitle);

        statusText = new Text("MySQL database is not responding. \nPlease ensure XAMPP is running and try again.");
        statusText.setFill(Color.WHITE);
        statusText.setFont(jelleeTextBody);

        btnRetry = new Button("RETRY CONNECTION");
        btnRetry.setFont(jelleeTextBody);

        btnRetry.setOnAction(e -> attemptReconnect());

        VBox layout = new VBox(30, title, statusText, btnRetry);
        layout.setAlignment(Pos.CENTER);
        layout.setPrefSize(1280, 720);

        getContentRoot().getChildren().addAll(bg, layout);
    }

    private void attemptReconnect() {
        btnRetry.setDisable(true);
        statusText.setText("Attempting to connect...");

        // Running the check on a background thread so the UI doesn't lag
        new Thread(() -> {
            boolean success = DatabaseConnection.checkStatus();

            Platform.runLater(() -> {
                if (success) {
                    FXGL.getSceneService().popSubScene();
                } else {
                    statusText.setText("Retry failed. Is MySQL running in XAMPP?");
                    btnRetry.setDisable(false);
                }
            });
        }).start();
    }
}