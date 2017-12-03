package org.andreaesposito.buttonpanelcore.service;

import org.andreaesposito.buttonpanelcore.beans.PanelEvent;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class PublisherService {

    @MessageMapping("/hello")
    @SendTo("/topic/panelEvents")
    public PanelEvent greeting() {
        PanelEvent event = new PanelEvent();
        event.setButton("RED");
        event.setMode(1);
        return event;
    }
}
