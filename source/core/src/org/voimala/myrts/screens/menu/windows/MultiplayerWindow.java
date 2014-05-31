package org.voimala.myrts.screens.menu.windows;


import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import org.voimala.myrts.screens.menu.MenuScreen;

public class MultiplayerWindow extends AbstractMenuWindow {

    private Skin skin;
    private Table table;

    public MultiplayerWindow(String title, Skin skin, MenuScreen menuScreen) {
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

        table.pad(10, 10, 0, 10);

        int buttonsWidth = 200;
        int buttonsPadding = 10;

        table.row();
        TextButton textButtonCreateServer = new TextButton("Create server", skin);
        textButtonCreateServer.setWidth(buttonsWidth);
        textButtonCreateServer.pad(buttonsPadding);
        table.add(textButtonCreateServer);

        table.row();
        TextButton textButtonBrowseServers = new TextButton("Browse servers", skin);
        textButtonBrowseServers.setWidth(buttonsWidth);
        textButtonBrowseServers.pad(buttonsPadding);
        textButtonBrowseServers.setDisabled(true);
        table.add(textButtonBrowseServers);

        table.row();
        TextButton textButtonJoinByIP = new TextButton("Join by IP", skin);
        textButtonJoinByIP.setWidth(buttonsWidth);
        textButtonJoinByIP.pad(buttonsPadding);
        table.add(textButtonJoinByIP);

        this.add(table);
    }

    private void finalizeWindow() {
        setColor(1, 1, 1, 0.8f);

        pack();
        setPosition(0, 0);
        setVisible(false);
    }

    @Override
    public WindowName getWindowName() {
        return WindowName.MULTIPLAYER;
    }
}
