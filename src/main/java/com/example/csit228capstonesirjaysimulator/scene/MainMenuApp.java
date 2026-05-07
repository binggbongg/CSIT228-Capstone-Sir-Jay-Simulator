package com.example.csit228capstonesirjaysimulator.scene;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class MainMenuApp extends FXGLMenu {
    public MainMenuApp(){
        super(MenuType.MAIN_MENU);

        Image image = new Image("assets/textures/logo_not_final.png");
        ImageView bg = new ImageView(image);
        bg.setFitHeight(FXGL.getAppHeight());
        bg.setFitWidth(FXGL.getAppWidth());

        getContentRoot().getChildren().add(bg);

        Button btnPlay  = new Button ("Play");
        Button btnLeaderboard  = new Button ("Leaderboard");
        Button btnExit = new Button ("Exit");

        btnPlay.setPrefWidth(200);
        btnLeaderboard.setPrefWidth(200);
        btnExit.setPrefWidth(200);

        btnPlay.setOnAction( e -> {
            IntroCutScene cutScene = new IntroCutScene(() -> {
                fireNewGame();
            });
            FXGL.getSceneService().pushSubScene(cutScene);
        });
        btnLeaderboard.setOnAction(e -> {
            System.out.println("CLICKING LEADERBOARD");

            getContentRoot().setEffect(new GaussianBlur(50));
            FXGL.getSceneService().pushSubScene(new LeaderboardScene(getContentRoot()));
        });
        btnExit.setOnAction(e -> fireExit());

        VBox menuBox = new VBox(20, btnPlay, btnLeaderboard, btnExit);
        menuBox.setAlignment(Pos.CENTER);

        menuBox.setTranslateX(FXGL.getAppWidth() / 2.0 - 100);
        menuBox.setTranslateY(FXGL.getAppWidth() / 2.0 - 100);

        getContentRoot().getChildren().add(menuBox);
    }
}
