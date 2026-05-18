package com.example.csit228capstonesirjaysimulator.scene;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import com.example.csit228capstonesirjaysimulator.component.leaderboard.LeaderboardRow;
import com.example.csit228capstonesirjaysimulator.database.UserDatabaseService;
import com.example.csit228capstonesirjaysimulator.entity.ScoreRecord;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.util.List;


public class LeaderboardScene extends SubScene {
    private VBox listContainer;
    private Node parentRoot;

    public LeaderboardScene(Node parentRoot) {
        this.parentRoot = parentRoot;
        initUI();
    }

    private void initUI(){

        ImageView bg = new ImageView(new Image("assets/textures/bg_polka_dot.jpg"));
        bg.setOpacity(.25);

        Rectangle rect = new Rectangle(600, 650);
        rect.setFill(Color.rgb(30, 30, 30, 0.9));
        rect.setArcHeight(30);
        rect.setArcWidth(30);
        rect.setStroke(Color.GOLD);
        rect.setStrokeWidth(2);
        rect.setEffect(new DropShadow(20, Color.BLACK));

        Font jelleeFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Jellee-Roman.ttf"), 36);

        Text title = new Text("TOP NOTCHERS");
        title.setFont(Font.font(jelleeFont.getFamily(), FontWeight.BOLD, jelleeFont.getSize()));
        title.setFill(Color.GOLD);

        HBox header = new HBox(70, new Text("RANK"), new Text("PROFESSOR"), new Text("SCORE"));
        header.setAlignment(Pos.CENTER);
        header.getChildren().forEach(node -> {
            ((Text) node).setFill(Color.LIGHTGRAY);
            ((Text) node).setFont(Font.font(jelleeFont.getFamily(), FontWeight.BOLD, 14));
        });
        header.setPadding(new Insets(10, 0, 10, 0));

        listContainer = new VBox(8);
        listContainer.setAlignment(Pos.TOP_CENTER);
        listContainer.setPadding(new Insets(10));
        refreshLeaderboard();

        Button btnBack = new Button("RETURN TO MENU");
        btnBack.setFont(Font.font(jelleeFont.getFamily(), FontWeight.BOLD, 14));
        btnBack.setTextFill(Color.BLACK);
        btnBack.setBackground(new Background(new BackgroundFill(Color.GOLD, CornerRadii.EMPTY, Insets.EMPTY)));
        btnBack.setPadding(new Insets(10, 40, 10, 40));

        btnBack.setOnAction(e -> {
            if (parentRoot != null) parentRoot.setEffect(null);
            FXGL.getSceneService().popSubScene();
        });

        VBox contentBox = new VBox(10, title, header, listContainer, btnBack);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(50));

        StackPane container = new StackPane(rect, contentBox);
        container.setPrefSize(1280, 720);
        container.setAlignment(Pos.CENTER);

        getContentRoot().getChildren().addAll(bg, container);
    }

    public void refreshLeaderboard() {
        listContainer.getChildren().clear();
        List<ScoreRecord> scores = UserDatabaseService.getInstance().getLeaderboard();

        int rank = 1;
        for (ScoreRecord s : scores) {
            LeaderboardRow row = new LeaderboardRow(rank, s.getName(), s.getScore());
            row.setAlignment(Pos.CENTER);
            listContainer.getChildren().add(row);
            rank++;
        }
        //fills in the remaining rows
        while (rank <= 10){
            LeaderboardRow row = new LeaderboardRow(rank, "", 0);
            row.setAlignment(Pos.CENTER);
            listContainer.getChildren().add(row);
            rank++;
        }
    }
}