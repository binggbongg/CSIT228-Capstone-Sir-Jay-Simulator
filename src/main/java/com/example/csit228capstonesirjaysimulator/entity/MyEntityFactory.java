package com.example.csit228capstonesirjaysimulator.entity;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;
import com.example.csit228capstonesirjaysimulator.component.student.DistractIdleState;
import com.example.csit228capstonesirjaysimulator.component.student.StudentComponent;
import com.example.csit228capstonesirjaysimulator.component.student.IdleState;
import org.w3c.dom.Text;

public class MyEntityFactory implements EntityFactory {
    @Spawns("student")
    public Entity newStudent(SpawnData data) {
        StudentComponent student = new StudentComponent("stud5");

        Entity e = baseStudent(data, student);

        student.changeState(new IdleState(student));
        return e;
    }

    @Spawns("distractor")
    public Entity newDistractor(SpawnData data) {
        StudentComponent student = new StudentComponent("trey");

        Entity e = baseStudent(data, student);

        student.changeState(new DistractIdleState(student));
        return e;
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
        Texture texture = FXGL.getAssetLoader().loadTexture("Chair.PNG");
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
        Texture texture = FXGL.getAssetLoader().loadTexture("Table.PNG");
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
