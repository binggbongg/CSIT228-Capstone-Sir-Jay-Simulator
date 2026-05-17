package com.example.csit228capstonesirjaysimulator.scene;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
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
    private UserProfile             selectedProfile = null;

    private FlowPane  cardFlow;
    private Button    btnViewProfile;
    private Button    btnPlay;
    private VBox      dialogBox;
    private StackPane overlayPane;

    private int loginAttempts = 0;
    private static final int MAX_LOGIN_ATTEMPTS = 5;

    private final ProfileDialogBuilder builder;

    public ProfileSelectScene(javafx.scene.Node parentRoot, Runnable onPlay) {
        this.parentRoot = parentRoot;
        this.onPlay     = onPlay;
        profiles.addAll(UserDatabaseService.getInstance().getAllProfiles());
        builder = new ProfileDialogBuilder(profiles);
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

        HBox actionsRow = buildActionsRow();

        Button btnBack = builder.buildButton("RETURN TO MENU");
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
        btnViewProfile = builder.buildButton("VIEW PROFILE");
        btnViewProfile.setDisable(true);
        btnViewProfile.setOnAction(e -> showViewDialog());

        btnPlay = builder.buildButton("PLAY");
        btnPlay.setDisable(true);
        btnPlay.setOnAction(e -> showPasswordDialogThenPlay());

        HBox row = new HBox(16, btnViewProfile, btnPlay);
        row.setAlignment(Pos.CENTER);
        return row;
    }


    private void showAddDialog() {
        openOverlay();

        TextField     tfId      = builder.dialogField("Teacher ID (00-0000-000)");
        TextField     tfName    = builder.dialogField("Username");
        PasswordField pfPass    = builder.passwordField("Password");
        PasswordField pfConfirm = builder.passwordField("Confirm Password");

        ComboBox<String> cbCourse  = builder.styledCombo("Course",  "CS244", "CSIT228");
        ComboBox<String> cbSection = builder.styledCombo("Section", "F1", "F2", "F3");


        String comboStyle =
                "-fx-background-color:#2a2a2a;" +
                        "-fx-border-color: gold;" +
                        "-fx-border-width:1.5;" +
                        "-fx-border-radius:4;" +
                        "-fx-background-radius:4;" +
                        "-fx-font-size:13px;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #aaaaaa;";

        cbCourse.setStyle(comboStyle);
        cbSection.setStyle(comboStyle);

        cbCourse.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Course" : item);
                setStyle("-fx-text-fill: white; -fx-font-size:13px; -fx-background-color: transparent;");
            }
        });

        cbSection.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Section" : item);
                setStyle("-fx-text-fill: white; -fx-font-size:13px; -fx-background-color: transparent;");
            }
        });


        javafx.scene.control.Label lblCourse = new javafx.scene.control.Label("Course");
        lblCourse.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblCourse.setTextFill(Color.GOLD);

        javafx.scene.control.Label lblSection = new javafx.scene.control.Label("Section");
        lblSection.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblSection.setTextFill(Color.GOLD);

        VBox courseBox   = new VBox(4, lblCourse, cbCourse);
        VBox sectionBox  = new VBox(4, lblSection, cbSection);
        HBox combos      = new HBox(10, courseBox, sectionBox);
        combos.setAlignment(Pos.CENTER);

        Label err = builder.errorLabel();


        tfId.setOnAction(e -> tfName.requestFocus());
        tfName.setOnAction(e -> pfPass.requestFocus());
        pfPass.setOnAction(e -> pfConfirm.requestFocus());
        pfConfirm.setOnAction(e -> cbCourse.requestFocus());
        cbCourse.setOnAction(e -> cbSection.requestFocus());


        tfId.textProperty().addListener((obs, oldVal, newVal) -> {
            String digits = newVal.replaceAll("[^\\d]", "");
            if (digits.length() > 9) digits = digits.substring(0, 9);
            StringBuilder fmt = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                if (i == 2 || i == 6) fmt.append('-');
                fmt.append(digits.charAt(i));
            }
            String result = fmt.toString();
            if (!result.equals(newVal)) {
                javafx.application.Platform.runLater(() -> {
                    tfId.setText(result);
                    tfId.positionCaret(result.length());
                });
            }
        });

        Button btnCancel = builder.cancelButton(() -> overlayPane.setVisible(false));
        Button btnSave   = builder.buildButton("SAVE");


        cbSection.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) btnSave.fire();
        });

        btnSave.setOnAction(e -> {
            String tid  = tfId.getText().trim();
            String user = tfName.getText().trim();
            String crs  = cbCourse.getValue();
            String sec  = cbSection.getValue();
            String pw   = pfPass.getText();
            String pw2  = pfConfirm.getText();

            if (tid.isEmpty())                          { err.setText("Teacher ID is required."); return; }
            if (!tid.matches("\\d{2}-\\d{4}-\\d{3}"))  { err.setText("Format must be 00-0000-000."); return; }
            if (user.isEmpty())                         { err.setText("Username is required."); return; }
            if (user.length() > 60)                     { err.setText("Username max 60 characters."); return; }
            if (crs == null)                            { err.setText("Please select a course."); return; }
            if (sec == null)                            { err.setText("Please select a section."); return; }
            if (pw.isEmpty())                           { err.setText("Password is required."); return; }
            if (pw.length() < 8)                        { err.setText("Password min 8 characters."); return; }
            if (!pw.equals(pw2))                        { err.setText("Passwords do not match."); return; }
            if (profiles.stream().anyMatch(p -> p.getTeacherId().equalsIgnoreCase(tid))) {
                err.setText("Teacher ID already exists."); return;
            }

            new Thread(() -> {
                boolean ok = UserDatabaseService.getInstance().registerUser(tid, user, crs, sec, pw);
                javafx.application.Platform.runLater(() -> {
                    if (ok) {
                        profiles.add(new UserProfile(tid, user, crs, sec, pw));
                        refreshCards();
                        overlayPane.setVisible(false);
                    } else {
                        err.setText("DB error — check console.");
                    }
                });
            }).start();
        });

        showDialog(builder.buildAddDialog(tfId, tfName, pfPass, pfConfirm, combos, err, btnSave, btnCancel));
    }

    private void showViewDialog() {
        openOverlay();

        Button btnClose  = builder.buildButton("CLOSE");
        Button btnEdit   = builder.buildButton("EDIT");
        Button btnDelete = builder.buildDeleteButton("DELETE");

        btnClose.setOnAction(e -> overlayPane.setVisible(false));
        btnEdit.setOnAction(e -> showConfirmIdentityDialog(selectedProfile,
                () -> { overlayPane.setVisible(false); showEditDialog(selectedProfile); }));
        btnDelete.setOnAction(e -> showDeleteConfirmStep(selectedProfile));

        showDialog(builder.buildViewDialog(selectedProfile, btnEdit, btnDelete, btnClose));
    }

    private void showEditDialog(UserProfile p) {
        openOverlay();

        TextField     tfName    = builder.dialogField("Username");
        PasswordField pfPass    = builder.passwordField("New Password");
        PasswordField pfConfirm = builder.passwordField("Confirm New Password");
        ComboBox<String> cbCourse  = builder.styledCombo("Course",  "CS244", "CSIT228");
        ComboBox<String> cbSection = builder.styledCombo("Section", "F1", "F2", "F3");
        Label err = builder.errorLabel();

        tfName.setText(p.getUsername());
        cbCourse.setValue(p.getCourse());
        cbSection.setValue(p.getSection());

        Button btnCancel = builder.cancelButton(() -> overlayPane.setVisible(false));
        Button btnSave   = builder.buildButton("SAVE");

        btnSave.setOnAction(e -> {
            String newName = tfName.getText().trim();
            String newCrs  = cbCourse.getValue();
            String newSec  = cbSection.getValue();
            String newPw   = pfPass.getText();
            String newPw2  = pfConfirm.getText();

            if (newName.isEmpty() || newCrs == null || newSec == null) {
                err.setText("Please fill in all fields."); return;
            }
            if (!newPw.isEmpty() && !newPw.equals(newPw2)) {
                err.setText("Passwords do not match."); return;
            }
            String finalPw = newPw.isEmpty() ? p.getPassword() : newPw;

            new Thread(() -> {
                boolean ok = UserDatabaseService.getInstance()
                        .updateUser(p.getTeacherId(), newName, newCrs, newSec, finalPw);
                javafx.application.Platform.runLater(() -> {
                    if (ok) {
                        UserProfile updated = new UserProfile(p.getTeacherId(), newName, newCrs, newSec, finalPw);
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

        showDialog(builder.buildEditDialog(p, tfName, pfPass, pfConfirm, cbCourse, cbSection, err, btnSave, btnCancel));
    }

    private void showConfirmIdentityDialog(UserProfile p, Runnable onSuccess) {
        openOverlay();

        PasswordField pfPass = builder.passwordField("Password");
        Label err = builder.errorLabel();

        Button btnCancel = builder.cancelButton(() -> {
            loginAttempts = 0;
            overlayPane.setVisible(false);
        });
        Button btnOk = builder.buildButton("CONFIRM");

        if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
            pfPass.setDisable(true);
            btnOk.setDisable(true);
            err.setText("Too many attempts. Please restart the app.");
        }

        btnOk.setOnAction(e -> {
            if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
                err.setText("Too many attempts. Please restart the app."); return;
            }
            if (p.checkPassword(pfPass.getText())) {
                loginAttempts = 0;
                onSuccess.run();
            } else {
                loginAttempts++;
                int remaining = MAX_LOGIN_ATTEMPTS - loginAttempts;
                if (remaining <= 0) {
                    err.setText("Too many attempts. Please restart the app.");
                    pfPass.setDisable(true);
                    btnOk.setDisable(true);
                } else {
                    err.setText("Incorrect password. " + remaining + " attempt(s) left.");
                }
                pfPass.clear();
            }
        });

        pfPass.setOnAction(e -> btnOk.fire());

        showDialog(builder.buildConfirmDialog(p, pfPass, err, btnOk, btnCancel));
    }

    private void showDeleteConfirmStep(UserProfile p) {
        openOverlay();

        Button btnCancel  = builder.cancelButton(() -> overlayPane.setVisible(false));
        Button btnConfirm = builder.buildDeleteButton("DELETE");

        btnConfirm.setOnAction(e -> showDeletePasswordStep(p));

        showDialog(builder.buildDeleteConfirmDialog(p, btnConfirm, btnCancel));
    }

    private void showDeletePasswordStep(UserProfile p) {
        openOverlay();

        PasswordField pfPass = builder.passwordField("Password");
        Label err = builder.errorLabel();

        Button btnCancel = builder.cancelButton(() -> overlayPane.setVisible(false));
        Button btnOk     = builder.buildDeleteButton("DELETE");

        btnOk.setOnAction(e -> {
            if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
                err.setText("Too many attempts. Please restart the app."); return;
            }
            if (p.checkPassword(pfPass.getText())) {
                loginAttempts = 0;
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
                loginAttempts++;
                int remaining = MAX_LOGIN_ATTEMPTS - loginAttempts;
                if (remaining <= 0) {
                    err.setText("Too many attempts. Please restart the app.");
                    pfPass.setDisable(true);
                    btnOk.setDisable(true);
                } else {
                    err.setText("Incorrect password. " + remaining + " attempt(s) left.");
                }
                pfPass.clear();
            }
        });

        pfPass.setOnAction(e -> btnOk.fire());

        showDialog(builder.buildDeletePasswordDialog(p, pfPass, err, btnOk, btnCancel));
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
        card.setOnMouseEntered(ev -> { if (!p.equals(selectedProfile)) bg.setFill(Color.rgb(65, 65, 65)); });
        card.setOnMouseExited(ev  -> { if (!p.equals(selectedProfile)) bg.setFill(Color.rgb(50, 50, 50)); });

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


    private void startGame() {
        if (selectedProfile == null) return;
        UserDatabaseService.getInstance().setCurrentUser(selectedProfile);

        new Thread(() ->
                UserDatabaseService.getInstance().updateLastPlayed(selectedProfile.getTeacherId())
        ).start();

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

    private Rectangle panelBg(double w, double h) {
        Rectangle r = new Rectangle(w, h);
        r.setFill(Color.rgb(30, 30, 30, 0.9));
        r.setArcWidth(20); r.setArcHeight(20);
        r.setStroke(Color.GOLD); r.setStrokeWidth(2);
        r.setEffect(new DropShadow(20, Color.BLACK));
        return r;
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }
}