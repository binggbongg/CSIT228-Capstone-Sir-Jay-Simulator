package com.example.csit228capstonesirjaysimulator.entity;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;
import com.example.csit228capstonesirjaysimulator.component.student.StudentComponent;

import java.util.*;

public class MyEntityFactory implements EntityFactory {
    private List<String> listOfPrefixes = List.of("stud1", "stud2", "stud3", "stud4");

    private String getRandomPrefix(){
        return listOfPrefixes.get(FXGL.random(0, listOfPrefixes.size()-1));
    }

    @Spawns("student")
    public Entity newStudent(SpawnData data) {
        StudentComponent student = new StudentComponent("students/"+getRandomPrefix());

        return baseStudent(data, student);
    }

    @Spawns("distractor")
    public Entity newDistractor(SpawnData data) {
        StudentComponent student = new StudentComponent("students/trey");

        return baseStudent(data, student);
    }

    private Entity baseStudent(SpawnData data, StudentComponent brain) {
        int z = data.get("zIndex");

        return FXGL.entityBuilder(data)
                .type(EntityType.STUDENT)
                .bbox(new HitBox(BoundingShape.box(100,150)))
                .zIndex(z)
                .collidable()
                .with("isRightSide", data.get("isRightSide"))
                .with(brain)
                .build();
    }

    @Spawns("background")
    public Entity gameBg(SpawnData data){
        String name = data.get("texture");
        Texture texture = FXGL.getAssetLoader().loadTexture(name);
        texture.setFitHeight(720);
        texture.setFitWidth(1280);

        return FXGL.entityBuilder(data)
                .type(EntityType.BACKGROUND)
                .view(texture)
                .zIndex(-100)
                .build();
    }

    @Spawns("chair")
    public Entity newChair(SpawnData data){
        Texture texture = FXGL.getAssetLoader().loadTexture("chair.png");
        texture.setFitWidth(210);
        texture.setFitHeight(210);

        return FXGL.entityBuilder(data)
                .type(EntityType.CHAIR)
                .view(texture)
                .zIndex(5)
                .build();
    }

    @Spawns("table")
    public Entity newTable(SpawnData data){
        Texture texture = FXGL.getAssetLoader().loadTexture("table.png");
        texture.setFitWidth(240);
        texture.setFitHeight(240);

        int z = data.get("zIndex");

        return FXGL.entityBuilder(data)
                .type(EntityType.TABLE)
                .view(texture)
                .zIndex(z)
                .build();
    }
}

