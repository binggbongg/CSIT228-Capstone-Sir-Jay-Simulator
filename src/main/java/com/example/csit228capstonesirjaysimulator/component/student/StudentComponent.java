package com.example.csit228capstonesirjaysimulator.component.student;

import com.almasb.fxgl.entity.component.Component;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;

public class StudentComponent extends Component {
    private StudentState state;
    private ColorAdjust currentColor = new ColorAdjust();
    // color adjust not official. change field to animation component class once sprite sheet is done.

    @Override
    public void onAdded() {
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

    public void setHue(double value){
        var children = entity.getViewComponent().getChildren();
        if(children.isEmpty()) return;

        currentColor.setHue(value);
        children.getFirst().setEffect(value == 0 ? null : currentColor);
    }
}
