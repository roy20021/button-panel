package org.andreaesposito.buttonpanelcore.beans;

public class PanelState {

    public enum State {
        LEFT_OR_UP, RIGHT_OR_DOWN
    }

    private State switchA;
    private State switchB;
    private State switchC;

    public State getSwitchA() {
        return switchA;
    }

    public void setSwitchA(State switchA) {
        this.switchA = switchA;
    }

    public State getSwitchB() {
        return switchB;
    }

    public void setSwitchB(State switchB) {
        this.switchB = switchB;
    }

    public State getSwitchC() {
        return switchC;
    }

    public void setSwitchC(State switchC) {
        this.switchC = switchC;
    }
}
