package org.andreaesposito.buttonpanel.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ViewUtil {

    private static final Logger logger = LoggerFactory.getLogger(ViewUtil.class);

    public static Image createButtonPanelImage() {
        return ViewUtil.createImage("trayicon.png", "Button Panel Tray Icon");
    }

    public static Image createButtonPanelImageConnected() {
        return ViewUtil.createImage("trayicon_connected.png", "Button Panel Tray Icon");
    }

    //Obtain the image URL
    public static Image createImage(String path, String description) {
        URL imageURL = ButtonPanelTray.class.getClassLoader().getResource(path);

        if (imageURL == null) {
            logger.warn("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}
