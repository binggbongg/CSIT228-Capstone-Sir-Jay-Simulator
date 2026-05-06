package com.example.csit228capstonesirjaysimulator.entity;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.texture.Texture;
import com.example.csit228capstonesirjaysimulator.component.student.StudentComponent;

public class MyEntityFactory implements EntityFactory {
    @Spawns("student")
    public Entity newStudent(SpawnData data){
        Texture texture = FXGL.texture("rainbow.jpg");
        texture.setFitHeight(150);
        texture.setFitWidth(150);

        boolean isRight = data.get("isRightSide");

        return FXGL.entityBuilder(data)
                .type(EntityType.STUDENT)
                .viewWithBBox(texture)
                .zIndex(10)
                .collidable()
                .with("isRightSide", isRight)
                .with(new StudentComponent())
                .build();
    }
}
