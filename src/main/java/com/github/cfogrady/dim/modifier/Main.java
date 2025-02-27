package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.data.firmware.FirmwareManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;

@Slf4j
public class Main extends Application {
    private ApplicationOrchestrator applicationOrchestrator;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            applicationOrchestrator = ApplicationOrchestrator.buildOrchestration(primaryStage);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("icon.png")));
        loadFirstScene();
    }

    @Override
    public void stop() {
        applicationOrchestrator.getTimer().cancel();
        applicationOrchestrator.getTimer().purge();
    }

    private void loadFirstScene() {
        FirmwareManager firmwareManager = applicationOrchestrator.getFirmwareManager();
        if(firmwareManager.isValidFirmwareLocationSet()) {
            try {
                applicationOrchestrator.getAppState().setFirmwareData(firmwareManager.loadFirmware());
                applicationOrchestrator.getFirstLoadScene().setupScene();
            } catch (IOException ioe) {
                log.error("Unable to load firmware. Please select firmware location.", ioe);
                applicationOrchestrator.getFirmwareLoadScene().setupScene();
            }
        } else {
            //firmware load scene
            applicationOrchestrator.getFirmwareLoadScene().setupScene();
        }
    }
}
