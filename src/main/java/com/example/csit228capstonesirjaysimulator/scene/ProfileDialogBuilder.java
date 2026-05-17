package com.example.csit228capstonesirjaysimulator.scene;

import com.example.csit228capstonesirjaysimulator.database.MissionRepository;
import com.example.csit228capstonesirjaysimulator.database.MissionRepository.MissionProgressRow;
import com.example.csit228capstonesirjaysimulator.database.UserDatabaseService;
import com.example.csit228capstonesirjaysimulator.database.UserProfile;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;

import java.util.List;

public class ProfileDialogBuilder {

    private final List<UserProfile> profiles;

    public ProfileDialogBuilder(List<UserProfile> profiles) {
        this.profiles = profiles;
    }


    public StackPane buildAddDialog(TextField tfId, TextField tfName,
                                    PasswordField pfPass, PasswordField pfConfirm,
                                    ComboBox<String> cbCourse, ComboBox<String> cbSection,
                                    Label err, Button btnSave, Button btnCancel) {
        Rectangle dBg = panelBg(420, 440);

        Text dTitle = new Text("ADD PROFILE");
        dTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        dTitle.setFill(Color.GOLD);

        HBox combos = new HBox(10, cbCourse, cbSection);
        combos.setAlignment(Pos.CENTER);

        HBox btns = new HBox(12, btnCancel, btnSave);
        btns.setAlignment(Pos.CENTER);

        VBox form = new VBox(12, dTitle, tfId, tfName, pfPass, pfConfirm, combos, err, btns);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(32));
        form.setMaxWidth(420);

        return new StackPane(dBg, form);
    }


    public StackPane buildEditDialog(UserProfile p,
                                     TextField tfName,
                                     PasswordField pfPass, PasswordField pfConfirm,
                                     ComboBox<String> cbCourse, ComboBox<String> cbSection,
                                     Label err, Button btnSave, Button btnCancel) {
        Rectangle dBg = panelBg(420, 440);

        Text dTitle = new Text("EDIT PROFILE");
        dTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        dTitle.setFill(Color.GOLD);

        Text idLabel = new Text("Teacher ID: " + p.getTeacherId());
        idLabel.setFill(Color.GRAY);
        idLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

        HBox combos = new HBox(10, cbCourse, cbSection);
        combos.setAlignment(Pos.CENTER);

        HBox btns = new HBox(12, btnCancel, btnSave);
        btns.setAlignment(Pos.CENTER);

        VBox form = new VBox(12, dTitle, idLabel, tfName, pfPass, pfConfirm, combos, err, btns);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(32));
        form.setMaxWidth(420);

        return new StackPane(dBg, form);
    }


    public StackPane buildViewDialog(UserProfile p,
                                     Button btnEdit, Button btnDelete, Button btnClose) {
        Rectangle dBg = panelBg(780, 540);

        Text dTitle = new Text(p.getUsername());
        dTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        dTitle.setFill(Color.GOLD);

        Text sub = new Text(p.getTeacherId() + "  ·  " + p.getCourse() + " " + p.getSection());
        sub.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        sub.setFill(Color.LIGHTGRAY);

        VBox missionList = new VBox(8);
        missionList.setPadding(new Insets(6));

        new Thread(() -> {
            List<MissionProgressRow> rows = MissionRepository.getInstance()
                    .getLatestSessionProgress(p.getTeacherId());
            javafx.application.Platform.runLater(() -> {
                if (rows.isEmpty()) {
                    Text none = new Text("No sessions played yet.");
                    none.setFill(Color.GRAY);
                    none.setFont(Font.font("Arial", 12));
                    missionList.getChildren().add(none);
                } else {
                    for (MissionProgressRow row : rows)
                        missionList.getChildren().add(buildMissionRow(row));
                }
            });
        }).start();

        ScrollPane missionScroll = new ScrollPane(missionList);
        missionScroll.setPrefSize(330, 300);
        missionScroll.setFitToWidth(true);
        missionScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        missionScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; "
                + "-fx-border-color: #444444; -fx-border-width: 1;");

        VBox leftCol = new VBox(10, columnTitle("MISSIONS"), missionScroll);
        leftCol.setAlignment(Pos.TOP_CENTER);

        UserDatabaseService db = UserDatabaseService.getInstance();
        VBox statsList = new VBox(8,
                statRow("Catch Accuracy",    String.format("%.1f%%", db.getCatchAccuracy(p.getTeacherId()))),
                statRow("Highest Combo",     String.valueOf(db.getHighestCombo(p.getTeacherId()))),
                statRow("Cheaters Caught",   String.valueOf(db.getCheatersCaught(p.getTeacherId()))),
                statRow("False Accusations", String.valueOf(db.getFalseAccusations(p.getTeacherId()))),
                statRow("Quizzes Conducted", String.valueOf(db.getQuizzesConducted(p.getTeacherId()))),
                statRow("Hours Worked",      String.format("%.1f h", db.getHoursWorked(p.getTeacherId())))
        );
        statsList.setAlignment(Pos.TOP_LEFT);
        statsList.setPadding(new Insets(6));

        VBox rightCol = new VBox(10, columnTitle("STATS"), statsList);
        rightCol.setAlignment(Pos.TOP_CENTER);

        HBox columns = new HBox(20, leftCol, new Rectangle(1, 340, Color.rgb(80, 76, 40)), rightCol);
        columns.setAlignment(Pos.TOP_CENTER);

        HBox bottomBtns = new HBox(16, btnEdit, btnDelete, btnClose);
        bottomBtns.setAlignment(Pos.CENTER);

        VBox content = new VBox(12, dTitle, sub, columns, bottomBtns);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(32));
        content.setMaxWidth(780);

        return new StackPane(dBg, content);
    }


    public StackPane buildConfirmDialog(UserProfile p,
                                        PasswordField pfPass,
                                        Label err,
                                        Button btnOk, Button btnCancel) {
        Rectangle dBg = panelBg(380, 290);

        Text dTitle = new Text("CONFIRM IDENTITY");
        dTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        dTitle.setFill(Color.GOLD);

        Text sub = new Text("Profile: " + p.getUsername());
        sub.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        sub.setFill(Color.LIGHTGRAY);

        HBox btns = new HBox(12, btnCancel, btnOk);
        btns.setAlignment(Pos.CENTER);

        VBox form = new VBox(14, dTitle, sub, pfPass, err, btns);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(36));
        form.setMaxWidth(380);

        return new StackPane(dBg, form);
    }


    public StackPane buildDeleteConfirmDialog(UserProfile p,
                                              Button btnConfirm, Button btnCancel) {
        Rectangle dBg = panelBg(420, 260);

        Text dTitle = new Text("DELETE PROFILE");
        dTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        dTitle.setFill(Color.rgb(220, 60, 60));

        Text warning = new Text("Are you sure you want to delete\n\""
                + p.getUsername() + "\"?\n\nThis action cannot be undone.");
        warning.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        warning.setFill(Color.LIGHTGRAY);
        warning.setTextAlignment(TextAlignment.CENTER);

        HBox btns = new HBox(12, btnCancel, btnConfirm);
        btns.setAlignment(Pos.CENTER);

        VBox form = new VBox(16, dTitle, warning, btns);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(32));
        form.setMaxWidth(420);

        return new StackPane(dBg, form);
    }


    public StackPane buildDeletePasswordDialog(UserProfile p,
                                               PasswordField pfPass,
                                               Label err,
                                               Button btnOk, Button btnCancel) {
        Rectangle dBg = panelBg(380, 270);

        Text dTitle = new Text("CONFIRM IDENTITY");
        dTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        dTitle.setFill(Color.GOLD);

        Text sub = new Text("Enter password to confirm deletion of\n\"" + p.getUsername() + "\"");
        sub.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        sub.setFill(Color.LIGHTGRAY);
        sub.setTextAlignment(TextAlignment.CENTER);

        HBox btns = new HBox(12, btnCancel, btnOk);
        btns.setAlignment(Pos.CENTER);

        VBox form = new VBox(14, dTitle, sub, pfPass, err, btns);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(36));
        form.setMaxWidth(380);

        return new StackPane(dBg, form);
    }


    public TextField dialogField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(340);
        styleTextField(tf);
        return tf;
    }

    public PasswordField passwordField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setPrefWidth(340);
        styleTextField(pf);
        return pf;
    }

    public ComboBox<String> styledCombo(String prompt, String... items) {
        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll(items);
        cb.setPromptText(prompt);
        cb.setPrefWidth(160);
        cb.setStyle("-fx-background-color:#222222; -fx-border-color:#666666; "
                + "-fx-border-width:1; -fx-border-radius:4; -fx-background-radius:4;");
        return cb;
    }

    public Label errorLabel() {
        Label l = new Label(" ");
        l.setTextFill(Color.rgb(220, 80, 80));
        l.setFont(Font.font("Arial", 11));
        return l;
    }

    public Button buildButton(String label) {
        Button b = new Button(label);
        b.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        b.setTextFill(Color.BLACK);
        b.setBackground(new Background(new BackgroundFill(Color.GOLD, CornerRadii.EMPTY, Insets.EMPTY)));
        b.setPadding(new Insets(10, 30, 10, 30));
        return b;
    }

    public Button buildDeleteButton(String label) {
        Button b = new Button(label);
        b.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        b.setTextFill(Color.WHITE);
        b.setBackground(new Background(new BackgroundFill(
                Color.rgb(180, 40, 40), new CornerRadii(4), Insets.EMPTY)));
        b.setPadding(new Insets(10, 30, 10, 30));
        return b;
    }

    public Button cancelButton(Runnable action) {
        Button b = new Button("CANCEL");
        b.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        b.setTextFill(Color.LIGHTGRAY);
        b.setBackground(Background.EMPTY);
        b.setOnAction(e -> action.run());
        return b;
    }

    public void styleTextField(javafx.scene.control.TextInputControl tf) {
        tf.setFont(Font.font("Arial", 13));
        tf.setStyle("-fx-background-color:#222222; -fx-text-fill:#e0e0e0; "
                + "-fx-prompt-text-fill:#666666; -fx-border-color:#666666; "
                + "-fx-border-width:1; -fx-border-radius:4; "
                + "-fx-background-radius:4; -fx-padding:7 10 7 10;");
    }



    private Rectangle panelBg(double w, double h) {
        Rectangle r = new Rectangle(w, h);
        r.setFill(Color.rgb(30, 30, 30, 0.95));
        r.setArcWidth(20); r.setArcHeight(20);
        r.setStroke(Color.GOLD); r.setStrokeWidth(1.5);
        r.setEffect(new DropShadow(20, Color.BLACK));
        return r;
    }

    private Text columnTitle(String text) {
        Text t = new Text(text);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        t.setFill(Color.GOLD);
        return t;
    }

    private HBox statRow(String label, String value) {
        Text lbl = new Text(label);
        lbl.setFill(Color.LIGHTGRAY);
        lbl.setFont(Font.font("Arial", FontWeight.NORMAL, 13));

        Text val = new Text(value);
        val.setFill(Color.WHITE);
        val.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(lbl, spacer, val);
        row.setPrefWidth(300);
        row.setPadding(new Insets(0, 10, 0, 10));
        return row;
    }

    private HBox buildMissionRow(MissionProgressRow row) {
        Color barColor = row.completed() ? Color.LIMEGREEN : Color.GOLD;

        double pct;
        if ("BOOLEAN".equalsIgnoreCase(row.type())) {
            pct = row.completed() ? 1.0 : 0.0;
        } else {
            pct = row.targetValue() == 0
                    ? 1.0
                    : Math.min(1.0, (double) row.currentValue() / row.targetValue());
        }

        final double BAR_TOTAL = 140.0;
        Pane barPane = new Pane();
        barPane.setPrefSize(BAR_TOTAL, 8);
        barPane.setMaxSize(BAR_TOTAL, 8);
        barPane.setMinSize(BAR_TOTAL, 8);

        Rectangle track = new Rectangle(BAR_TOTAL, 8, Color.rgb(60, 60, 60));
        track.setArcWidth(4); track.setArcHeight(4);

        Rectangle fill = new Rectangle(Math.max(BAR_TOTAL * pct, 0.001), 8, barColor);
        fill.setArcWidth(4); fill.setArcHeight(4);

        barPane.getChildren().addAll(track, fill);

        String progressLabel = "BOOLEAN".equalsIgnoreCase(row.type())
                ? (row.completed() ? "✓" : "✗")
                : row.currentValue() + " / " + row.targetValue() + "  (" + (int)(pct * 100) + "%)";

        Text desc = new Text(row.description());
        desc.setFill(row.completed() ? Color.LIMEGREEN : Color.LIGHTGRAY);
        desc.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        desc.setWrappingWidth(160);

        Text prog = new Text(progressLabel);
        prog.setFill(barColor);
        prog.setFont(Font.font("Arial", FontWeight.BOLD, 11));

        VBox left = new VBox(3, desc, barPane);
        left.setAlignment(Pos.CENTER_LEFT);

        HBox rowBox = new HBox(10, left, prog);
        rowBox.setAlignment(Pos.CENTER_LEFT);
        rowBox.setPadding(new Insets(4, 8, 4, 8));
        rowBox.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 4;");
        return rowBox;
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }
}