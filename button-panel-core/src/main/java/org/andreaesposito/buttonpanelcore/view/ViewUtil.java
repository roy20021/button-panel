package org.andreaesposito.buttonpanelcore.view;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ViewUtil {

    public static Image createButtonPanelImage() {
        return ViewUtil.createImage("trayicon.png", "Button Panel Tray Icon");
    }

    //Obtain the image URL
    public static Image createImage(String path, String description) {
        URL imageURL = ButtonPanelTray.class.getClassLoader().getResource(path);

        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}
