package org.voimala.myrts.screens.menu.windows;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class MainMenuWindow extends Window {

    private Skin skin;
    private Table table;

    public MainMenuWindow(String title, Skin skin) {
        super(title, skin);
        this.skin = skin;

        //initialize();
    }

    public void initialize() {
        buildWidgets();
        finalizeWindow();
    }

    private void buildWidgets() {
        table = new Table();

        table.pad(10, 10, 0, 10);

        int buttonsWidth = 200;
        int buttonsPadding = 10;

        table.row();
        TextButton textButtonSingleplayer = new TextButton("Singleplayer", skin);
        textButtonSingleplayer.setWidth(buttonsWidth);
        textButtonSingleplayer.pad(buttonsPadding);
        textButtonSingleplayer.setDisabled(true);
        table.add(textButtonSingleplayer);

        table.row();
        TextButton textButtonMultiplayer = new TextButton("Multiplayer", skin);
        textButtonMultiplayer.setWidth(buttonsWidth);
        textButtonMultiplayer.pad(buttonsPadding);
        table.add(textButtonMultiplayer);

        table.row();
        TextButton textButtonSettings = new TextButton("Settings", skin);
        textButtonSettings.setWidth(buttonsWidth);
        textButtonSettings.setDisabled(true);
        textButtonSettings.pad(buttonsPadding);
        table.add(textButtonSettings);

        table.row();
        TextButton textButtonCredits = new TextButton("Credits", skin);
        textButtonCredits.setWidth(buttonsWidth);
        textButtonCredits.setDisabled(true);
        textButtonCredits.pad(buttonsPadding);
        table.add(textButtonCredits);

        table.row();
        TextButton textButtonQuit = new TextButton("Quit", skin);
        textButtonQuit.setWidth(buttonsWidth);
        textButtonQuit.pad(buttonsPadding);
        textButtonQuit.setDisabled(true);
        table.add(textButtonQuit);

        this.add(table);
    }

    private void finalizeWindow() {
        // Set size
        setWidth(300);
        setHeight(500);

        // Visual effects
        setColor(1, 1, 1, 0.8f);

        // Hide options window by default
        setVisible(false);

        // Let TableLayout recalculate widget sizes and positions
        pack();

        // Set position
        setPosition(0, 0);
    }
}
