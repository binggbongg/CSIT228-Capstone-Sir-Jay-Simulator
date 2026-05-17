package com.example.csit228capstonesirjaysimulator.component.leaderboard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class LeaderboardRow extends HBox {

    public LeaderboardRow(int rank, String name, int score) {
        this.setSpacing(0);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 0.1), CornerRadii.EMPTY, Insets.EMPTY)));

        this.setBorder(new Border(new BorderStroke(
                Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                new BorderWidths(0, 0, 1, 0))));

        this.setPadding(new Insets(10));
        this.setPrefWidth(450);
        this.setMaxWidth(450);

        Font jelleeText = Font.loadFont(getClass().getResourceAsStream("/fonts/Jellee-Roman.ttf"), 12);

        Text textRank = new Text("#" + rank);
        textRank.setFill(Color.GOLD);
        textRank.setFont(jelleeText);
        StackPane rankCell = new StackPane(textRank);
        rankCell.setPrefWidth(60);
        rankCell.setAlignment(Pos.CENTER_LEFT);

        Text textName = new Text(name);
        textName.setFill(Color.WHITE);
        textName.setWrappingWidth(150);
        textName.setFont(jelleeText);
        textName.setWrappingWidth(180);
        StackPane nameCell = new StackPane(textName);
        nameCell.setPrefWidth(220);
        nameCell.setAlignment(Pos.CENTER_LEFT);

        Text textScore = new Text(score + " pts");
        textScore.setFill(Color.LIGHTGREEN);
        textScore.setFont(jelleeText);
        StackPane scoreCell = new StackPane(textScore);
        scoreCell.setPrefWidth(120);
        scoreCell.setAlignment(Pos.CENTER_RIGHT);

        this.getChildren().addAll(rankCell, nameCell, scoreCell);
    }
}
