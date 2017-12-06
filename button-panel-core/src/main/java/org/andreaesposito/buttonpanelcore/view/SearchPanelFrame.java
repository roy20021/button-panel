package org.andreaesposito.buttonpanelcore.view;

import org.andreaesposito.buttonpanelcore.service.SerialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

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
        this.setLayout(new FlowLayout());

        JLabel titleLablel = new JLabel("Select the serial port:");
        this.add(titleLablel);

        String[] serialPorts = serialService.getSerialPorts();
        JComboBox<String> serialPortsComboBox = new JComboBox<>(serialPorts);
        this.add(serialPortsComboBox);

        JButton selectBtn = new JButton("Select");
        selectBtn.addActionListener(e ->
                serialService.setSerialPort(serialPorts[serialPortsComboBox.getSelectedIndex()])
        );
        this.add(selectBtn);

        this.pack();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

        this.setIconImage(ViewUtil.createButtonPanelImage());

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
