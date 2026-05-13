package com.example.csit228capstonesirjaysimulator.component.student;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.scene.effect.ColorAdjust;
import javafx.util.Duration;

public class StudentComponent extends Component {
    private StudentState state;
    //private ColorAdjust currentColor = new ColorAdjust();
    private AnimatedTexture texture;
    private AnimationChannel animIdle, animSus, animCheat, animDistract;
    private String prefix;

    public StudentComponent(String prefix){
        this.prefix = prefix;
    }
    @Override
    public void onAdded() {
        double speed = 2.0;
        animIdle  = new AnimationChannel(FXGL.image(prefix+ "_idle_state.png"), 9, 189, 354, Duration.seconds(speed), 0, 2);
        animSus   = new AnimationChannel(FXGL.image(prefix+"_sus_state.png"),  9, 189, 354, Duration.seconds(speed), 0, 2);
        animCheat = new AnimationChannel(FXGL.image(prefix+"_cheat_state.png"), 9, 189, 354, Duration.seconds(speed), 0, 2);
        animDistract = new AnimationChannel(FXGL.image(prefix+"_talking_state.png"),9, 189, 354, Duration.seconds(speed), 0, 2);

        texture = new AnimatedTexture(animIdle);
        texture.setPreserveRatio(true);
        texture.setFitHeight(120);

        texture.setSmooth(true);
        texture.loop();

        entity.getViewComponent().addChild(texture);
        changeState(new IdleState(this));
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
        texture.setTranslateX(0);
    }

    public void playSuspiciousAnimation() {
        texture.loopNoOverride(animSus);
        texture.setTranslateX(-0.5);
        texture.setTranslateY(0);
    }

    public void playCheatingAnimation() {
        texture.loopNoOverride(animCheat);
        texture.setTranslateX(-3.5);
        texture.setTranslateY(0);
    }

    public void playDistractAnimation() {
        texture.loopNoOverride(animDistract);
        texture.setTranslateX(-4.5);
        texture.setTranslateY(0);
    }


    //    public void setHue(double value){
//        var children = entity.getViewComponent().getChildren();
//        if(children.isEmpty()) return;
//
//        currentColor.setHue(value);
//        children.getFirst().setEffect(value == 0 ? null : currentColor);
//    }
}
