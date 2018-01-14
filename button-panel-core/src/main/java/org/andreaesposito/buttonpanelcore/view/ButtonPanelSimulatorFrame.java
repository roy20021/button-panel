package org.andreaesposito.buttonpanelcore.view;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

public class ButtonPanelSimulatorFrame extends JFrame {

    public ButtonPanelSimulatorFrame(){
        super("Button Panel Simulator");
    }

    @PostConstruct
    private void init() {
        this.setLayout(new GridBagLayout());

        //TODO

        this.pack();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

        this.setIconImage(ViewUtil.createButtonPanelImage());

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
