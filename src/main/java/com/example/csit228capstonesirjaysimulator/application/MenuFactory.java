package com.example.csit228capstonesirjaysimulator.application;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.example.csit228capstonesirjaysimulator.scene.MainMenuApp;
import org.jetbrains.annotations.NotNull;

public class MenuFactory extends SceneFactory {
    @NotNull
    @Override
    public FXGLMenu newMainMenu(){
        return new MainMenuApp();
    }
}
