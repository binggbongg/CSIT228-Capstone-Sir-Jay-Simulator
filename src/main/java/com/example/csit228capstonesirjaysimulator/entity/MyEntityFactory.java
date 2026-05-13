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
import org.w3c.dom.Text;

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
        Texture texture = FXGL.getAssetLoader().loadTexture("Chair.PNG");
        texture.setFitWidth(150);
        texture.setFitHeight(150);

        return FXGL.entityBuilder(data)
                .type(EntityType.CHAIR)
                .view(texture)
                .zIndex(5)
                .build();
    }
}
