package org.voimala.myrts.screens.menu.windows;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import org.voimala.myrts.networking.NetworkManager;
import org.voimala.myrts.screens.menu.MenuScreen;

public class JoinByIPWindow extends AbstractMenuWindow {

    private Skin skin;

    private TextField textFieldIp;
    private TextField textFieldPort;

    public JoinByIPWindow(Skin skin, MenuScreen menuScreen) {
        super("Join by IP", skin, menuScreen);
        this.skin = skin;

        initialize();
    }

    public void initialize() {
        buildWidgets();
        finalizeWindow();
    }

    private void buildWidgets() {
        Table table = new Table();

        table.pad(10);

        int buttonsWidth = 150;
        int buttonsHeight = 50;
        int buttonsPadding = 10;
        int buttonRowPadding = 2;

        table.row();
        Label labelIp = new Label("IP:", skin);
        table.add(labelIp).width(50).right();
        textFieldIp = new TextArea("localhost", skin);
        table.add(textFieldIp).width(190).colspan(2).left();

        table.row();
        Label labelPort = new Label("Port:", skin);
        table.add(labelPort).width(50).right();
        textFieldPort = new TextArea(String.valueOf(NetworkManager.getInstance().DEFAULT_PORT), skin);
        table.add(textFieldPort).width(100).colspan(2).left();

        table.row().padTop(buttonRowPadding * 5);
        TextButton textButtonBack = new TextButton("< Back", skin);
        textButtonBack.pad(buttonsPadding);
        textButtonBack.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onBackClicked();
            }
        });
        table.add(textButtonBack).size(buttonsWidth / 2, buttonsHeight / 2).left();

        TextButton textButtonConnect = new TextButton("Connect", skin);
        textButtonConnect.pad(buttonsPadding);
        textButtonConnect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onJoinClicked();
            }
        });
        table.add(textButtonConnect).size(buttonsWidth, buttonsHeight / 2).right();

        this.add(table);
        table.debug();
    }

    private void onJoinClicked() {
        NetworkManager.getInstance().setJoinIp(textFieldIp.getText());
        NetworkManager.getInstance().setJoinPort(Integer.valueOf(textFieldPort.getText()));
        NetworkManager.getInstance().joinGame();
    }

    private void finalizeWindow() {
        setDefaultSizeAndPosition();
        setDefaultStyle();
        setVisible(false);
    }

    private void onBackClicked() {
        menuScreen.hideWindow(getWindowName());
        menuScreen.showWindow(WindowName.MULTIPLAYER);
    }

    private void setDefaultSizeAndPosition() {
        setWidth(300);
        setHeight(150);
        setPosition(Gdx.graphics.getWidth() / 2 - getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - getHeight() / 2);
    }

    private void setDefaultStyle() {
        setColor(1, 1, 1, 0.8f);
    }

    @Override
    public WindowName getWindowName() {
        return WindowName.JOIN_BY_IP;
    }
}
