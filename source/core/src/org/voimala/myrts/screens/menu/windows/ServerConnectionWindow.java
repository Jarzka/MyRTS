package org.voimala.myrts.screens.menu.windows;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import org.voimala.myrts.networking.NetworkManager;
import org.voimala.myrts.screens.menu.MenuScreen;

public class ServerConnectionWindow extends AbstractMenuWindow {

    private Skin skin;
    private Label labelMessage;
    private String message = "Connecting to the server..."; // Default message

    public ServerConnectionWindow(Skin skin, MenuScreen menuScreen) {
        super("Server connection", skin, menuScreen);
        this.skin = skin;

        initialize();
    }

    public void initialize() {
        buildWidgets();
        finalizeWindow();
    }

    private void buildWidgets() {
        Table table = new Table();

        table.pad(20, 10, 10, 10);

        int buttonsWidth = 150;
        int buttonsHeight = 50;
        int buttonsPadding = 10;
        int buttonRowPadding = 2;

        table.row();
        labelMessage = new Label(message, skin);
        table.add(labelMessage);

        table.row();
        TextButton textButtonCancel = new TextButton("Cancel", skin);
        textButtonCancel.pad(buttonsPadding);
        textButtonCancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onCancelClicked();
            }
        });
        table.add(textButtonCancel).size(buttonsWidth, buttonsHeight / 2).padTop(20);

        this.add(table);
        table.debug();
    }

    private void finalizeWindow() {
        setDefaultSizeAndPosition();
        setDefaultStyle();
        setVisible(false);
    }

    private void setDefaultSizeAndPosition() {
        setWidth(300);
        setHeight(140);
        setPosition(Gdx.graphics.getWidth() / 2 - getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - getHeight() / 2);
    }

    private void onCancelClicked() {
        NetworkManager.getInstance().disconnectClientThread();
        menuScreen.hideWindow(getWindowName());
        menuScreen.showWindow(WindowName.MAIN_MENU);
    }

    private void setDefaultStyle() {
        setColor(1, 1, 1, 0.8f);
    }

    @Override
    public WindowName getWindowName() {
        return WindowName.SERVER_CONNECTION;
    }

    public void setMessage(final String message) {
        this.message = message;
        labelMessage.setText(message);
    }

    public String getMessage() {
        return message;
    }
}
