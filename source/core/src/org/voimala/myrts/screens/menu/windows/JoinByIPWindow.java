package org.voimala.myrts.screens.menu.windows;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import org.voimala.myrts.screens.menu.MenuScreen;

public class JoinByIPWindow extends AbstractMenuWindow {

    private Skin skin;

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
        table.addActor(labelIp);
        TextField textFieldIp = new TextArea("localhost", skin);
        table.add(textFieldIp).width(280);

        table.row();
        Label labelPort = new Label("Port:", skin);
        table.addActor(labelPort);
        TextField textFieldPort = new TextArea("52828", skin);
        table.add(textFieldPort).width(100);

        table.row();
        TextButton textButtonConnect = new TextButton("Connect", skin);
        textButtonConnect.pad(buttonsPadding);
        textButtonConnect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //onJoinClicked(); // TODO
            }
        });
        table.add(textButtonConnect).size(buttonsWidth, buttonsHeight / 2).pad(buttonRowPadding);

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

    private void onBackClicked() {
        menuScreen.hideWindow(getWindowName());
        menuScreen.showWindow(WindowName.MULTIPLAYER);
    }

    private void setDefaultSizeAndPosition() {
        setWidth(300);
        setHeight(180);
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
