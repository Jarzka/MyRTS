package org.voimala.myrts.screens.menu.windows;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.voimala.myrts.screens.menu.MenuScreen;

public class MainMenuWindow extends AbstractMenuWindow {

    private Skin skin;
    private Table table;

    public MainMenuWindow(String title, Skin skin, MenuScreen menuScreen) {
        super(title, skin, menuScreen);
        this.skin = skin;

        initialize();
    }

    public void initialize() {
        buildWidgets();
        finalizeWindow();
    }

    private void buildWidgets() {
        table = new Table();

        table.pad(10);

        int buttonsWidth = 150;
        int buttonsHeight = 50;
        int buttonsPadding = 10;

        table.row();
        TextButton textButtonSingleplayer = new TextButton("Singleplayer", skin);
        textButtonSingleplayer.pad(buttonsPadding);
        textButtonSingleplayer.setDisabled(true);
        table.add(textButtonSingleplayer).size(buttonsWidth, buttonsHeight);

        table.row();
        TextButton textButtonMultiplayer = new TextButton("Multiplayer", skin);
        textButtonMultiplayer.pad(buttonsPadding);
        textButtonMultiplayer.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onMultiplayerButtonClicked();
            }
        });
        table.add(textButtonMultiplayer).size(buttonsWidth, buttonsHeight);

        table.row();
        TextButton textButtonSettings = new TextButton("Settings", skin);
        textButtonSettings.setDisabled(true);
        textButtonSettings.pad(buttonsPadding);
        table.add(textButtonSettings).size(buttonsWidth, buttonsHeight);

        table.row();
        TextButton textButtonCredits = new TextButton("Credits", skin);
        textButtonCredits.setDisabled(true);
        textButtonCredits.pad(buttonsPadding);
        table.add(textButtonCredits).size(buttonsWidth, buttonsHeight);

        table.row();
        TextButton textButtonQuit = new TextButton("Quit", skin);
        textButtonQuit.pad(buttonsPadding);
        textButtonQuit.setDisabled(true);
        table.add(textButtonQuit).size(buttonsWidth, buttonsHeight);

        this.add(table);
    }

    private void finalizeWindow() {
        // Set size
        setWidth(200);
        setHeight(300);

        setColor(1, 1, 1, 0.8f);

        //pack();
        setPosition(Gdx.graphics.getWidth() / 2 - getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - getHeight() / 2);
        setVisible(false);
    }

    public void onMultiplayerButtonClicked() {
        menuScreen.showWindow(WindowName.MULTIPLAYER);
        menuScreen.hideWindow(WindowName.MAIN_MENU);
    }

    @Override
    public WindowName getWindowName() {
        return WindowName.MAIN_MENU;
    }
}
