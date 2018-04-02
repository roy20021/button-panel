package org.andreaesposito.buttonpanel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.andreaesposito.buttonpanel.beans.PanelEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

import static org.andreaesposito.buttonpanel.WebSocketConfig.TOPIC;

@Controller
public class StompService {

    public static final String TOPIC_NAME = "/panelEvents";

    private static final Logger logger = LoggerFactory.getLogger(StompService.class);

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private SerialService serialService;

    private ObjectWriter ow;

    @PostConstruct
    private void init() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    public void publish(PanelEvent panelEvent) {
        logger.info("Going to publish: " + panelEvent);
        try {
            String json = ow.writeValueAsString(panelEvent);
            template.convertAndSend(TOPIC + TOPIC_NAME, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @MessageMapping("/fullLoad")
    public void requestFullLoad() {
        serialService.askFullLoad();
    }

    @MessageMapping("/acknowledge")
    public void acknowledge(@Payload String message) {
        serialService.turnLedON(Integer.parseInt(message));
    }
}
