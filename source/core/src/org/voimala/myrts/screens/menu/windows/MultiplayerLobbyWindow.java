package org.voimala.myrts.screens.menu.windows;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import org.voimala.myrts.screens.menu.MenuScreen;

import java.util.ArrayList;

public class MultiplayerLobbyWindow extends AbstractMenuWindow {

    private Skin skin;

    private String[] slotValues;
    private String[] factionValues;
    private String[] colorValues;
    private String[] teamValues;

    public MultiplayerLobbyWindow(Skin skin, MenuScreen menuScreen) {
        super("Multiplayer Lobby", skin, menuScreen);
        this.skin = skin;

        initialize();
    }

    public void initialize() {
        initializeSelectBoxValues();
        buildWidgets();
        finalizeWindow();
    }

    private void buildWidgets() {
        Table table = new Table();

        table.pad(10);
        table.columnDefaults(0).width(30);
        table.columnDefaults(1).width(200);
        table.columnDefaults(2).width(100);
        table.columnDefaults(3).width(100);
        table.columnDefaults(4).width(30);

        // Build the heading
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

        // Build slot rows
        for (int i = 1; i <= 8; i++) {
            table.row();
            Label labelNum = new Label(String.valueOf(i), skin);
            table.add(labelNum).left();
            SelectBox selectBoxSlot = new SelectBox(skin);
            selectBoxSlot.setItems(slotValues);
            table.add(selectBoxSlot);
            SelectBox selectBoxFaction = new SelectBox(skin);
            selectBoxFaction.setItems(factionValues);
            table.add(selectBoxFaction);
            SelectBox selectBoxColor = new SelectBox(skin);
            selectBoxColor.setItems(colorValues);
            table.add(selectBoxColor);
            SelectBox selectBoxTeam = new SelectBox(skin);
            selectBoxTeam.setItems(teamValues);
            table.add(selectBoxTeam);
        }

        // TODO How to refer to selectboxes later?

        this.add(table);
    }

    private void initializeSelectBoxValues() {
        slotValues = new String[3];
        slotValues[0] = "Open";
        slotValues[1] = "Closed";
        slotValues[2] = "Test AI";

        factionValues = new String[2];
        factionValues[0] = "Good";
        factionValues[1] = "Bad";

        colorValues = new String[8];
        colorValues[0] = "Red";
        colorValues[1] = "Blue";
        colorValues[2] = "Green";
        colorValues[3] = "Yellow";
        colorValues[4] = "Purple";
        colorValues[5] = "Orange";
        colorValues[6] = "Black";
        colorValues[7] = "Pink";

        teamValues = new String[9];
        teamValues[0] = "1";
        teamValues[1] = "2";
        teamValues[2] = "3";
        teamValues[3] = "4";
        teamValues[4] = "5";
        teamValues[5] = "6";
        teamValues[6] = "7";
        teamValues[7] = "8";
        teamValues[8] = "-";
    }

    private void finalizeWindow() {
        setDefaultSizeAndPosition();
        setDefaultStyle();
        setVisible(false);
    }

    private void setDefaultSizeAndPosition() {
        setWidth(800);
        setHeight(480);
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
