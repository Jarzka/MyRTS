package org.voimala.myrts.screens.menu.windows;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.networking.Chat;
import org.voimala.myrts.networking.NetworkManager;
import org.voimala.myrts.networking.RTSProtocolManager;
import org.voimala.myrts.screens.menu.MenuScreen;

public class MultiplayerLobbyWindow extends AbstractMenuWindow {

    private Skin skin;

    private String[] playerValues;
    private String[] factionValues;
    private String[] colorValues;
    private String[] teamValues;

    private List textAreaChatMessages;
    private TextField textFieldComposeMessage;

    public MultiplayerLobbyWindow(Skin skin, MenuScreen menuScreen) {
        super("Multiplayer Lobby", skin, menuScreen);
        this.skin = skin;

        initialize();
    }

    public void initialize() {
        initializeSelectBoxArrayValues();
        buildWidgets();
        finalizeWindow();
    }

    private void buildWidgets() {
        int buttonsWidth = 150;
        int buttonsHeight = 50;
        int buttonsPadding = 10;
        int buttonRowPadding = 2;

        Table table = new Table();

        table.pad(10);
        table.columnDefaults(0).width(25);
        table.columnDefaults(1).width(200);
        table.columnDefaults(2).width(100);
        table.columnDefaults(3).width(100);
        table.columnDefaults(4).width(45);
        table.columnDefaults(5).width(100);

        // Build the heading
        table.row();
        Label labelNumber = new Label("#", skin);
        table.add(labelNumber).left();
        Label labelPlayer = new Label("Player", skin);
        table.add(labelPlayer).left();
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
            SelectBox selectBoxPlayer = new SelectBox(skin);
            selectBoxPlayer.setItems(playerValues);
            selectBoxPlayer.setName("player" + String.valueOf(i));
            table.add(selectBoxPlayer).left();
            SelectBox selectBoxFaction = new SelectBox(skin);
            selectBoxFaction.setItems(factionValues);
            selectBoxFaction.setName("faction" + String.valueOf(i));
            table.add(selectBoxFaction).left();
            SelectBox selectBoxColor = new SelectBox(skin);
            selectBoxColor.setItems(colorValues);
            selectBoxColor.setSelectedIndex(i - 1);
            selectBoxColor.setName("color" + String.valueOf(i));
            table.add(selectBoxColor).left();
            SelectBox selectBoxTeam = new SelectBox(skin);
            selectBoxTeam.setItems(teamValues);
            selectBoxTeam.setName("team" + String.valueOf(i));
            table.add(selectBoxTeam).left();
            CheckBox checkBoxReady = new CheckBox("Ready", skin);
            checkBoxReady.setName("ready" + String.valueOf(i));
            table.add(checkBoxReady).left();
        }

        // Chat
        table.row();
        textAreaChatMessages = new List(skin);
        ScrollPane scroll = new ScrollPane(textAreaChatMessages, skin);
        table.add(scroll).left().width(500).height(200).padTop(20).colspan(5);
        table.row();
        textFieldComposeMessage = new TextField("", skin);
        table.add(textFieldComposeMessage).left().width(500).colspan(5);

        TextButton buttonStart = new TextButton("Start", skin);
        table.add(buttonStart).bottom().left().width((int) (buttonsWidth * 0.8)).height(buttonsHeight / 2).pad(buttonsPadding);
        TextButton buttonDisconnect = new TextButton("Disconnect", skin);
        buttonDisconnect.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onDisconnectClicked();
            }
        });
        table.add(buttonDisconnect).bottom().right().width((int) (buttonsWidth * 0.8)).height(buttonsHeight / 2).pad(buttonsPadding);

        this.add(table);
    }

    private void initializeSelectBoxArrayValues() {
        playerValues = new String[3];
        playerValues[0] = "Open";
        playerValues[1] = "Closed";
        playerValues[2] = "Test AI";

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
        setWidth(900);
        setHeight(600);
        setPosition(Gdx.graphics.getWidth() / 2 - getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - getHeight() / 2);
    }

    private void onDisconnectClicked() {
        NetworkManager.getInstance().disconnectAll();
    }

    private void setDefaultStyle() {
        setColor(1, 1, 1, 0.8f);
    }

    public void update() {
        handleEventSendChatMessage();
        updateChatMessages();
    }

    private void handleEventSendChatMessage() {
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER) && textFieldComposeMessage.getText().length() > 0) {
            sendChatMessage();
        }
    }

    private void sendChatMessage() {
        NetworkManager.getInstance().getClientThread().sendMessage(
                RTSProtocolManager.getInstance().createNetworkMessageChatMessage(
                        GameMain.getInstance().getPlayer().getName(), textFieldComposeMessage.getText()));
        textFieldComposeMessage.setText("");
    }

    private void updateChatMessages() {
        textAreaChatMessages.setItems(Chat.getInstance().getChatMessagesForChatBox());
    }

    @Override
    public WindowName getWindowName() {
        return WindowName.MULTIPLAYER_LOBBY;
    }
}
