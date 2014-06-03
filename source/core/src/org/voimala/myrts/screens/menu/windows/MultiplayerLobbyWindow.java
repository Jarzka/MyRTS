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

    private SelectBox selectBoxSlot1;
    private SelectBox selectBoxSlot2;
    private SelectBox selectBoxSlot3;
    private SelectBox selectBoxSlot4;
    private SelectBox selectBoxSlot5;
    private SelectBox selectBoxSlot6;
    private SelectBox selectBoxSlot7;
    private SelectBox selectBoxSlot8;

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

        table.row();
        Label labelNumber1 = new Label("1", skin);
        table.add(labelNumber1).left();
        selectBoxSlot1 = new SelectBox(skin);
        selectBoxSlot1.setItems(slotValues);
        table.add(selectBoxSlot1);
        SelectBox selectBoxFaction1 = new SelectBox(skin);
        selectBoxFaction1.setItems(factionValues);
        table.add(selectBoxFaction1);
        SelectBox selectBoxColor1 = new SelectBox(skin);
        selectBoxColor1.setItems(colorValues);
        table.add(selectBoxColor1);
        SelectBox selectBoxTeam1 = new SelectBox(skin);
        selectBoxTeam1.setItems(teamValues);
        table.add(selectBoxTeam1);

        table.row();
        Label labelNumber2 = new Label("2", skin);
        table.add(labelNumber2).left();
        selectBoxSlot2 = new SelectBox(skin);
        selectBoxSlot2.setItems(slotValues);
        table.add(selectBoxSlot2);

        table.row();
        Label labelNumber3 = new Label("3", skin);
        table.add(labelNumber3).left();
        selectBoxSlot3 = new SelectBox(skin);
        selectBoxSlot3.setItems(slotValues);
        table.add(selectBoxSlot3);

        table.row();
        Label labelNumber4 = new Label("4", skin);
        table.add(labelNumber4).left();
        selectBoxSlot4 = new SelectBox(skin);
        selectBoxSlot4.setItems(slotValues);
        table.add(selectBoxSlot4);

        table.row();
        Label labelNumber5 = new Label("5", skin);
        table.add(labelNumber5).left();
        selectBoxSlot5 = new SelectBox(skin);
        selectBoxSlot5.setItems(slotValues);
        table.add(selectBoxSlot5);

        table.row();
        Label labelNumber6 = new Label("6", skin);
        table.add(labelNumber6).left();
        selectBoxSlot6 = new SelectBox(skin);
        selectBoxSlot6.setItems(slotValues);
        table.add(selectBoxSlot6);

        table.row();
        Label labelNumber7 = new Label("7", skin);
        table.add(labelNumber7).left();
        selectBoxSlot7 = new SelectBox(skin);
        selectBoxSlot7.setItems(slotValues);
        table.add(selectBoxSlot7);

        table.row();
        Label labelNumber8 = new Label("8", skin);
        table.add(labelNumber8).left();
        selectBoxSlot8 = new SelectBox(skin);
        selectBoxSlot8.setItems(slotValues);
        table.add(selectBoxSlot8);

        // TODO

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
