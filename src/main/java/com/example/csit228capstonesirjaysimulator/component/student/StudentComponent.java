package com.example.csit228capstonesirjaysimulator.component.student;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.scene.effect.ColorAdjust;
import javafx.util.Duration;

public class StudentComponent extends Component {
    private StudentState state;
    private ColorAdjust currentColor = new ColorAdjust();
    private AnimatedTexture texture;
    private AnimationChannel animIdle, animSus, animCheat, animDistract;
    private String prefix;

    public StudentComponent(String prefix){
        this.prefix = prefix;
    }
    @Override
    public void onAdded() {
        double speed = 2.0;
        animIdle  = new AnimationChannel(FXGL.getAssetLoader().loadTexture(prefix+ "_idle_state.png").getImage(), 9, 189, 354, Duration.seconds(speed), 0, 2);
        animSus   = new AnimationChannel(FXGL.getAssetLoader().loadTexture(prefix+"_sus_state.png").getImage(),  9, 189, 354, Duration.seconds(speed), 0, 2);
        animCheat = new AnimationChannel(FXGL.getAssetLoader().loadTexture(prefix+"_cheat_state.png").getImage(), 9, 189, 354, Duration.seconds(speed), 0, 2);
        animDistract = new AnimationChannel(FXGL.getAssetLoader().loadTexture(prefix+"_talking_state.png").getImage(),9, 189, 354, Duration.seconds(speed), 0, 2);

        texture = new AnimatedTexture(animIdle);
        texture.setPreserveRatio(true);
        texture.setFitHeight(80);

        texture.setSmooth(false);
        texture.loop();

        entity.getBoundingBoxComponent().clearHitBoxes();
        double offsetX = 50;
        double offsetY = 90;

        entity.getBoundingBoxComponent().addHitBox(
                new HitBox("BODY", new Point2D(offsetX, offsetY), BoundingShape.box(100, 170))
        );

        entity.getViewComponent().addChild(texture);
        if (prefix.contains("trey")) {
            changeState(new DistractIdleState(this));
        } else {
            changeState(new IdleState(this));
        }
    }
    @Override
    public void onUpdate(double tpf) {
        if(state != null){
            state.onUpdate(tpf);
        }
    }

    public void onProctorClick() {
        if (state != null) {
            state.onAction();
        }
    }

    public void changeState(StudentState newState){
        state = newState;
    }

    public void playIdleAnimation() {
        texture.loopNoOverride(animIdle);
    }

    public void playSuspiciousAnimation() {
        texture.loopNoOverride(animSus);
    }

    public void playCheatingAnimation() {
        texture.loopNoOverride(animCheat);
    }

    public void playDistractAnimation() {
        texture.loopNoOverride(animDistract);
    }

    public void setHue(double value){
        var children = entity.getViewComponent().getChildren();
        if(children.isEmpty()) return;

        currentColor.setHue(value);
        children.getFirst().setEffect(value == 0 ? null : currentColor);
    }

    public void setBrightness(double value){
        currentColor.setBrightness(value);

        if (texture != null) {
            texture.setEffect(currentColor);
        }
    }

    public String getPrefix() {
        return prefix;
    }
}
