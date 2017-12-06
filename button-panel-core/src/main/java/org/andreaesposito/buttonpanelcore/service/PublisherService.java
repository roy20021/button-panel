package org.andreaesposito.buttonpanelcore.service;

import org.andreaesposito.buttonpanelcore.beans.PanelEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import static org.andreaesposito.buttonpanelcore.WebSocketConfig.TOPIC;

@Service
public class PublisherService {

    public static final String TOPIC_NAME = "/panelEvents";

    @Autowired
    private SimpMessagingTemplate template;

    public void test(String message) {
        PanelEvent event = new PanelEvent();
        event.setButton("RED " + message);
        event.setMode(1);

        template.convertAndSend(TOPIC + TOPIC_NAME, event);
    }
}
