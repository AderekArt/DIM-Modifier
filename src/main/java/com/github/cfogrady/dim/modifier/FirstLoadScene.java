package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.vb.dim.reader.DimReader;
import com.github.cfogrady.vb.dim.reader.content.DimContent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@RequiredArgsConstructor
@Slf4j
public class FirstLoadScene implements com.github.cfogrady.dim.modifier.Scene {
    private final Stage stage;

    @Override
    public void setupScene() {
        DimReader reader = new DimReader();
        Button button = new Button();
        button.setText("Open DIM File");
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select DIM File");
            File file = fileChooser.showOpenDialog(stage);
            if(file != null) {
                InputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(file);
                    DimContent content = reader.readDimData(fileInputStream, false);
                    LoadedScene scene = new LoadedScene(content, stage);
                    scene.setupScene(SelectionState.builder().backgroundType(BackgroundType.IMAGE).selectionType(CurrentSelectionType.LOGO).build());
                } catch (FileNotFoundException e) {
                    log.error("Couldn't find selected file.", e);
                }

            }
        });
        Scene scene = new Scene(new StackPane(button), 640, 480);

        stage.setScene(scene);
        stage.show();
    }
}