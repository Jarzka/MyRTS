package org.voimala.myrts.screens.menu.windows;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import org.voimala.myrts.networking.NetworkManager;
import org.voimala.myrts.screens.menu.MenuScreen;

public class MultiplayerWindow extends AbstractMenuWindow {

    private Skin skin;

    public MultiplayerWindow(Skin skin, MenuScreen menuScreen) {
        super("Multiplayer", skin, menuScreen);
        this.skin = skin;

        initialize();
    }

    public void initialize() {
        buildWidgets();
        finalizeWindow();
    }

    private void buildWidgets() {
        Table table = new Table();

        table.pad(10, 10, 0, 10);

        int buttonsWidth = 150;
        int buttonsHeight = 50;
        int buttonsPadding = 10;
        int buttonRowPadding = 2;

        table.row();
        TextButton textButtonBrowseServers = new TextButton("Browse servers", skin);
        textButtonBrowseServers.pad(buttonsPadding);
        table.add(textButtonBrowseServers).size(buttonsWidth, buttonsHeight).pad(buttonRowPadding);

        table.row();
        TextButton textButtonJoinByIP = new TextButton("Join by IP", skin);
        textButtonJoinByIP.pad(buttonsPadding);
        textButtonJoinByIP.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onJoinByIpClicked();
            }
        });
        table.add(textButtonJoinByIP).size(buttonsWidth, buttonsHeight).pad(buttonRowPadding);

        table.row();
        TextButton textButtonCreateServer = new TextButton("Create server", skin);
        textButtonCreateServer.pad(buttonsPadding);
        textButtonCreateServer.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onCreateServerClicked();
            }
        });
        table.add(textButtonCreateServer).size(buttonsWidth, buttonsHeight).pad(buttonRowPadding);

        table.row();
        TextButton textButtonBack = new TextButton("< Back", skin);
        textButtonBack.pad(buttonsPadding);
        textButtonBack.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onBackClicked();
            }
        });
        table.add(textButtonBack).size(buttonsWidth / 2, buttonsHeight / 2).padTop(buttonRowPadding * 3);

        this.add(table);
    }

    private void finalizeWindow() {
        setDefaultSizeAndPosition();
        setDefaultStyle();
        setVisible(false);
    }

    private void setDefaultSizeAndPosition() {
        setWidth(200);
        setHeight(250);
        setPosition(Gdx.graphics.getWidth() / 2 - getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - getHeight() / 2);
    }

    private void setDefaultStyle() {
        setColor(1, 1, 1, 0.8f);
    }

    private void onJoinByIpClicked() {
        menuScreen.hideWindow(getWindowName());
        menuScreen.showWindow(WindowName.JOIN_BY_IP);
    }

    private void onCreateServerClicked() {
        /* TODO Normally we would show the "Create server" window, but it will be skipped
        while the menu is still in its early development phase */
        createServer();
        connectToLocalHost();
    }

    private void createServer() {
        NetworkManager.getInstance().hostGame(NetworkManager.getInstance().DEFAULT_PORT);
    }

    private void connectToLocalHost() {
        NetworkManager.getInstance().setJoinIp("localhost");
        NetworkManager.getInstance().setJoinPort(NetworkManager.getInstance().DEFAULT_PORT);
        NetworkManager.getInstance().joinGame();
    }

    private void onBackClicked() {
        menuScreen.hideWindow(getWindowName());
        menuScreen.showWindow(WindowName.MAIN_MENU);
    }

    @Override
    public WindowName getWindowName() {
        return WindowName.MULTIPLAYER;
    }
}
