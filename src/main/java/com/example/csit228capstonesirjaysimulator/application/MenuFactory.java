package com.example.csit228capstonesirjaysimulator.application;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.example.csit228capstonesirjaysimulator.scene.CustomLoadingScene;
import com.example.csit228capstonesirjaysimulator.scene.MainMenuApp;
import org.jetbrains.annotations.NotNull;

public class MenuFactory extends SceneFactory {
    @NotNull
    @Override
    public FXGLMenu newMainMenu(){
        return new MainMenuApp();
    }

    @NotNull
    @Override
    public LoadingScene newLoadingScene() {
        return new CustomLoadingScene();
    }
}
