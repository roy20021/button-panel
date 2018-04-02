package org.andreaesposito.buttonpanel.beans;

import org.springframework.context.ApplicationEvent;

public class ButtonPanelConnectivity extends ApplicationEvent {

    private boolean connected;

    public ButtonPanelConnectivity() {
        super(new Object());
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
