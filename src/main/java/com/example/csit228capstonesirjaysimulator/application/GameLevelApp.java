package com.example.csit228capstonesirjaysimulator.application;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.example.csit228capstonesirjaysimulator.component.student.IdleState;
import com.example.csit228capstonesirjaysimulator.component.student.StudentComponent;
import com.example.csit228capstonesirjaysimulator.entity.EntityType;
import com.example.csit228capstonesirjaysimulator.entity.MyEntityFactory;
import com.example.csit228capstonesirjaysimulator.scene.FinishScene;
import com.example.csit228capstonesirjaysimulator.scene.PauseScene;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.getInput;
import static com.almasb.fxgl.dsl.FXGL.onBtnDown;
import static com.almasb.fxgl.dsl.FXGLForKtKt.*;



import com.example.csit228capstonesirjaysimulator.database.Sessionstats;
import com.example.csit228capstonesirjaysimulator.component.mission.Mission;
import com.example.csit228capstonesirjaysimulator.database.MissionRepository;

import java.time.Instant;
import java.util.List;

public class GameLevelApp extends GameApplication {
    private final MyEntityFactory factory = new MyEntityFactory();
    private boolean isRight;
    private ImageView leftButton, rightButton, pauseButton;
    private ImageView streakIndicator;

    // Preloaded streak images
    private Image streakIconNormal;
    private Image streakIconAngry;
    private Image streakIconAngrier;

    private Sessionstats sessionStats;
    private long sessionStart;

    private List<Mission<?>> sessionMissions;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setHeight(720);
        gameSettings.setWidth(1280);
        gameSettings.setTitle("Sir Serato Simulator");
//        gameSettings.setDeveloperMenuEnabled(true);
        // turn developer menu on for debugging stuff
        gameSettings.setMainMenuEnabled(true);
        gameSettings.setMenuKey(KeyCode.F12);
        gameSettings.setSceneFactory(new MenuFactory());
    }

    @Override
    protected void initUI(){
        showScoreText();
        setupButtons();
        updateRoomView(FXGL.getb("isRight"));
    }

    private void setupButtons(){

        Image pauseBtnImg = new Image("assets/textures/button-pause.png");
        Image leftBtnImg = new Image("assets/textures/button-left.png");
        Image rightBtnImg = new Image("assets/textures/button-right.png");

        pauseButton = new ImageView(pauseBtnImg);
        leftButton = new ImageView(leftBtnImg);
        rightButton = new ImageView(rightBtnImg);

        leftButton.setFitWidth(50);
        leftButton.setPreserveRatio(true);
        rightButton.setFitWidth(50);
        rightButton.setPreserveRatio(true);

        pauseButton.setFitWidth(50);
        pauseButton.setPreserveRatio(true);

        pauseButton.setOnMouseClicked(e -> {
            FXGL.getSceneService().pushSubScene(new PauseScene());
        });

        leftButton.setOnMouseClicked( e -> updateRoomView(false));
        rightButton.setOnMouseClicked(e -> updateRoomView(true));

        leftButton.setTranslateX(60);
        leftButton.setTranslateY(360);

        rightButton.setTranslateX(1200);
        rightButton.setTranslateY(360);

        pauseButton.setTranslateX(1200);
        pauseButton.setTranslateY(60);

        getGameScene().addUINode(leftButton);
        getGameScene().addUINode(rightButton);
        getGameScene().addUINode(pauseButton);
    }

    private void showScoreText(){
        Image dashboardImg = new Image("assets/textures/game-screen_dashboard.png");
        ImageView gameDashboard = new ImageView(dashboardImg);

        gameDashboard.setFitWidth(600);
        gameDashboard.setPreserveRatio(true);
        gameDashboard.setTranslateX(-30);
        gameDashboard.setTranslateY(-20);

        getGameScene().addUINode(gameDashboard);

        Font jelleeScore = Font.loadFont(getClass().getResourceAsStream("/fonts/Jellee-Roman.ttf"), 30);
        Font jelleeBody = Font.loadFont(getClass().getResourceAsStream("/fonts/Jellee-Roman.ttf"), 12);
        Font jelleeHeading = Font.loadFont(getClass().getResourceAsStream("/fonts/Jellee-Roman.ttf"), 24);

        Text scoreText = new Text("SCORE");
        scoreText.setTranslateX(50);
        scoreText.setTranslateY(125);
        scoreText.setFill(Color.BLACK);
        scoreText.setFont(jelleeBody);
        getGameScene().addUINode(scoreText);

        Text scoreVarText = new Text();
        scoreVarText.setTranslateX(50);
        scoreVarText.setTranslateY(156);
        scoreVarText.setFill(Color.BLACK);
        scoreVarText.setFont(jelleeScore);
        scoreVarText.textProperty().bind(getip("score").asString());
        getGameScene().addUINode(scoreVarText);

        Text livesText = new Text("LIVES");
        livesText.setTranslateX(200);
        livesText.setTranslateY(125);
        livesText.setFill(Color.BLACK);
        livesText.setFont(jelleeBody);
        getGameScene().addUINode(livesText);

        Text livesVarText = new Text();
        livesVarText.setTranslateX(200);
        livesVarText.setTranslateY(150);
        livesVarText.setFill(Color.BLACK);
        livesVarText.setFont(jelleeHeading);
        livesVarText.textProperty().bind(getip("lives").asString());
        getGameScene().addUINode(livesVarText);

        Text multText = new Text("MULTIPLIER");
        multText.setTranslateX(300);
        multText.setTranslateY(125);
        multText.setFill(Color.BLACK);
        multText.setFont(jelleeBody);
        getGameScene().addUINode(multText);

        Text multVarText = new Text();
        multVarText.setTranslateX(300);
        multVarText.setTranslateY(150);
        multVarText.setFill(Color.BLACK);
        multVarText.setFont(jelleeHeading);
        multVarText.textProperty().bind(getip("mult").asString());
        getGameScene().addUINode(multVarText);

        // Streak indicator ImageView
        streakIconNormal = new Image("assets/textures/sir_serato-icon.png");
        streakIconAngry = new Image("assets/textures/sir_serato-icon-angry.png");
        streakIconAngrier = new Image("assets/textures/sir_serato-icon-angrier.png");

        streakIndicator = new ImageView(streakIconNormal);
        streakIndicator.setFitWidth(80);
        streakIndicator.setPreserveRatio(true);
        streakIndicator.setTranslateX(400);
        streakIndicator.setTranslateY(100);

        getGameScene().addUINode(streakIndicator);
    }

    /**
     * Updates the streak indicator image based on the current streak value.
     * - streak > 6: sir_serato-icon-angrier.png
     * - streak > 3: sir_serato-icon-angry.png
     * - streak <= 3 (less than 6): sir_serato-icon.png
     */

    private void updateStreakIndicator(int streak) {
        if (streak > 6) {
            streakIndicator.setImage(streakIconAngrier);
        } else if (streak > 3) {
            streakIndicator.setImage(streakIconAngry);
        } else {
            streakIndicator.setImage(streakIconNormal);
        }
    }

    @Override
    protected void initGameVars(Map<String, Object> vars){
        vars.put("score", 0);
        vars.put("lives", 3);
        vars.put("mult", 1);
        vars.put("streak", 0);
        vars.put("isLocked", false);
        vars.put("isRight", false);

        vars.put("sessionCheatersCaught",0);
        vars.put("sessionFalseAccusations",0);
        vars.put("sessionTotalAttempts",0);
        vars.put("sessionTotalCorrect",0);
        vars.put("sessionDuration",0.0);
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(factory);

        spawnStudentGrid(false);
        spawnStudentGrid(true);

        sessionStats = new Sessionstats();
        sessionStart = Instant.now().getEpochSecond();

        sessionMissions = MissionRepository.getInstance().loadGlobalMissions();
        FXGL.getWorldProperties().setValue("sessionMissions", sessionMissions);




        // setting initial state (left side of classroom)
        updateRoomView(false);

        getip("lives").addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() <= 0) {
                showGameOver();
            }
        });

        // Listen for streak changes and update the visual indicator
        getip("streak").addListener((observable, oldValue, newValue) -> {
            updateStreakIndicator(newValue.intValue());
        });
    }

    private void showGameOver(){
        long now      = Instant.now().getEpochSecond();
        double secs   = now - sessionStart;
        FXGL.getWorldProperties().setValue("sessionDuration", secs);

        FXGL.getSceneService().pushSubScene(new FinishScene());
    }

    private void updateRoomView(boolean isRightSide) {
        if (FXGL.getb("isLocked")) {
            System.out.println("Movement locked! Help the student first.");
            return;
        }

        FXGL.set("isRight", isRightSide);

        getGameWorld().getEntitiesByType(EntityType.BACKGROUND).forEach(Entity::removeFromWorld);
        String filename = isRightSide ? "teacherView_rightSide.PNG" : "teacherView_leftSide.PNG";
        spawn("background", new SpawnData(0, 0).put("texture", filename));


        FXGL.getGameWorld().getEntitiesByType(EntityType.STUDENT).forEach(e ->
            e.setVisible(e.getBoolean("isRightSide") == isRightSide)
        );

        if(leftButton != null && rightButton != null) {
            leftButton.setVisible(isRightSide);
            rightButton.setVisible(!isRightSide);
        }
    }

    private void spawnStudentGrid(boolean isRight) {
        int rows = 3, cols = 3, spacing = 190;
        double startX = (getAppWidth() - (cols - 1) * spacing) / 2.0 - 60;
        double startY = (getAppHeight() - (rows - 1) * spacing) / 2.0 - 60;

        List<Integer> seatIndices = new ArrayList<>();
        for (int i = 0; i < (rows * cols); i++) {
            seatIndices.add(i);
        }

        Collections.shuffle(seatIndices);
        List<Integer> distractorSeats = seatIndices.subList(0, 1);

        int currentSeat = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double x = startX + (c * spacing);
                double y = startY + (r * spacing);

                SpawnData data = new SpawnData(x, y).put("isRightSide", isRight);

                // 4. Decide which entity type to spawn based on our shuffled list
                if (distractorSeats.contains(currentSeat)) {
                    spawn("distractor", data);
                } else {
                    spawn("student", data);
                }

                currentSeat++;
            }
        }
    }

    @Override
    protected void initInput() {
        // Explicitly call the handleMouseClick method inside a void lambda
        onBtnDown(MouseButton.PRIMARY, () -> {
            System.out.println("Mouse is clicking");
            handleMouseClick();
        });

        Input input = FXGL.getInput();

        UserAction pauseScreen = new UserAction("Pause Screen"){
            @Override
            protected void onAction() {
                FXGL.getSceneService().pushSubScene(new PauseScene());
            }
        };

        UserAction switchScreenRight = new UserAction("Switch Screen Right"){
            @Override
            protected void onAction(){
                updateRoomView(true);
            }
        };

        UserAction switchScreenLeft = new UserAction("Switch Screen Left"){
            @Override
            protected void onAction(){
                updateRoomView(false);
            }
        };

        UserAction shushStudents = new UserAction("PAGHILOM!") {
            @Override
            protected void onAction(){
                handlePressE();
            }
        };

        input.addAction(switchScreenLeft, KeyCode.A);
        input.addAction(switchScreenRight, KeyCode.D);
        input.addAction(shushStudents, KeyCode.E);
        input.addAction(pauseScreen, KeyCode.ESCAPE);
    }

    private void handleMouseClick() {
        Point2D mousePoint = getInput().getMousePositionWorld();
        double x = mousePoint.getX();
        double y = mousePoint.getY();

        for (Entity e : getGameWorld().getEntitiesByType(EntityType.STUDENT)) {
            // Check if the mouse point is inside the entity's current bounding box
            if (e.isVisible() && e.getBoundingBoxComponent().isWithin(x, y, x, y)) {
                System.out.println("student is hit");
                e.getComponent(StudentComponent.class).onProctorClick();
                break;
            }
        }
    }

    private void handlePressE(){
        // shush all students
        if(FXGL.geti("streak") > 6){
            System.out.println("Shush is activated");
            for (Entity e : getGameWorld().getEntitiesByType(EntityType.STUDENT)) {
                StudentComponent s = e.getComponent(StudentComponent.class);
                if(s != null) s.changeState(new IdleState(s));
            }
            streakIndicator.setImage(streakIconNormal);
            FXGL.set("streak", 0);
            FXGL.set("isLocked", false);
        }
    }


    @SuppressWarnings("unchecked")
    private void updateMissionsOnCheaterCaught() {
        if (sessionMissions == null) return;
        for (Mission<?> m : sessionMissions) {
            if (m.getMissionId() == 1 || m.getMissionId() == 2) {
                ((Mission<Integer>) m).increment();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void updateMissionsOnStreak(int streak) {
        if (sessionMissions == null) return;
        for (Mission<?> m : sessionMissions) {
            if (m.getMissionId() == 3)
                ((Mission<Integer>) m).setCurrent(streak);
            else if (m.getMissionId() == 4)
                ((Mission<Integer>) m).setCurrent(streak);
        }
    }

    @SuppressWarnings("unchecked")
    private void updateMissionsOnScore(int score) {
        if (sessionMissions == null) return;
        for (Mission<?> m : sessionMissions) {
            if (m.getMissionId() == 6)
                ((Mission<Integer>) m).setCurrent(score);
            else if (m.getMissionId() == 7)
                ((Mission<Integer>) m).setCurrent(score);
        }
    }

    @SuppressWarnings("unchecked")
    private void updateMissionsOnMultiplier(int mult) {
        if (sessionMissions == null) return;
        for (Mission<?> m : sessionMissions) {
            if (m.getMissionId() == 8 && mult >= 3)
                ((Mission<Boolean>) m).complete();
            else if (m.getMissionId() == 9 && mult >= 5)
                ((Mission<Boolean>) m).complete();
        }
    }




}
