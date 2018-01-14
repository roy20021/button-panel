package org.andreaesposito.buttonpanelcore.beans;

import org.andreaesposito.buttonpanelcore.beans.PanelState.State;

public class PanelEvent {

    public enum SourceType {
        BUTTON, SWITCH
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
