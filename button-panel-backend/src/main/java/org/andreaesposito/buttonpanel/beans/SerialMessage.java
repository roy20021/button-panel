package org.andreaesposito.buttonpanel.beans;

import org.springframework.context.ApplicationEvent;

public class SerialMessage extends ApplicationEvent {

    private String message;

    /**
     * Create a new ApplicationEvent.
     *
     * @param message the object on which the event initially occurred (never {@code null})
     */
    public SerialMessage(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SerialMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
