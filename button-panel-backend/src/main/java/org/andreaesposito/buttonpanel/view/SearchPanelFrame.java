package org.andreaesposito.buttonpanel.view;

import org.andreaesposito.buttonpanel.service.SerialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Component
@Scope("prototype")
public class SearchPanelFrame extends JFrame {

    @Autowired
    private SerialService serialService;

    public SearchPanelFrame() {
        super("Search Panel...");
    }

    @PostConstruct
    private void init() {

        String[] serialPorts = serialService.getSerialPorts();

        if (serialPorts.length == 0) {
            JOptionPane.showMessageDialog(null,
                    "No Serial ports found! Is the button panel connected?",
                    "No Serial ports",
                    JOptionPane.ERROR_MESSAGE);
            this.addWindowListener(new WindowAdapter() { // Dirty hack here for keeping logic in tray
                @Override
                public void windowOpened(WindowEvent e) {
                    SearchPanelFrame.this.dispose();
                }
            });
            return;
        }

        this.setLayout(new FlowLayout());

        JLabel titleLabel = new JLabel("Select the serial port:");
        this.add(titleLabel);

        JComboBox<String> serialPortsComboBox = new JComboBox<>(serialPorts);
        this.add(serialPortsComboBox);

        JButton selectBtn = new JButton("Select");
        selectBtn.addActionListener(e -> {
                    serialService.setSerialPort(serialPorts[serialPortsComboBox.getSelectedIndex()]);
                    this.dispose();
                }
        );
        this.add(selectBtn);

        this.pack();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

        this.setIconImage(ViewUtil.createButtonPanelImage());

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
