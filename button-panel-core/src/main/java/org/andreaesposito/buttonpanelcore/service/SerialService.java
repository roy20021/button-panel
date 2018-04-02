package org.andreaesposito.buttonpanelcore.service;

import com.fazecast.jSerialComm.SerialPort;
import org.andreaesposito.buttonpanelcore.beans.ButtonPanelConnectivity;
import org.andreaesposito.buttonpanelcore.beans.SerialMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

@Service
public class SerialService implements Runnable, ApplicationEventPublisherAware {

    private static final int KEEP_ALIVE_TIMEOUT = 5 * 60 * 1000; // 5 minutes

    private static final String FULL_LOAD_MESSAGE = "S";

    private static final String PREFERRED_SERIAL_PORT = "ButtonPanel:PreferredSerialPort";

    private static final Logger logger = LoggerFactory.getLogger(SerialService.class);

    private ApplicationEventPublisher applicationEventPublisher;

    private Optional<SerialPort> serialPort = Optional.empty();
    private Preferences preferences;

    private Optional<Thread> daemon = Optional.empty();

    private ButtonPanelConnectivity connectedEvent;
    private ButtonPanelConnectivity disconnectedEvent;

    @PostConstruct
    private void retrieveAndInitPreferredSerialPort() {
        preferences = Preferences.userRoot();

        connectedEvent = new ButtonPanelConnectivity();
        connectedEvent.setConnected(true);

        disconnectedEvent = new ButtonPanelConnectivity();
        disconnectedEvent.setConnected(false);

        startListen();
    }

    @PreDestroy
    private synchronized void stopListen() {
        if (daemon.isPresent()) {
            daemon.get().interrupt();
            daemon = Optional.empty();
        }
        if (serialPort.isPresent()) {
            serialPort.get().closePort();
        }

        applicationEventPublisher.publishEvent(disconnectedEvent);
    }

    @Scheduled(fixedRate = 5000)
    private synchronized void startListen() {

        if (!serialPort.isPresent()) { // Check if now it is connected
            Optional<String> preferredSerialPort = Optional.ofNullable(preferences.get(PREFERRED_SERIAL_PORT, null));
            if (preferredSerialPort.isPresent()) {
                serialPort = Optional.of(SerialPort.getCommPort(preferredSerialPort.get()));
            }
        }
        if (serialPort.isPresent() && !serialPort.get().isOpen()) { // check if connection has to be opened
            boolean succeed = serialPort.get().openPort();

            if (succeed) {
                daemon = Optional.of(new Thread(this));
                daemon.get().start();

                applicationEventPublisher.publishEvent(connectedEvent);
            }
        }
    }

    public String[] getSerialPorts() {
        return Stream.of(SerialPort.getCommPorts()).map(port -> port.getDescriptivePortName()).toArray(String[]::new);
    }

    public void setSerialPort(String portName) {
        stopListen(); // stop before move to new one

        try {
            Thread.sleep(1000); // Wait serial connection to physically be released
        } catch (InterruptedException e) {
            //Nothing to do
        }

        // find serial port
        serialPort = Stream.of(SerialPort.getCommPorts()).filter(port ->
                portName.equals(port.getDescriptivePortName())).findFirst();

        if (!serialPort.isPresent()) {
            logger.warn(portName + " not found");
            return;
        }

        // set preferences with new selected port
        preferences.put(PREFERRED_SERIAL_PORT, serialPort.get().getSystemPortName());

        startListen();
    }

    public Stream<String> getStream() {
        if (serialPort.isPresent()) {
            serialPort.get().setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, KEEP_ALIVE_TIMEOUT, 0);
            BufferedReader reader = new BufferedReader(new InputStreamReader(serialPort.get().getInputStream()));
            return reader.lines();
        }
        throw new IllegalStateException("No Serial Port Selected");
    }

    public void askFullLoad() {
        if (serialPort.isPresent()) {
            byte[] requestMessage = FULL_LOAD_MESSAGE.getBytes();
            serialPort.get().writeBytes(requestMessage, requestMessage.length);
        }
    }

    public void turnLedON(int led) {
        if (serialPort.isPresent()) {
            byte[] requestMessage = String.valueOf(led).getBytes();
            serialPort.get().writeBytes(requestMessage, requestMessage.length);
        }
    }

    @Override
    public void run() {
        try {
            this.getStream().forEach(msg -> {
                SerialMessage serialMessage = new SerialMessage(msg);
                logger.info("Received from serial: " + serialMessage);
                applicationEventPublisher.publishEvent(serialMessage);
            });
        } catch (RuntimeException e) {
            // Nothing to do
            e.printStackTrace();
            applicationEventPublisher.publishEvent(disconnectedEvent);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
