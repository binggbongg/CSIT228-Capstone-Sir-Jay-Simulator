package com.example.csit228capstonesirjaysimulator.entity;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.texture.Texture;
import com.example.csit228capstonesirjaysimulator.component.student.DistractIdleState;
import com.example.csit228capstonesirjaysimulator.component.student.StudentComponent;
import com.example.csit228capstonesirjaysimulator.component.student.IdleState;
import com.example.csit228capstonesirjaysimulator.component.student.StudentState;

public class MyEntityFactory implements EntityFactory {
    @Spawns("student")
    public Entity newStudent(SpawnData data) {
        StudentComponent student = new StudentComponent();

        Entity e = baseStudent(data, student, "rainbow.jpg");

        student.changeState(new IdleState(student));
        return e;
    }

    @Spawns("distractor")
    public Entity newDistractor(SpawnData data) {
        StudentComponent student = new StudentComponent();

        Entity e = baseStudent(data, student, "applejack.jpg");

        student.changeState(new DistractIdleState(student));
        return e;
    }

    private Entity baseStudent(SpawnData data, StudentComponent brain, String textureName) {
        Texture texture = FXGL.getAssetLoader().loadTexture(textureName);
        texture.setFitWidth(150);
        texture.setFitHeight(150);

        return FXGL.entityBuilder(data)
                .type(EntityType.STUDENT)
                .viewWithBBox(texture)
                .zIndex(10)
                .collidable()
                .with("isRightSide", (boolean)data.get("isRightSide"))
                .with(brain)
                .build();
    }
}
