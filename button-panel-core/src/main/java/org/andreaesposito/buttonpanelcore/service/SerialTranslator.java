package org.andreaesposito.buttonpanelcore.service;

import org.andreaesposito.buttonpanelcore.beans.PanelEvent;
import org.andreaesposito.buttonpanelcore.beans.PanelState;
import org.andreaesposito.buttonpanelcore.beans.SerialMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class SerialTranslator implements ApplicationListener<SerialMessage> {

    @Autowired
    private PublisherService publisherService;

    private final PanelState panelState = new PanelState();

    public synchronized Stream<PanelEvent> translate(SerialMessage serialMessage) {

        //TODO: translate string to semantic

        PanelEvent dummyEvent = new PanelEvent();
        dummyEvent.setSource(PanelEvent.SourceType.BUTTON);
        dummyEvent.setState(PanelState.State.LEFT_OR_UP);

        return Stream.of(dummyEvent);
    }

    @Override
    public void onApplicationEvent(SerialMessage serialMessage) {
        this.translate(serialMessage).forEach(publisherService::publish);
    }
}
