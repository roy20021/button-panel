package org.andreaesposito.buttonpanel.service;

import org.andreaesposito.buttonpanel.beans.PanelEvent;
import org.andreaesposito.buttonpanel.beans.SerialMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class SerialTranslator implements ApplicationListener<SerialMessage> {

    private static final String PROTOCOL_READY = "##Ready";
    private static final String PROTOCOL_LOAD = "##Load";
    private static final String PROTOCOL_START = "##Status Start";
    private static final String PROTOCOL_END = "##Status End";
    private static final String PROTOCOL_SWITCHES = "Switches:";
    private static final String PROTOCOL_BUTTONS = "Buttons:";

    @Autowired
    private StompService stompService;

    private char[] previousSwitches;
    boolean fullLoadGoingOn = false;

    private Map<Integer, PanelEvent.SourceType> fromSwitchesIndexToName;
    private Map<Integer, PanelEvent.SourceType> fromButtonIndexToName;

    @PostConstruct
    private void init() {
        fromSwitchesIndexToName = new HashMap<>();
        fromSwitchesIndexToName.put(0, PanelEvent.SourceType.SWITCH_A);
        fromSwitchesIndexToName.put(1, PanelEvent.SourceType.SWITCH_B);
        fromSwitchesIndexToName.put(2, PanelEvent.SourceType.SWITCH_C);
        fromSwitchesIndexToName.put(3, PanelEvent.SourceType.SWITCH_D);
        fromSwitchesIndexToName.put(4, PanelEvent.SourceType.TOGGLE_A);
        fromSwitchesIndexToName.put(5, PanelEvent.SourceType.TOGGLE_B);
        fromSwitchesIndexToName.put(6, PanelEvent.SourceType.TOGGLE_C);

        fromButtonIndexToName = new HashMap<>();
        fromButtonIndexToName.put(0, PanelEvent.SourceType.BUTTON_A);
        fromButtonIndexToName.put(1, PanelEvent.SourceType.BUTTON_B);
        fromButtonIndexToName.put(2, PanelEvent.SourceType.BUTTON_C);
        fromButtonIndexToName.put(3, PanelEvent.SourceType.BUTTON_D);
    }

    public synchronized Optional<Stream<PanelEvent>> translate(SerialMessage serialMessage) {
        final String message = serialMessage.getMessage();

        if (PROTOCOL_LOAD.equals(message)) {
            fullLoadGoingOn = true;
        } else if (PROTOCOL_END.equals(message)) {
            fullLoadGoingOn = false;
        } else if (message.startsWith(PROTOCOL_SWITCHES)) {
            char[] values = message.replaceAll(PROTOCOL_SWITCHES, "").toCharArray();

            if (previousSwitches == null) { // initialise last state first time
                previousSwitches = values;
            }

            Stream.Builder builder = Stream.builder();
            IntStream.range(0, values.length).forEach(i -> {
                if (fullLoadGoingOn || previousSwitches[i] != values[i]) {
                    PanelEvent event = new PanelEvent();
                    event.setSource(fromSwitchesIndexToName.get(i));
                    event.setState(calculateState(i, values[i]));
                    builder.add(event);
                }
            });

            previousSwitches = values;

            return Optional.of(builder.build());
        } else if (message.startsWith(PROTOCOL_BUTTONS)) {
            char[] values = message.replaceAll(PROTOCOL_BUTTONS, "").toCharArray();
            Stream.Builder builder = Stream.builder();
            IntStream.range(0, values.length).forEach(i -> {
                PanelEvent.State state = values[i] == '0' ? PanelEvent.State.UP : PanelEvent.State.DOWN;

                if (state.equals(PanelEvent.State.DOWN)) { // Notify only when button pressed
                    PanelEvent event = new PanelEvent();
                    event.setSource(fromButtonIndexToName.get(i));
                    event.setState(state);
                    builder.add(event);
                }
            });
            return Optional.of(builder.build());
        }

        return Optional.empty();
    }

    private PanelEvent.State calculateState(int index, char value) {
        if (index >= 0 && index < 4) {
            return value == '0' ? PanelEvent.State.LEFT : PanelEvent.State.RIGHT;
        } else {
            return value == '0' ? PanelEvent.State.DOWN : PanelEvent.State.UP;
        }
    }


    @Override
    public void onApplicationEvent(SerialMessage serialMessage) {
        Optional<Stream<PanelEvent>> panelEvent = this.translate(serialMessage);
        if (panelEvent.isPresent()) {
            panelEvent.get().forEach(stompService::publish);
        }
    }
}
