package com.example.csit228capstonesirjaysimulator.component.leaderboard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class LeaderboardRow extends HBox {

    public LeaderboardRow(int rank, String name, int score) {
        this.setSpacing(50);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 0.1), CornerRadii.EMPTY, Insets.EMPTY)));

        this.setBorder(new Border(new BorderStroke(
                Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                new BorderWidths(0, 0, 1, 0))));

        this.setPadding(new Insets(10));
        this.setPrefWidth(450);
        this.setMaxWidth(450);

        Text textRank = new Text("#" + rank);
        textRank.setFill(Color.GOLD);

        Text textName = new Text(name);
        textName.setFill(Color.WHITE);
        textName.setWrappingWidth(150);

        Text textScore = new Text(String.valueOf(score) + " pts");
        textScore.setFill(Color.LIGHTGREEN);

        this.getChildren().addAll(textRank, textName, textScore);
    }
}
