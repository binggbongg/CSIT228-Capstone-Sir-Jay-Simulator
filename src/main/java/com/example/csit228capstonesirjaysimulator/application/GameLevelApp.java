package com.example.csit228capstonesirjaysimulator.application;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.example.csit228capstonesirjaysimulator.component.student.StudentComponent;
import com.example.csit228capstonesirjaysimulator.entity.EntityType;
import com.example.csit228capstonesirjaysimulator.entity.MyEntityFactory;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.getInput;
import static com.almasb.fxgl.dsl.FXGL.onBtnDown;
import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class GameLevelApp extends GameApplication {
    private final MyEntityFactory factory = new MyEntityFactory();

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setHeight(720);
        gameSettings.setWidth(1280);
        gameSettings.setTitle("Cool and Normal part of game duh");
//        gameSettings.setDeveloperMenuEnabled(true);
        // turn developer menu on for debugging stuff
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(factory);
        int rows = 3;
        int cols = 3;
        int spacing = 150;

        double startX = (getAppWidth() - (cols - 1) * spacing) / 2.0;
        double startY = (getAppHeight() - (rows - 1) * spacing) / 2.0;

        startX -= 60;
        startY -= 60;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                spawn("student", startX + (c * spacing), startY + (r * spacing));
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
    }

    private void handleMouseClick() {
        Point2D mousePoint = getInput().getMousePositionWorld();
        double x = mousePoint.getX();
        double y = mousePoint.getY();

        for (Entity e : getGameWorld().getEntitiesByType(EntityType.STUDENT)) {
            // Check if the mouse point is inside the entity's current bounding box
            if (e.getBoundingBoxComponent().isWithin(x, y, x, y)) {
                System.out.println("Manual hit detected on student!");
                e.getComponent(StudentComponent.class).onProctorClick();
            }
        }
    }
}
