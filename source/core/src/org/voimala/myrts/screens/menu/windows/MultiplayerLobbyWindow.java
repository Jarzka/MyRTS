package org.voimala.myrts.screens.menu.windows;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import org.voimala.myrts.screens.menu.MenuScreen;

public class MultiplayerLobbyWindow extends AbstractMenuWindow {

    private Skin skin;

    public MultiplayerLobbyWindow(Skin skin, MenuScreen menuScreen) {
        super("Multiplayer Lobby", skin, menuScreen);
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
        table.columnDefaults(0).width(40);
        table.columnDefaults(1).width(200);
        table.columnDefaults(2).width(100);
        table.columnDefaults(3).width(100);
        table.columnDefaults(4).width(100);

        table.row();
        Label labelNumber = new Label("#", skin);
        table.add(labelNumber).left();
        Label labelSlot = new Label("Slot", skin);
        table.add(labelSlot).left();
        Label labelFaction = new Label("Faction", skin);
        table.add(labelFaction).left();
        Label labelColor = new Label("Color", skin);
        table.add(labelColor).left();
        Label labelTeam = new Label("Team", skin);
        table.add(labelTeam).left();

        // TODO

        this.add(table);
    }

    private void finalizeWindow() {
        setDefaultSizeAndPosition();
        setDefaultStyle();
        setVisible(false);
    }

    private void setDefaultSizeAndPosition() {
        setWidth(800);
        setHeight(600);
        setPosition(Gdx.graphics.getWidth() / 2 - getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - getHeight() / 2);
    }

    private void setDefaultStyle() {
        setColor(1, 1, 1, 0.8f);
    }

    @Override
    public WindowName getWindowName() {
        return WindowName.MULTIPLAYER_LOBBY;
    }
}
