package org.andreaesposito.buttonpanelcore.service;

import org.andreaesposito.buttonpanelcore.beans.PanelEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import static org.andreaesposito.buttonpanelcore.WebSocketConfig.TOPIC;

@Service
public class PublisherService {

    public static final String TOPIC_NAME = "/panelEvents";

    private static final Logger logger = LoggerFactory.getLogger(PublisherService.class);

    @Autowired
    private SimpMessagingTemplate template;

    public void publish(PanelEvent panelEvent) {
        logger.info("Going to publish: " + panelEvent);
        template.convertAndSend(TOPIC + TOPIC_NAME, panelEvent);
    }

}
