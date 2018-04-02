package org.andreaesposito.buttonpanel.view;

import org.andreaesposito.buttonpanel.beans.ButtonPanelConnectivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class ButtonPanelTray implements ApplicationListener<ButtonPanelConnectivity> {

    private static final Logger logger = LoggerFactory.getLogger(ButtonPanelTray.class);

    @Autowired
    private ApplicationContext applicationContext;

    final SystemTray tray = SystemTray.getSystemTray();

    public void show() {
        /* Use an appropriate Look and Feel */
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);

        //Schedule a job for the event-dispatching thread: adding TrayIcon.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private void createAndShowGUI() {
        //Check the SystemTray support
        if (!SystemTray.isSupported()) {
            logger.error("SystemTray is not supported");
            System.exit(-2);
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon =
                new TrayIcon(ViewUtil.createButtonPanelImage());

        // Create a popup menu components
        MenuItem searchItem = new MenuItem("Search Panel...");
        MenuItem exitItem = new MenuItem("Exit");

        //Add components to popup menu
        popup.add(searchItem);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            logger.error("TrayIcon could not be added.", e);
            System.exit(-2);
        }

        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Button Panel TrayIcon");

        searchItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SearchPanelFrame searchPanelFrame = applicationContext.getBean(SearchPanelFrame.class);
                searchPanelFrame.setVisible(true);
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0);
            }
        });
    }

    @Override
    public void onApplicationEvent(ButtonPanelConnectivity event) {
        tray.getTrayIcons()[0].setImage(event.isConnected() ? ViewUtil.createButtonPanelImageConnected() : ViewUtil.createButtonPanelImage());
    }
}
