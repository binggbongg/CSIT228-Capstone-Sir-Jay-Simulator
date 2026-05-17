package com.example.csit228capstonesirjaysimulator.scene;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
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

import java.util.ArrayList;
import java.util.List;

public class ProfileSelectScene extends SubScene {

    private final javafx.scene.Node parentRoot;
    private final Runnable          onPlay;

    private final List<UserProfile> profiles = new ArrayList<>();
    private UserProfile selectedProfile = null;

    private FlowPane  cardFlow;
    private Button    btnViewProfile;
    private Button    btnPlay;
    private HBox      actionsRow;
    private VBox      dialogBox;
    private StackPane overlayPane;

    public ProfileSelectScene(javafx.scene.Node parentRoot, Runnable onPlay) {
        this.parentRoot = parentRoot;
        this.onPlay     = onPlay;
        profiles.addAll(UserDatabaseService.getInstance().getAllProfiles());
        buildScene();
    }

    private void buildScene() {
        Rectangle bg = panelBg(600, 650);

        Text title = new Text("SELECT PROFILE");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 36));
        title.setFill(Color.GOLD);

        cardFlow = new FlowPane(12, 12);
        cardFlow.setAlignment(Pos.CENTER);
        cardFlow.setPrefWrapLength(580);
        cardFlow.setPadding(new Insets(8));
        cardFlow.setMaxWidth(580);
        refreshCards();

        ScrollPane scroll = new ScrollPane(cardFlow);
        scroll.setPrefSize(520, 340);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; "
                + "-fx-border-color: transparent;");

        actionsRow = buildActionsRow();

        Button btnBack = buildButton("RETURN TO MENU");
        btnBack.setOnAction(e -> closeScene());

        VBox content = new VBox(16, title, scroll, actionsRow, btnBack);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(600);
        content.setPadding(new Insets(40));

        StackPane panel = new StackPane(bg, content);
        panel.setPrefSize(1280, 720);
        panel.setAlignment(Pos.CENTER);

        overlayPane = new StackPane();
        overlayPane.setPrefSize(1280, 720);
        overlayPane.setVisible(false);
        overlayPane.setStyle("-fx-background-color: rgba(0,0,0,0.65);");

        dialogBox = new VBox();
        overlayPane.getChildren().add(dialogBox);

        StackPane root = new StackPane(panel, overlayPane);
        root.setPrefSize(1280, 720);
        getContentRoot().getChildren().add(root);
    }

    private HBox buildActionsRow() {
        btnViewProfile = buildButton("VIEW PROFILE");
        btnViewProfile.setDisable(true);
        btnViewProfile.setOnAction(e -> showViewDialog());

        btnPlay = buildButton("PLAY");
        btnPlay.setDisable(true);
        btnPlay.setOnAction(e -> showPasswordDialogThenPlay());

        HBox row = new HBox(16, btnViewProfile, btnPlay);
        row.setAlignment(Pos.CENTER);
        return row;
    }


    private void refreshCards() {
        cardFlow.getChildren().clear();
        for (UserProfile p : profiles)
            cardFlow.getChildren().add(buildProfileCard(p));
        cardFlow.getChildren().add(buildAddCard());
    }

    private VBox buildProfileCard(UserProfile p) {
        boolean sel = p.equals(selectedProfile);

        Rectangle bg = new Rectangle(155, 80);
        bg.setFill(sel ? Color.rgb(60, 55, 10) : Color.rgb(50, 50, 50));
        bg.setArcWidth(10); bg.setArcHeight(10);
        bg.setStroke(sel ? Color.GOLD : Color.rgb(90, 90, 90));
        bg.setStrokeWidth(sel ? 2 : 1);

        Text name = new Text(truncate(p.getUsername(), 14));
        name.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        name.setFill(sel ? Color.GOLD : Color.LIGHTGRAY);

        Text sub = new Text(p.getCourse() + " · " + p.getSection());
        sub.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        sub.setFill(Color.GRAY);

        VBox inner = new VBox(4, name, sub);
        inner.setAlignment(Pos.CENTER);
        inner.setMaxSize(155, 80);

        StackPane card = new StackPane(bg, inner);
        card.setPrefSize(155, 80);
        card.setStyle("-fx-cursor: hand;");

        card.setOnMouseClicked(e -> {
            selectedProfile = p;
            btnViewProfile.setDisable(false);
            btnPlay.setDisable(false);
            refreshCards();
        });

        card.setOnMouseEntered(ev -> {
            if (!p.equals(selectedProfile))
                bg.setFill(Color.rgb(65, 65, 65));
        });
        card.setOnMouseExited(ev -> {
            if (!p.equals(selectedProfile))
                bg.setFill(Color.rgb(50, 50, 50));
        });

        VBox wrapper = new VBox(card);
        wrapper.setAlignment(Pos.CENTER);
        return wrapper;
    }

    private VBox buildAddCard() {
        Rectangle bg = new Rectangle(155, 80);
        bg.setFill(Color.rgb(40, 40, 40));
        bg.setArcWidth(10); bg.setArcHeight(10);
        bg.setStroke(Color.rgb(100, 100, 100));
        bg.getStrokeDashArray().addAll(6.0, 4.0);

        Text plus = new Text("+");
        plus.setFont(Font.font("Verdana", FontWeight.BOLD, 28));
        plus.setFill(Color.LIGHTGRAY);

        StackPane card = new StackPane(bg, plus);
        card.setPrefSize(155, 80);
        card.setStyle("-fx-cursor: hand;");
        card.setOnMouseClicked(e -> showAddDialog());
        card.setOnMouseEntered(ev -> bg.setFill(Color.rgb(55, 55, 55)));
        card.setOnMouseExited(ev  -> bg.setFill(Color.rgb(40, 40, 40)));

        VBox wrapper = new VBox(card);
        wrapper.setAlignment(Pos.CENTER);
        return wrapper;
    }


    private void showPasswordDialogThenPlay() {
        if (selectedProfile == null) return;
        showConfirmIdentityDialog(selectedProfile, () -> {
            selectedProfile = UserDatabaseService.getInstance()
                    .loginUser(selectedProfile.getTeacherId(), selectedProfile.getPassword());
            overlayPane.setVisible(false);
            startGame();
        });
    }

    private void showViewDialog() {
        if (selectedProfile == null) return;
        openOverlay();

        UserProfile         p  = selectedProfile;
        UserDatabaseService db = UserDatabaseService.getInstance();

        Rectangle dBg = panelBg(780, 540);

        Text dTitle = new Text(p.getUsername());
        dTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        dTitle.setFill(Color.GOLD);

        Text sub = new Text(p.getTeacherId() + "  ·  " + p.getCourse() + " " + p.getSection());
        sub.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        sub.setFill(Color.LIGHTGRAY);

        //Mission list
        VBox missionList = new VBox(8);
        missionList.setPadding(new Insets(6));

        new Thread(() -> {
            List<MissionProgressRow> rows =
                    MissionRepository.getInstance().getLatestSessionProgress(p.getTeacherId());
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

        // Stats
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

        // Bottom buttons
        Button btnEdit   = buildButton("EDIT");
        Button btnDelete = buildDeleteButton("DELETE");
        Button btnClose  = buildButton("CLOSE");

        btnClose.setOnAction(e -> overlayPane.setVisible(false));

        btnEdit.setOnAction(e -> {

            showConfirmIdentityDialog(p, () -> {
                overlayPane.setVisible(false);
                showEditDialog(p);
            });
        });

        btnDelete.setOnAction(e -> {

            showDeleteConfirmStep(p);
        });

        HBox bottomBtns = new HBox(16, btnEdit, btnDelete, btnClose);
        bottomBtns.setAlignment(Pos.CENTER);

        VBox content = new VBox(12, dTitle, sub, columns, bottomBtns);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(32));
        content.setMaxWidth(780);

        showDialog(new StackPane(dBg, content));
    }


    private void showConfirmIdentityDialog(UserProfile p, Runnable onSuccess) {
        openOverlay();

        Rectangle dBg = panelBg(380, 270);

        Text dTitle = new Text("CONFIRM IDENTITY");
        dTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        dTitle.setFill(Color.GOLD);

        Text sub = new Text("Profile: " + p.getUsername());
        sub.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        sub.setFill(Color.LIGHTGRAY);

        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("Password");
        pfPass.setPrefWidth(300);
        styleTextField(pfPass);

        Label err = errorLabel();

        Button btnOk     = buildButton("CONFIRM");
        Button btnCancel = cancelButton(() -> overlayPane.setVisible(false));

        btnOk.setOnAction(e -> {
            if (p.checkPassword(pfPass.getText())) {
                onSuccess.run();
            } else {
                err.setText("Incorrect password.");
                pfPass.clear();
            }
        });

        pfPass.setOnAction(e -> btnOk.fire());

        HBox btns = new HBox(12, btnCancel, btnOk);
        btns.setAlignment(Pos.CENTER);

        VBox form = new VBox(14, dTitle, sub, pfPass, err, btns);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(36));
        form.setMaxWidth(380);

        showDialog(new StackPane(dBg, form));
    }

    private void showDeleteConfirmStep(UserProfile p) {
        openOverlay();

        Rectangle dBg = panelBg(420, 260);

        Text dTitle = new Text("DELETE PROFILE");
        dTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        dTitle.setFill(Color.rgb(220, 60, 60));

        Text warning = new Text(
                "Are you sure you want to delete\n\""
                        + p.getUsername()
                        + "\"?\n\nThis action cannot be undone.");
        warning.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        warning.setFill(Color.LIGHTGRAY);
        warning.setTextAlignment(TextAlignment.CENTER);

        Button btnConfirm = buildDeleteButton("DELETE");
        Button btnCancel  = cancelButton(() -> overlayPane.setVisible(false));

        // After confirming "yes", move to the password step
        btnConfirm.setOnAction(e -> showDeletePasswordStep(p));

        HBox btns = new HBox(12, btnCancel, btnConfirm);
        btns.setAlignment(Pos.CENTER);

        VBox form = new VBox(16, dTitle, warning, btns);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(32));
        form.setMaxWidth(420);

        showDialog(new StackPane(dBg, form));
    }

    private void showDeletePasswordStep(UserProfile p) {
        openOverlay();

        Rectangle dBg = panelBg(380, 270);

        Text dTitle = new Text("CONFIRM IDENTITY");
        dTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        dTitle.setFill(Color.GOLD);

        Text sub = new Text("Enter password to confirm deletion of\n\"" + p.getUsername() + "\"");
        sub.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        sub.setFill(Color.LIGHTGRAY);
        sub.setTextAlignment(TextAlignment.CENTER);

        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("Password");
        pfPass.setPrefWidth(300);
        styleTextField(pfPass);

        Label err = errorLabel();

        Button btnOk     = buildDeleteButton("DELETE");
        Button btnCancel = cancelButton(() -> overlayPane.setVisible(false));

        btnOk.setOnAction(e -> {
            if (p.checkPassword(pfPass.getText())) {
                new Thread(() -> {
                    boolean ok = UserDatabaseService.getInstance().deleteUser(p.getTeacherId());
                    javafx.application.Platform.runLater(() -> {
                        if (ok) {
                            profiles.remove(p);
                            if (p.equals(selectedProfile)) {
                                selectedProfile = null;
                                btnViewProfile.setDisable(true);
                                btnPlay.setDisable(true);
                            }
                            overlayPane.setVisible(false);
                            refreshCards();
                        } else {
                            err.setText("DB error — check console.");
                        }
                    });
                }).start();
            } else {
                err.setText("Incorrect password.");
                pfPass.clear();
            }
        });

        pfPass.setOnAction(e -> btnOk.fire());

        HBox btns = new HBox(12, btnCancel, btnOk);
        btns.setAlignment(Pos.CENTER);

        VBox form = new VBox(14, dTitle, sub, pfPass, err, btns);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(36));
        form.setMaxWidth(380);

        showDialog(new StackPane(dBg, form));
    }

    private void showEditDialog(UserProfile p) {
        openOverlay();

        Rectangle dBg = panelBg(420, 440);

        Text dTitle = new Text("EDIT PROFILE");
        dTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        dTitle.setFill(Color.GOLD);

        TextField tfName = dialogField("Username");
        tfName.setText(p.getUsername());

        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("New Password");
        pfPass.setPrefWidth(340);
        styleTextField(pfPass);

        PasswordField pfConfirm = new PasswordField();
        pfConfirm.setPromptText("Confirm New Password");
        pfConfirm.setPrefWidth(340);
        styleTextField(pfConfirm);

        ComboBox<String> cbCourse  = styledCombo("Course",  "CS244", "CSIT228");
        ComboBox<String> cbSection = styledCombo("Section", "F1", "F2", "F3");
        cbCourse.setValue(p.getCourse());
        cbSection.setValue(p.getSection());

        HBox combos = new HBox(10, cbCourse, cbSection);
        combos.setAlignment(Pos.CENTER);

        Label err      = errorLabel();
        Button btnSave   = buildButton("SAVE");
        Button btnCancel = cancelButton(() -> overlayPane.setVisible(false));

        btnSave.setOnAction(e -> {
            String newName = tfName.getText().trim();
            String newCrs  = cbCourse.getValue();
            String newSec  = cbSection.getValue();
            String newPw   = pfPass.getText();
            String newPw2  = pfConfirm.getText();

            if (newName.isEmpty() || newCrs == null || newSec == null) {
                err.setText("Please fill in all fields.");
                return;
            }
            String finalPassword;
            if (!newPw.isEmpty() || !newPw2.isEmpty()) {
                if (!newPw.equals(newPw2)) {
                    err.setText("Passwords do not match.");
                    return;
                }
                finalPassword = newPw;
            } else {
                finalPassword = p.getPassword();
            }

            final String fp = finalPassword;
            new Thread(() -> {
                boolean ok = UserDatabaseService.getInstance()
                        .updateUser(p.getTeacherId(), newName, newCrs, newSec, fp);
                javafx.application.Platform.runLater(() -> {
                    if (ok) {
                        // Rebuild the profile object in our local list
                        UserProfile updated = new UserProfile(
                                p.getTeacherId(), newName, newCrs, newSec, fp);
                        int idx = profiles.indexOf(p);
                        if (idx >= 0) profiles.set(idx, updated);
                        selectedProfile = updated;
                        overlayPane.setVisible(false);
                        refreshCards();
                    } else {
                        err.setText("DB error — check console.");
                    }
                });
            }).start();
        });

        HBox btns = new HBox(12, btnCancel, btnSave);
        btns.setAlignment(Pos.CENTER);

        Text idLabel = new Text("Teacher ID: " + p.getTeacherId());
        idLabel.setFill(Color.GRAY);
        idLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

        VBox form = new VBox(12, dTitle, idLabel, tfName, pfPass, pfConfirm, combos, err, btns);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(32));
        form.setMaxWidth(420);

        showDialog(new StackPane(dBg, form));
    }

    private void showAddDialog() {
        openOverlay();

        Rectangle dBg = panelBg(420, 440);

        Text dTitle = new Text("ADD PROFILE");
        dTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        dTitle.setFill(Color.GOLD);

        TextField tfId   = dialogField("Teacher ID");
        TextField tfName = dialogField("Username");

        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("Password");
        pfPass.setPrefWidth(340);
        styleTextField(pfPass);

        PasswordField pfConfirm = new PasswordField();
        pfConfirm.setPromptText("Confirm Password");
        pfConfirm.setPrefWidth(340);
        styleTextField(pfConfirm);

        ComboBox<String> cbCourse  = styledCombo("Course",  "CS244", "CSIT228");
        ComboBox<String> cbSection = styledCombo("Section", "F1", "F2", "F3");
        HBox combos = new HBox(10, cbCourse, cbSection);
        combos.setAlignment(Pos.CENTER);

        Label  err       = errorLabel();
        Button btnSave   = buildButton("SAVE");
        Button btnCancel = cancelButton(() -> overlayPane.setVisible(false));

        btnSave.setOnAction(e -> {
            String tid  = tfId.getText().trim();
            String user = tfName.getText().trim();
            String crs  = cbCourse.getValue();
            String sec  = cbSection.getValue();
            String pw   = pfPass.getText();
            String pw2  = pfConfirm.getText();

            if (tid.isEmpty() || user.isEmpty() || crs == null || sec == null || pw.isEmpty()) {
                err.setText("Please fill in all fields."); return;
            }
            if (!pw.equals(pw2)) {
                err.setText("Passwords do not match."); return;
            }
            if (profiles.stream().anyMatch(p -> p.getTeacherId().equalsIgnoreCase(tid))) {
                err.setText("Teacher ID already exists."); return;
            }

            UserProfile np = new UserProfile(tid, user, crs, sec, pw);
            new Thread(() -> {
                boolean ok = UserDatabaseService.getInstance()
                        .registerUser(tid, user, crs, sec, pw);
                javafx.application.Platform.runLater(() -> {
                    if (ok) { profiles.add(np); refreshCards(); overlayPane.setVisible(false); }
                    else    { err.setText("DB error — check console."); }
                });
            }).start();
        });

        HBox btns = new HBox(12, btnCancel, btnSave);
        btns.setAlignment(Pos.CENTER);

        VBox form = new VBox(12, dTitle, tfId, tfName, pfPass, pfConfirm, combos, err, btns);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(32));
        form.setMaxWidth(420);

        showDialog(new StackPane(dBg, form));
    }

    private void startGame() {
        if (selectedProfile == null) return;
        System.out.println("[ProfileSelect] Playing as: " + selectedProfile);
        UserDatabaseService.getInstance().setCurrentUser(selectedProfile);
        closeScene();
        if (onPlay != null) onPlay.run();
    }

    private void closeScene() {
        if (parentRoot != null) parentRoot.setEffect(null);
        FXGL.getSceneService().popSubScene();
    }

    private void openOverlay() {
        dialogBox.getChildren().clear();
        overlayPane.setVisible(true);
    }

    private void showDialog(javafx.scene.Node node) {
        dialogBox.getChildren().add(node);
        dialogBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(dialogBox, Priority.ALWAYS);
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

        javafx.scene.layout.Pane barPane = new javafx.scene.layout.Pane();
        barPane.setPrefSize(BAR_TOTAL, 8);
        barPane.setMaxSize(BAR_TOTAL, 8);
        barPane.setMinSize(BAR_TOTAL, 8);

        Rectangle track = new Rectangle(BAR_TOTAL, 8, Color.rgb(60, 60, 60));
        track.setArcWidth(4);
        track.setArcHeight(4);
        track.setLayoutX(0);
        track.setLayoutY(0);

        double fillWidth = BAR_TOTAL * pct;
        Rectangle fill = new Rectangle(Math.max(fillWidth, 0.001), 8, barColor);
        fill.setArcWidth(4);
        fill.setArcHeight(4);
        fill.setLayoutX(0);
        fill.setLayoutY(0);

        barPane.getChildren().addAll(track, fill);


        String progressLabel;
        if ("BOOLEAN".equalsIgnoreCase(row.type())) {
            progressLabel = row.completed() ? "✓" : "✗";
        } else {
            progressLabel = row.currentValue() + " / " + row.targetValue()
                    + "  (" + (int)(pct * 100) + "%)";
        }

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


    private Rectangle panelBg(double w, double h) {
        Rectangle r = new Rectangle(w, h);
        r.setFill(Color.rgb(30, 30, 30, 0.95));
        r.setArcWidth(20); r.setArcHeight(20);
        r.setStroke(Color.GOLD); r.setStrokeWidth(1.5);
        r.setEffect(new DropShadow(20, Color.BLACK));
        return r;
    }

    private Button buildButton(String label) {
        Button b = new Button(label);
        b.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        b.setTextFill(Color.BLACK);
        b.setBackground(new Background(new BackgroundFill(Color.GOLD, CornerRadii.EMPTY, Insets.EMPTY)));
        b.setPadding(new Insets(10, 30, 10, 30));
        return b;
    }

    private Button buildDeleteButton(String label) {
        Button b = new Button(label);
        b.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        b.setTextFill(Color.WHITE);
        b.setBackground(new Background(new BackgroundFill(
                Color.rgb(180, 40, 40), new CornerRadii(4), Insets.EMPTY)));
        b.setPadding(new Insets(10, 30, 10, 30));
        return b;
    }

    private Button cancelButton(Runnable action) {
        Button b = new Button("CANCEL");
        b.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        b.setTextFill(Color.LIGHTGRAY);
        b.setBackground(Background.EMPTY);
        b.setOnAction(e -> action.run());
        return b;
    }

    private TextField dialogField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(340);
        styleTextField(tf);
        return tf;
    }

    private void styleTextField(javafx.scene.control.TextInputControl tf) {
        tf.setFont(Font.font("Arial", 13));
        tf.setStyle("-fx-background-color:#222222; -fx-text-fill:#e0e0e0; "
                + "-fx-prompt-text-fill:#666666; -fx-border-color:#666666; "
                + "-fx-border-width:1; -fx-border-radius:4; "
                + "-fx-background-radius:4; -fx-padding:7 10 7 10;");
    }

    private ComboBox<String> styledCombo(String prompt, String... items) {
        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll(items);
        cb.setPromptText(prompt);
        cb.setPrefWidth(160);
        cb.setStyle("-fx-background-color:#222222; -fx-border-color:#666666; "
                + "-fx-border-width:1; -fx-border-radius:4; -fx-background-radius:4;");
        return cb;
    }

    private Label errorLabel() {
        Label l = new Label(" ");
        l.setTextFill(Color.rgb(220, 80, 80));
        l.setFont(Font.font("Arial", 11));
        return l;
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

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }
}
