package org.andreaesposito.buttonpanelcore;

import org.andreaesposito.buttonpanelcore.view.ButtonPanelTray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableScheduling
public class ButtonPanelApplication {

    @Autowired
    private ButtonPanelTray buttonPanelTray;

    @PostConstruct
    private void init() {
        buttonPanelTray.show();
    }

    public static void main(String[] args) {
        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(ButtonPanelApplication.class);
        springApplicationBuilder.headless(false);
        springApplicationBuilder.run(args);
    }
}
