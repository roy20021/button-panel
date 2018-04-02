package org.andreaesposito.buttonpanel.beans;

public class PanelEvent {

    public enum SourceType {
        BUTTON_A, BUTTON_B, BUTTON_C, BUTTON_D, SWITCH_A, SWITCH_B, SWITCH_C, SWITCH_D, TOGGLE_A, TOGGLE_B, TOGGLE_C
    }

    public enum State {
        LEFT, RIGHT, UP, DOWN
    }

    private SourceType source;
    private State state;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public SourceType getSource() {
        return source;
    }

    public void setSource(SourceType source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "PanelEvent{" +
                "source=" + source +
                ", state=" + state +
                '}';
    }
}
