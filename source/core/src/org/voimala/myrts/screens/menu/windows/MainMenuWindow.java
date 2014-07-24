package org.voimala.myrts.screens.menu.windows;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.screens.ScreenName;
import org.voimala.myrts.screens.menu.MenuScreen;

public class MainMenuWindow extends AbstractMenuWindow {

    private Skin skin;

    public MainMenuWindow(Skin skin, MenuScreen menuScreen) {
        super("Main Menu", skin, menuScreen);
        this.skin = skin;

        initialize();
    }

    public void initialize() {
        buildWidgets();
        finalizeWindow();
    }

    private void buildWidgets() {
        int buttonsWidth = 150; // TODO Move to a single class
        int buttonsHeight = 50;
        int buttonsPadding = 10;
        int buttonRowPadding = 2;

        Table table = new Table();

        table.pad(10);

        table.row();
        TextButton textButtonSingleplayer = new TextButton("Singleplayer", skin);
        textButtonSingleplayer.pad(buttonsPadding);
        textButtonSingleplayer.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onSingleplayerButtonClicked();
            }
        });
        table.add(textButtonSingleplayer).size(buttonsWidth, buttonsHeight).pad(buttonRowPadding);

        table.row();
        TextButton textButtonMultiplayer = new TextButton("Multiplayer", skin);
        textButtonMultiplayer.pad(buttonsPadding);
        textButtonMultiplayer.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onMultiplayerButtonClicked();
            }
        });
        table.add(textButtonMultiplayer).size(buttonsWidth, buttonsHeight).pad(buttonRowPadding);

        table.row();
        TextButton textButtonSettings = new TextButton("Settings", skin);
        textButtonSettings.pad(buttonsPadding);
        table.add(textButtonSettings).size(buttonsWidth, buttonsHeight).pad(buttonRowPadding);

        table.row();
        TextButton textButtonCredits = new TextButton("Credits", skin);
        textButtonCredits.pad(buttonsPadding);
        table.add(textButtonCredits).size(buttonsWidth, buttonsHeight).pad(buttonRowPadding);

        table.row();
        TextButton textButtonQuit = new TextButton("Quit", skin);
        textButtonQuit.pad(buttonsPadding);
        textButtonQuit.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onQuitClicked();
            }
        });
        table.add(textButtonQuit).size(buttonsWidth, buttonsHeight).pad(buttonRowPadding);

        this.add(table);
    }

    private void finalizeWindow() {
        setDefaultSizeAndPosition();
        setDefaultStyle();
        setVisible(false);
    }

    private void setDefaultSizeAndPosition() {
        setWidth(200);
        setHeight(320);
        setPosition(Gdx.graphics.getWidth() / 2 - getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - getHeight() / 2);
    }

    private void setDefaultStyle() {
        setColor(1, 1, 1, 0.8f);
    }

    public void onSingleplayerButtonClicked() {
        /* TODO Normally we would show the "Singleplayer window", but it will be skipped
         * as long as the main menu is still in development. */
        GameMain.getInstance().setNextScreen(ScreenName.LOAD_GAMEPLAY);
    }

    public void onMultiplayerButtonClicked() {
        menuScreen.hideWindow(getWindowName());
        menuScreen.showWindow(WindowName.MULTIPLAYER);
    }

    public void onQuitClicked() {
        System.exit(1);
    }

    @Override
    public WindowName getWindowName() {
        return WindowName.MAIN_MENU;
    }
}
