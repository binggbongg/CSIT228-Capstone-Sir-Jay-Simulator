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
import com.example.csit228capstonesirjaysimulator.scene.IntroCutScene;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.getInput;
import static com.almasb.fxgl.dsl.FXGL.onBtnDown;
import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class GameLevelApp extends GameApplication {
    private final MyEntityFactory factory = new MyEntityFactory();
    private boolean isRight;
    private ImageView leftButton;
    private ImageView rightButton;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setHeight(720);
        gameSettings.setWidth(1280);
        gameSettings.setTitle("Sir Serato Simulator");
//        gameSettings.setDeveloperMenuEnabled(true);
        // turn developer menu on for debugging stuff
        gameSettings.setMainMenuEnabled(true);
        gameSettings.setSceneFactory(new MenuFactory());
    }

    @Override
    protected void initUI(){
        //temporary ui for score checking
        showScoreText();
        setupButtons();
        updateRoomView(isRight);
    }

    private void setupButtons(){

        Image leftBtnImg = new Image("assets/textures/button-left.png");
        Image rightBtnImg = new Image("assets/textures/button-right.png");

        leftButton = new ImageView(leftBtnImg);
        rightButton = new ImageView(rightBtnImg);

        leftButton.setFitWidth(50);
        leftButton.setPreserveRatio(true);
        rightButton.setFitWidth(50);
        rightButton.setPreserveRatio(true);

        leftButton.setOnMouseClicked( e -> updateRoomView(false));
        rightButton.setOnMouseClicked(e -> updateRoomView(true));

        leftButton.setTranslateX(60);
        leftButton.setTranslateY(360);

        rightButton.setTranslateX(1200);
        rightButton.setTranslateY(360);

        getGameScene().addUINode(leftButton);
        getGameScene().addUINode(rightButton);
    }

    private void showScoreText(){
        Text scoreText = new Text("SCORE");
        scoreText.setTranslateX(50);
        scoreText.setTranslateY(100);
        scoreText.setFill(Color.BLACK);
        scoreText.setFont(Font.font("verdana", 24));
        getGameScene().addUINode(scoreText);

        Text scoreVarText = new Text();
        scoreVarText.setTranslateX(150);
        scoreVarText.setTranslateY(100);
        scoreVarText.setFill(Color.BLACK);
        scoreVarText.setFont(Font.font("verdana", 24));
        scoreVarText.textProperty().bind(getip("score").asString());
        getGameScene().addUINode(scoreVarText);

        Text livesText = new Text();
        livesText.setTranslateX(300);
        livesText.setTranslateY(100);
        livesText.setFill(Color.BLACK);
        livesText.setFont(Font.font("verdana", 24));
        livesText.textProperty().bind(getip("lives").asString());
        getGameScene().addUINode(livesText);

        Text multText = new Text();
        multText.setTranslateX(350);
        multText.setTranslateY(100);
        multText.setFill(Color.BLACK);
        multText.setFont(Font.font("verdana", 24));
        multText.textProperty().bind(getip("mult").asString());
        getGameScene().addUINode(multText);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars){
        vars.put("score", 0);
        vars.put("lives", 3);
        vars.put("mult", 1);
        vars.put("streak", 0);
    }

    @Override
    protected void initGame() {
        isRight = false;
        FXGL.getGameWorld().addEntityFactory(factory);

        spawnStudentGrid(false);
        spawnStudentGrid(true);

        // setting initial state (left side of classroom)
        updateRoomView(false);

        getip("lives").addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() <= 0) {
                showGameOver();
            }
        });
    }

    private void showGameOver(){
        FXGL.getSceneService().pushSubScene(new FinishScene());
    }

    private void updateRoomView(boolean isRightSide) {
        isRight = isRightSide;

        String filename = (isRight) ? "teacherView_rightSide.PNG" : "teacherView_leftSide.PNG";
        FXGL.getGameScene().setBackgroundRepeat(filename);

        FXGL.getGameWorld().getEntitiesByType(EntityType.STUDENT).forEach(e -> {
            boolean studentIsRight = e.getBoolean("isRightSide");
            e.setVisible(studentIsRight == isRightSide);
        });

        if(leftButton != null && rightButton != null) {
            leftButton.setVisible(isRight);
            rightButton.setVisible(!isRight);
        }
    }

    private void spawnStudentGrid(boolean isRight) {
        int rows = 3, cols = 3, spacing = 150;
        double startX = (getAppWidth() - (cols - 1) * spacing) / 2.0 - 60;
        double startY = (getAppHeight() - (rows - 1) * spacing) / 2.0 - 60;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // We pass the boolean "isRightSide" into the SpawnData
                spawn("student", new SpawnData(startX + (c * spacing), startY + (r * spacing))
                        .put("isRightSide", isRight));
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
                handleSpacebar();
            }
        };

        input.addAction(switchScreenLeft, KeyCode.A);
        input.addAction(switchScreenRight, KeyCode.D);
        input.addAction(shushStudents, KeyCode.E);
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

    private void handleSpacebar(){
        // shush all students
        if(FXGL.geti("streak") > 6){
            for (Entity e : getGameWorld().getEntitiesByType(EntityType.STUDENT)) {
                StudentComponent s = e.getComponent(StudentComponent.class);
                s.changeState(new IdleState(s));
            }
        }
    }
}
