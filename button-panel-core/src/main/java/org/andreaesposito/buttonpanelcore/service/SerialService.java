package org.andreaesposito.buttonpanelcore.service;

import com.fazecast.jSerialComm.SerialPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

@Service
public class SerialService implements Runnable {

    private static final String PREFERRED_SERIAL_PORT = "Button Panel - Preferred Serial Port";

    @Autowired
    PublisherService publisherService;

    private Optional<SerialPort> selectedPort;
    private Preferences preferences;

    private Optional<Thread> listener;

    @PostConstruct
    private void retrieveAndInitPreferredSerialPort() {
        preferences = Preferences.userRoot();

        Optional<String> preferredSerialPort = Optional.ofNullable(preferences.get(PREFERRED_SERIAL_PORT, null));
        if (preferredSerialPort.isPresent()) {
            selectedPort = Optional.of(SerialPort.getCommPort(preferredSerialPort.get()));
            startListen();
        }
    }

    @PreDestroy
    private synchronized void stopListen() {
        if (listener.isPresent()) {
            listener.get().interrupt();
        }
        if (selectedPort.isPresent()) {
            selectedPort.get().closePort();
        }
    }

    private synchronized void startListen() {
        if (selectedPort.isPresent() && !selectedPort.get().isOpen()) {
            boolean succeed = selectedPort.get().openPort();
            if (succeed) {
                listener = Optional.of(new Thread(this));
                listener.get().start();
            }
        }
    }

    public String[] getSerialPorts() {
        return Stream.of(SerialPort.getCommPorts()).map(port -> port.getDescriptivePortName()).toArray(String[]::new);
    }

    public void setSerialPort(String portName) {
        stopListen(); // stop before move to new one

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        selectedPort = Stream.of(SerialPort.getCommPorts()).filter(port ->
                portName.equals(port.getDescriptivePortName())).findFirst();
        preferences.put(PREFERRED_SERIAL_PORT, selectedPort.get().getSystemPortName());
        startListen();
    }

    public Stream<String> getStream() {
        if (selectedPort.isPresent()) {
            selectedPort.get().setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
            BufferedReader reader = new BufferedReader(new InputStreamReader(selectedPort.get().getInputStream()));
            return reader.lines();
        }
        throw new IllegalStateException("No Serial Port Selected");
    }

    @Override
    public void run() {
        try {
            this.getStream().forEach(str -> {
                System.out.println(str);
                publisherService.greeting();
            });
        } catch (RuntimeException e) {
            // Nothing to do
        }
    }
}
