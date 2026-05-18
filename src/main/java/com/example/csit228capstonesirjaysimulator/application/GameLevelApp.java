package com.example.csit228capstonesirjaysimulator.application;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.example.csit228capstonesirjaysimulator.component.student.DistractIdleState;
import com.example.csit228capstonesirjaysimulator.component.student.IdleState;
import com.example.csit228capstonesirjaysimulator.component.student.StudentComponent;
import com.example.csit228capstonesirjaysimulator.database.UserDatabaseService;
import com.example.csit228capstonesirjaysimulator.entity.EntityType;
import com.example.csit228capstonesirjaysimulator.entity.MyEntityFactory;
import com.example.csit228capstonesirjaysimulator.scene.FinishScene;
import com.example.csit228capstonesirjaysimulator.scene.PauseScene;
import com.example.csit228capstonesirjaysimulator.scene.TutorialScene;
import com.example.csit228capstonesirjaysimulator.util.AudioManager;
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
import static com.almasb.fxgl.dsl.FXGL.spawn;
import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

import com.example.csit228capstonesirjaysimulator.database.Sessionstats;
import com.example.csit228capstonesirjaysimulator.component.mission.Mission;
import com.example.csit228capstonesirjaysimulator.database.MissionRepository;

import java.time.Instant;

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
        gameSettings.setMainMenuEnabled(true);
        gameSettings.setMenuKey(KeyCode.F12);
        gameSettings.setSceneFactory(new MenuFactory());
        gameSettings.setAppIcon("sir_serato_icon.png");
    }

    @Override
    protected void initUI(){
        showScoreText();
        setupButtons();
        updateRoomView(FXGL.getb("isRight"));
    }

    private void setupButtons(){

        Image pauseBtnImg = new Image("assets/textures/button_pause.png");
        Image leftBtnImg = new Image("assets/textures/button_left.png");
        Image rightBtnImg = new Image("assets/textures/button_right.png");

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
        Image dashboardImg = new Image("assets/textures/game_screen_dashboard.png");
        ImageView gameDashboard = new ImageView(dashboardImg);

        gameDashboard.setFitWidth(300);
        gameDashboard.setPreserveRatio(true);

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
        livesText.setTranslateX(50);
        livesText.setTranslateY(186);
        livesText.setFill(Color.BLACK);
        livesText.setFont(jelleeBody);
        getGameScene().addUINode(livesText);

        Text livesVarText = new Text();
        livesVarText.setTranslateX(50);
        livesVarText.setTranslateY(211);
        livesVarText.setFill(Color.BLACK);
        livesVarText.setFont(jelleeHeading);
        livesVarText.textProperty().bind(getip("lives").asString());
        getGameScene().addUINode(livesVarText);

        Text multText = new Text("MULTIPLIER");
        multText.setTranslateX(50);
        multText.setTranslateY(241);
        multText.setFill(Color.BLACK);
        multText.setFont(jelleeBody);
        getGameScene().addUINode(multText);

        Text multVarText = new Text();
        multVarText.setTranslateX(50);
        multVarText.setTranslateY(266);
        multVarText.setFill(Color.BLACK);
        multVarText.setFont(jelleeHeading);
        multVarText.textProperty().bind(getip("mult").asString());
        getGameScene().addUINode(multVarText);

        // Streak indicator ImageView
        streakIconNormal = new Image("assets/textures/sir_serato_icon.png");
        streakIconAngry = new Image("assets/textures/sir_serato_icon_angry.png");
        streakIconAngrier = new Image("assets/textures/sir_serato_icon_angrier.png");

        streakIndicator = new ImageView(streakIconNormal);
        streakIndicator.setFitWidth(80);
        streakIndicator.setPreserveRatio(true);
        streakIndicator.setTranslateX(150);
        streakIndicator.setTranslateY(100);

        getGameScene().addUINode(streakIndicator);

        Image buttonTImg = new Image("assets/textures/button_T.png");
        ImageView buttonT = new ImageView(buttonTImg);
        buttonT.setFitWidth(50);
        buttonT.setPreserveRatio(true);
        buttonT.setTranslateX(50);
        buttonT.setTranslateY(560);
        getGameScene().addUINode(buttonT);

        Text openTutorialText = new Text("Open tutorial");
        openTutorialText.setTranslateX(120);
        openTutorialText.setTranslateY(600);
        openTutorialText.setFill(Color.WHITE);
        openTutorialText.setFont(jelleeHeading);
        getGameScene().addUINode(openTutorialText);

        Image buttonEImg = new Image("assets/textures/button_E.png");
        ImageView buttonE = new ImageView(buttonEImg);
        buttonE.setFitWidth(50);
        buttonE.setPreserveRatio(true);
        buttonE.setTranslateX(50);
        buttonE.setTranslateY(630);
        getGameScene().addUINode(buttonE);

        Text shushText = new Text("Shush students");
        shushText.setTranslateX(120);
        shushText.setTranslateY(660);
        shushText.setFill(Color.WHITE);
        shushText.setFont(jelleeHeading);
        getGameScene().addUINode(shushText);
    }

    /**
     * Updates the streak indicator image based on the current streak value.
     * - streak > 6: sir_serato_icon_angrier.png
     * - streak > 3: sir_serato_icon_angry.png
     * - streak <= 3 (less than 6): sir_serato_icon.png
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

        try {
            Thread.sleep(1000); //artificial delay for loading screen
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        AudioManager.getInstance();
        AudioManager.getInstance().setMusicVolume(0.5);
        AudioManager.getInstance().setSoundVolume(1);
        AudioManager.getInstance().playMusic("chocolate-milk.mp3");
        FXGL.getGameWorld().addEntityFactory(factory);

        spawnChairs();

        spawnStudentGrid(false);
        spawnStudentGrid(true);

        sessionStats = new Sessionstats();
        sessionStart = Instant.now().getEpochSecond();

        sessionMissions = MissionRepository.getInstance().loadAllMissions(UserDatabaseService.getInstance().getCurrentUser().getTeacherId());

        FXGL.getWorldProperties().setValue("sessionMissions", sessionMissions);

        // setting initial state (left side of classroom)
        updateRoomView(false);
        spawnTables();

        getip("lives").addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() <= 0 && oldValue.intValue() > 0) {
                showGameOver();
            }
        });

        // Listen for streak changes and update the visual indicator
        getip("streak").addListener((observable, oldValue, newValue) -> {
            updateStreakIndicator(newValue.intValue());
        });
    }

    private void showGameOver(){
        AudioManager.getInstance().stopAllSounds();
        AudioManager.getInstance().stopAllMusicPlaying();

        long now      = Instant.now().getEpochSecond();
        double secs   = now - sessionStart;
        FXGL.getWorldProperties().setValue("sessionDuration", secs);

        FXGL.getSceneService().pushSubScene(new FinishScene());
    }

    private void updateRoomView(boolean isRightSide) {
        if (FXGL.getb("isLocked")) {
            return;
        }

        FXGL.set("isRight", isRightSide);

        getGameWorld().getEntitiesByType(EntityType.BACKGROUND).forEach(Entity::removeFromWorld);
        String filename = isRightSide ? "bg_teacher_view_right.png" : "bg_teacher_view_left.png";
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
        int colSpacing = 290;
        int rowSpacing = 190;

        double startX = 300;
        double startY = 430;

        List<Integer> seatIndices = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            seatIndices.add(i);
        }
        Collections.shuffle(seatIndices);
        int distractorIndex = seatIndices.getFirst();

        int currentSeat = 0;
        for (int r = 0; r < 3; r++) {
            double rowY = startY - (r * rowSpacing);
            int z = 30 - (r * 10);

            for (int c = 0; c < 3; c++) {
                double colX = startX + (c * colSpacing);

                SpawnData data = new SpawnData(colX, rowY)
                        .put("isRightSide", isRight)
                        .put("zIndex", z);

                if (currentSeat == distractorIndex) {
                    spawn("distractor", data);
                } else {
                    spawn("student", data);
                }

                currentSeat++;
            }
        }
    }

    private void spawnChairs(){
        int colSpacing = 290;
        int rowSpacing = 190;

        double startX = 292;
        double startY = 590;

        for (int r = 0; r < 3; r++) {
            double rowY = startY - (r * rowSpacing);

            for (int c = 0; c < 3; c++) {
                double colX = startX + (c * colSpacing);

                spawn("chair", new SpawnData(colX, rowY));
            }

        }
    }

    private void spawnTables(){
        int colSpacing = 290;
        int rowSpacing = 190;

        double startX = 275;
        double startY = 600;

        for (int r = 0; r < 3; r++) {
            double rowY = startY - (r * rowSpacing);

            int z = 30 - (r * 10);

            for (int c = 0; c < 3; c++) {
                double colX = startX + (c * colSpacing);

                spawn("table", new SpawnData(colX, rowY).put("zIndex", z));
            }

        }
    }

    @Override
    protected void initInput() {
        onBtnDown(MouseButton.PRIMARY, () -> {
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

        UserAction openTutorial = new UserAction("Open Tutorial") {
            @Override
            protected void onAction() {
                FXGL.getSceneService().pushSubScene(new TutorialScene());
            }
        };

        input.addAction(switchScreenLeft, KeyCode.A);
        input.addAction(switchScreenRight, KeyCode.D);
        input.addAction(shushStudents, KeyCode.E);
        input.addAction(pauseScreen, KeyCode.ESCAPE);
        input.addAction(openTutorial, KeyCode.T);
    }

    private void handleMouseClick() {
        Point2D mousePoint = getInput().getMousePositionWorld();
        double x = mousePoint.getX();
        double y = mousePoint.getY();

        for (Entity e : getGameWorld().getEntitiesByType(EntityType.STUDENT)) {
            if (!e.isVisible()) {
                continue;
            }
            // Check if the mouse point is inside the entity's current bounding box
            if (e.isVisible() && e.getBoundingBoxComponent().isWithin(x, y, x, y)) {
                e.getComponent(StudentComponent.class).onProctorClick();
                break;
            }
        }
    }

    private void handlePressE(){
        // shush all students
        if(FXGL.geti("streak") > 6){
            AudioManager.getInstance().playSound("swearbymysword1.wav");
            for (Entity e : getGameWorld().getEntitiesByType(EntityType.STUDENT)) {
                StudentComponent s = e.getComponent(StudentComponent.class);
                if (s != null) {
                    // Check if this specific entity is a distractor by checking its asset prefix
                    if (s.getPrefix().contains("trey")) {
                        s.changeState(new DistractIdleState(s));
                    } else {
                        s.changeState(new IdleState(s));
                    }
                }
            }
            streakIndicator.setImage(streakIconNormal);
            FXGL.set("streak", 0);
            FXGL.set("isLocked", false);
        }
    }
}
