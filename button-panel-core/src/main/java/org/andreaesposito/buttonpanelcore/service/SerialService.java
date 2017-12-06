package org.andreaesposito.buttonpanelcore.service;

import com.fazecast.jSerialComm.SerialPort;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SerialService implements Runnable {

    private static final String PREFERRED_SERIAL_PORT = "Button Panel - Preferred Serial Port";

    @Autowired
    private PublisherService publisherService;

    private Optional<SerialPort> serialPort = Optional.empty();
    private Preferences preferences;

    private Optional<Thread> daemon = Optional.empty();

    @PostConstruct
    private void retrieveAndInitPreferredSerialPort() {
        preferences = Preferences.userRoot();

        Optional<String> preferredSerialPort = Optional.ofNullable(preferences.get(PREFERRED_SERIAL_PORT, null));
        if (preferredSerialPort.isPresent()) {
            serialPort = Optional.of(SerialPort.getCommPort(preferredSerialPort.get()));
            startListen();
        }
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
    }

    @Scheduled(fixedRate = 5000)
    public synchronized void startListen() {
        if (serialPort.isPresent() && !serialPort.get().isOpen()) {
            boolean succeed = serialPort.get().openPort();
            if (succeed) {
                daemon = Optional.of(new Thread(this));
                daemon.get().start();
            }
        }
    }

    public String[] getSerialPorts() {
        return Stream.of(SerialPort.getCommPorts()).map(port -> port.getDescriptivePortName()).toArray(String[]::new);
    }

    public void setSerialPort(String portName) {
        stopListen(); // stop before move to new one

        try {
            Thread.sleep(500); // Wait serial connection to physically be released
        } catch (InterruptedException e) {
            //Nothing to do
        }

        // find serial port
        serialPort = Stream.of(SerialPort.getCommPorts()).filter(port ->
                portName.equals(port.getDescriptivePortName())).findFirst();

        // set preferences with new selected port
        preferences.put(PREFERRED_SERIAL_PORT, serialPort.get().getSystemPortName());

        startListen();
    }

    public Stream<String> getStream() {
        if (serialPort.isPresent()) {
            serialPort.get().setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 500, 0);
            BufferedReader reader = new BufferedReader(new InputStreamReader(serialPort.get().getInputStream()));
            return reader.lines();
        }
        throw new IllegalStateException("No Serial Port Selected");
    }

    @Override
    public void run() {
        try {
            this.getStream().forEach(str -> {
                System.out.println(str);
                publisherService.test(str);
            });
        } catch (RuntimeException e) {
            // Nothing to do
        }
    }
}
