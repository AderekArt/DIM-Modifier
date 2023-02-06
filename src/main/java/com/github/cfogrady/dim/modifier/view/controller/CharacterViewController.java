package com.github.cfogrady.dim.modifier.view.controller;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.SpriteReplacer;
import com.github.cfogrady.dim.modifier.controls.ImageIntComboBox;
import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.card.Character;
import com.github.cfogrady.dim.modifier.view.BemCharactersView;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;

@Slf4j
@RequiredArgsConstructor
public class CharacterViewController implements Initializable {
    private final Timer timer;
    private final AppState appState;
    private final SpriteImageTranslator spriteImageTranslator;
    private final SpriteReplacer spriteReplacer;
    private final Node statsSubView;
    private final StatsViewController statsViewController;
    private final TransformationViewController transformationViewController;
    private final Node transformationsSubView;

    @FXML
    private ImageIntComboBox characterSelectionComboBox;
    @FXML
    private StackPane nameBox;
    @FXML
    private Button statsButton;
    @FXML
    private Button transformationsButton;
    @FXML
    private AnchorPane subView;

    private enum SubViewSelection {
        STATS,
        TRANSFORMATIONS;
    }

    private int characterSelection = 0;
    private BemCharactersView.NameUpdater nameUpdater;
    private SubViewSelection subViewSelection = SubViewSelection.STATS;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void refreshAll() {
        initializeCharacterSelectionComboBox();
        initializeNameBox();
        refreshButtons();
        initializeSubView();
    }

    private void initializeCharacterSelectionComboBox() {
        characterSelectionComboBox.initialize(characterSelection, spriteImageTranslator.createImageValuePairs(appState.getIdleForCharacters()), 1.0, null, null);
        characterSelectionComboBox.setOnAction(e -> {
            if(characterSelectionComboBox.getValue() != null) {
                characterSelection = characterSelectionComboBox.getValue().getValue();
            }
            if(nameUpdater != null) {
                nameUpdater.cancel();
            }
            initializeNameBox();
            initializeSubView();
        });
    }

    private void initializeNameBox() {
        Character<?> character = appState.getCardData().getCharacters().get(characterSelection);
        SpriteData.Sprite nameSprite = character.getSprites().get(0);
        Image image = spriteImageTranslator.loadImageFromSprite(nameSprite);
        ImageView imageView = new ImageView(image);
        imageView.setViewport(new Rectangle2D(0, 0, 80, 15));
        if(nameSprite.getWidth() > 80) {
            nameUpdater = new BemCharactersView.NameUpdater(nameSprite.getWidth(), imageView, -80);
            timer.scheduleAtFixedRate(nameUpdater, 0, 33);
        }
        //nameBox.setBackground();
        nameBox.getChildren().clear();
        nameBox.getChildren().add(imageView);
        //stackPane.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        nameBox.setOnMouseClicked(event -> {
            SpriteData.Sprite newNameSprite = spriteReplacer.replaceSprite(nameSprite, false, true);
            if(newNameSprite != null) {
                character.getSprites().set(0, newNameSprite);
            }
        });
    }

    private void refreshButtons() {
        if(subViewSelection == SubViewSelection.STATS) {
            statsButton.setDisable(true);
            transformationsButton.setDisable(false);
        } else {
            statsButton.setDisable(false);
            transformationsButton.setDisable(true);
        }
        statsButton.setOnAction(e -> {
            subViewSelection = SubViewSelection.STATS;
            initializeSubView();
            refreshButtons();
        });
        transformationsButton.setOnAction(e -> {
            subViewSelection = SubViewSelection.TRANSFORMATIONS;
            initializeSubView();
            refreshButtons();
        });
    }

    private void initializeSubView() {
        subView.getChildren().clear();
        switch(subViewSelection) {
            case STATS -> {
                statsViewController.setCharacter(appState.getCharacter(characterSelection));
                statsViewController.setRefreshIdleSprite(this::initializeCharacterSelectionComboBox);
                statsViewController.refreshAll();
                subView.getChildren().add(statsSubView);
            }
            case TRANSFORMATIONS -> {
                transformationViewController.setCharacter(appState.getCharacter(characterSelection));
                transformationViewController.refreshAll();
                subView.getChildren().add(transformationsSubView);
            }
        }
    }
}
