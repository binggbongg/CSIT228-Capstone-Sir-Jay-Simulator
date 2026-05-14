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
    private final Runnable onPlay;

    private final List<UserProfile> profiles = new ArrayList<>();
    private UserProfile selectedProfile = null;


    private boolean editMode = false;


    private FlowPane  cardFlow;
    private Button    btnViewProfile;
    private Button    btnPlay;
    private Button    btnDelete;
    private Button    btnEdit;
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

        btnEdit = buildEditButton();
        btnEdit.setOnAction(e -> toggleEditMode());

        HBox titleRow = new HBox(16, title, btnEdit);
        titleRow.setAlignment(Pos.CENTER);


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


        VBox content = new VBox(16, titleRow, scroll, actionsRow, btnBack);
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



    private void toggleEditMode() {
        editMode = !editMode;

        if (editMode) {

            btnEdit.setText("DONE");
            btnEdit.setBackground(new Background(new BackgroundFill(
                    Color.rgb(50, 150, 50), new CornerRadii(4), Insets.EMPTY)));
            btnEdit.setTextFill(Color.WHITE);
        } else {

            btnEdit.setText("EDIT");
            btnEdit.setBackground(new Background(new BackgroundFill(
                    Color.rgb(80, 80, 80), new CornerRadii(4), Insets.EMPTY)));
            btnEdit.setTextFill(Color.LIGHTGRAY);
            selectedProfile = null;
        }

        rebuildActionsRow();
        refreshCards();
    }




    private HBox buildActionsRow() {
        btnViewProfile = buildButton("VIEW PROFILE");
        btnViewProfile.setDisable(true);
        btnViewProfile.setOnAction(e -> showViewDialog());

        btnPlay = buildButton("PLAY");
        btnPlay.setDisable(true);
        btnPlay.setOnAction(e -> showPasswordDialog());

        btnDelete = buildDeleteButton();
        btnDelete.setDisable(true);
        btnDelete.setOnAction(e -> showDeleteConfirmDialog());

        HBox row = new HBox(16, btnViewProfile, btnPlay);
        row.setAlignment(Pos.CENTER);
        return row;
    }


    private void rebuildActionsRow() {
        actionsRow.getChildren().clear();

        if (editMode) {

            btnDelete.setDisable(selectedProfile == null);
            actionsRow.getChildren().add(btnDelete);
        } else {

            btnViewProfile.setDisable(true);
            btnPlay.setDisable(true);
            actionsRow.getChildren().addAll(btnViewProfile, btnPlay);
        }

        actionsRow.requestLayout();
    }


    private void refreshCards() {
        cardFlow.getChildren().clear();
        for (UserProfile p : profiles)
            cardFlow.getChildren().add(buildProfileCard(p));

        if (!editMode)
            cardFlow.getChildren().add(buildAddCard());
    }

    private VBox buildProfileCard(UserProfile p) {
        boolean sel = p.equals(selectedProfile);


        Color normalFill   = editMode ? Color.rgb(60, 30, 30)  : Color.rgb(50, 50, 50);
        Color selFill      = editMode ? Color.rgb(100, 20, 20) : Color.rgb(60, 55, 10);
        Color normalStroke = editMode ? Color.rgb(140, 60, 60) : Color.rgb(90, 90, 90);
        Color selStroke    = editMode ? Color.rgb(220, 60, 60) : Color.GOLD;

        Rectangle bg = new Rectangle(155, 80);
        bg.setFill(sel ? selFill : normalFill);
        bg.setArcWidth(10); bg.setArcHeight(10);
        bg.setStroke(sel ? selStroke : normalStroke);
        bg.setStrokeWidth(sel ? 2 : 1);

        Text name = new Text(truncate(p.getUsername(), 14));
        name.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        name.setFill(sel ? (editMode ? Color.rgb(255, 110, 110) : Color.GOLD) : Color.LIGHTGRAY);

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
            if (editMode) {
                btnDelete.setDisable(false);

            } else {
                btnViewProfile.setDisable(false);
                btnPlay.setDisable(false);
            }
            refreshCards();
            actionsRow.requestLayout();
        });

        card.setOnMouseEntered(ev -> {
            if (!p.equals(selectedProfile))
                bg.setFill(editMode ? Color.rgb(80, 38, 38) : Color.rgb(65, 65, 65));
        });
        card.setOnMouseExited(ev -> {
            if (!p.equals(selectedProfile)) bg.setFill(normalFill);
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




    private void showDeleteConfirmDialog() {
        if (selectedProfile == null) return;
        openOverlay();

        Rectangle dBg = panelBg(420, 250);

        Text dTitle = new Text("DELETE PROFILE");
        dTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        dTitle.setFill(Color.rgb(220, 60, 60));

        Text warning = new Text(
                "Are you sure you want to delete\n\""
                        + selectedProfile.getUsername()
                        + "\"?\n\nThis cannot be undone.");
        warning.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        warning.setFill(Color.LIGHTGRAY);
        warning.setTextAlignment(TextAlignment.CENTER);

        Label err = errorLabel();

        Button btnConfirm = new Button("DELETE");
        btnConfirm.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btnConfirm.setTextFill(Color.WHITE);
        btnConfirm.setBackground(new Background(new BackgroundFill(
                Color.rgb(180, 40, 40), new CornerRadii(4), Insets.EMPTY)));
        btnConfirm.setPadding(new Insets(10, 20, 10, 20));

        Button btnCancel = cancelButton(() -> overlayPane.setVisible(false));

        btnConfirm.setOnAction(e -> {
            UserProfile toDelete = selectedProfile;
            new Thread(() -> {
                boolean ok = UserDatabaseService.getInstance().deleteUser(toDelete.getStudentId());
                javafx.application.Platform.runLater(() -> {
                    if (ok) {
                        profiles.remove(toDelete);
                        selectedProfile = null;
                        btnDelete.setDisable(true);
                        overlayPane.setVisible(false);
                        refreshCards();
                    } else {
                        err.setText("DB error – check console.");
                    }
                });
            }).start();
        });

        HBox btns = new HBox(12, btnCancel, btnConfirm);
        btns.setAlignment(Pos.CENTER);

        VBox form = new VBox(16, dTitle, warning, err, btns);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(32));
        form.setMaxWidth(420);

        showDialog(new StackPane(dBg, form));
    }

    private void showPasswordDialog() {
        if (selectedProfile == null) return;
        openOverlay();

        Rectangle dBg = panelBg(380, 260);

        Text dTitle = new Text("ENTER PASSWORD");
        dTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        dTitle.setFill(Color.GOLD);

        Text sub = new Text("Profile: " + selectedProfile.getUsername());
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
            if (selectedProfile.checkPassword(pfPass.getText())) {
                overlayPane.setVisible(false);
                selectedProfile = UserDatabaseService.getInstance()
                        .loginUser(selectedProfile.getStudentId(), pfPass.getText());
                startGame();
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

    private void showAddDialog() {
        openOverlay();

        Rectangle dBg = panelBg(420, 420);

        Text dTitle = new Text("ADD PROFILE");
        dTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        dTitle.setFill(Color.GOLD);

        TextField tfId   = dialogField("Professor ID");
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
        HBox combos = centred(new HBox(10, cbCourse, cbSection));

        Label err      = errorLabel();
        Button btnSave   = buildButton("SAVE");
        Button btnCancel = cancelButton(() -> overlayPane.setVisible(false));

        btnSave.setOnAction(e -> {
            String sid  = tfId.getText().trim();
            String user = tfName.getText().trim();
            String crs  = cbCourse.getValue();
            String sec  = cbSection.getValue();
            String pw   = pfPass.getText();
            String pw2  = pfConfirm.getText();

            if (sid.isEmpty() || user.isEmpty() || crs == null || sec == null || pw.isEmpty()) {
                err.setText("Please fill in all fields."); return;
            }
            if (!pw.equals(pw2)) {
                err.setText("Passwords do not match."); return;
            }
            if (profiles.stream().anyMatch(p -> p.getStudentId().equalsIgnoreCase(sid))) {
                err.setText("Student ID already exists."); return;
            }

            UserProfile np = new UserProfile(sid, user, crs, sec, pw);
            new Thread(() -> {
                boolean ok = UserDatabaseService.getInstance().registerUser(sid, user, crs, sec, pw);
                javafx.application.Platform.runLater(() -> {
                    if (ok) { profiles.add(np); refreshCards(); overlayPane.setVisible(false); }
                    else    { err.setText("DB error – check console."); }
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

    private void showViewDialog() {
        if (selectedProfile == null) return;
        openOverlay();

        UserProfile         p  = selectedProfile;
        UserDatabaseService db = UserDatabaseService.getInstance();

        Rectangle dBg = panelBg(780, 520);

        Text dTitle = new Text(p.getUsername());
        dTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        dTitle.setFill(Color.GOLD);

        Text sub = new Text(p.getStudentId() + "  ·  " + p.getCourse() + " " + p.getSection());
        sub.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        sub.setFill(Color.LIGHTGRAY);

        VBox missionList = new VBox(8);
        missionList.setPadding(new Insets(6));

        new Thread(() -> {
            List<MissionProgressRow> rows =
                    MissionRepository.getInstance().getLatestSessionProgress(p.getStudentId());
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
        missionScroll.setPrefSize(330, 340);
        missionScroll.setFitToWidth(true);
        missionScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        missionScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; "
                + "-fx-border-color: #444444; -fx-border-width: 1;");

        VBox leftCol = new VBox(10, columnTitle("MISSIONS"), missionScroll);
        leftCol.setAlignment(Pos.TOP_CENTER);

        VBox statsList = new VBox(8,
                statRow("Catch Accuracy",    String.format("%.1f%%", db.getCatchAccuracy(p.getStudentId()))),
                statRow("Highest Combo",     String.valueOf(db.getHighestCombo(p.getStudentId()))),
                statRow("Cheaters Caught",   String.valueOf(db.getCheatersCaught(p.getStudentId()))),
                statRow("False Accusations", String.valueOf(db.getFalseAccusations(p.getStudentId()))),
                statRow("Quizzes Conducted", String.valueOf(db.getQuizzesConducted(p.getStudentId()))),
                statRow("Hours Worked",      String.format("%.1f h", db.getHoursWorked(p.getStudentId())))
        );
        statsList.setAlignment(Pos.TOP_LEFT);
        statsList.setPadding(new Insets(6));

        VBox rightCol = new VBox(10, columnTitle("STATS"), statsList);
        rightCol.setAlignment(Pos.TOP_CENTER);

        HBox columns = new HBox(20, leftCol, new Rectangle(1, 380, Color.rgb(80, 76, 40)), rightCol);
        columns.setAlignment(Pos.TOP_CENTER);

        Button btnClose = buildButton("CLOSE");
        btnClose.setOnAction(e -> overlayPane.setVisible(false));

        VBox content = new VBox(12, dTitle, sub, columns, btnClose);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(36));
        content.setMaxWidth(780);

        showDialog(new StackPane(dBg, content));
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


    private HBox buildMissionRow(MissionProgressRow row) {
        Color barColor = row.completed() ? Color.LIMEGREEN : Color.GOLD;
        double pct = row.type().equals("BOOLEAN")
                ? (row.completed() ? 1.0 : 0.0)
                : Math.min(1.0, (double) row.currentValue() / Math.max(1, row.targetValue()));

        Rectangle track = new Rectangle(140, 8, Color.rgb(60, 60, 60));
        track.setArcWidth(4); track.setArcHeight(4);
        Rectangle fill = new Rectangle(140 * pct, 8, barColor);
        fill.setArcWidth(4); fill.setArcHeight(4);

        StackPane bar = new StackPane(track, fill);
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);

        Text desc = new Text(row.description());
        desc.setFill(row.completed() ? Color.LIMEGREEN : Color.LIGHTGRAY);
        desc.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        desc.setWrappingWidth(160);

        Text prog = new Text(row.progressText());
        prog.setFill(barColor);
        prog.setFont(Font.font("Arial", FontWeight.BOLD, 11));

        VBox left = new VBox(3, desc, bar);
        left.setAlignment(Pos.CENTER_LEFT);

        HBox rowBox = new HBox(10, left, prog);
        rowBox.setAlignment(Pos.CENTER_LEFT);
        rowBox.setPadding(new Insets(4, 8, 4, 8));
        rowBox.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 4;");
        return rowBox;
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


    private Button buildEditButton() {
        Button b = new Button("EDIT");
        b.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        b.setTextFill(Color.LIGHTGRAY);
        b.setBackground(new Background(new BackgroundFill(
                Color.rgb(80, 80, 80), new CornerRadii(4), Insets.EMPTY)));
        b.setPadding(new Insets(8, 16, 8, 16));
        return b;
    }


    private Button buildDeleteButton() {
        Button b = new Button("DELETE");
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

    private HBox centred(HBox box) {
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }
}