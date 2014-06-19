package org.voimala.myrts.networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.sun.istack.internal.NotNull;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.screens.gameplay.GameplayScreen;
import org.voimala.myrts.screens.gameplay.world.GameMode;

/** This class is used for typing and sending chat messages during gameplay. */
public class GameplayChatInputManager {

    private static final String TAG = GameplayChatInputManager.class.getName();

    private static GameplayChatInputManager instanceOfThis;

    private GameplayScreen gameplayScreen;
    private boolean isChatTypingOn = false;
    private String userChatMessage = "";

    private GameplayChatInputManager() {}

    public static GameplayChatInputManager getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new GameplayChatInputManager();
        }

        return instanceOfThis;
    }

    public void setGameplayScreen(@NotNull final GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
    }

    public void update() {
        handleChat();
    }

    private void handleChat() {
        handleUserInputTurnChatTypingOn();
        handleUserInputSendMessage();
    }

    private void handleUserInputTurnChatTypingOn() {
        if (gameplayScreen.getGameMode() == GameMode.MULTIPLAYER) {
            if (Gdx.input.isKeyPressed(Input.Keys.Y) && !isChatTypingOn) {
                isChatTypingOn = true;
            }
        }
    }

    public void handleUserChatInput(char character) {
        if (isChatTypingOn) {
            if (character == '\b') {
                userChatMessage = userChatMessage.substring(0, userChatMessage.length() - 1);
            } else {
                userChatMessage += character;
            }
        }
    }

    private void handleUserInputSendMessage() {
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER) && isChatTypingOn && userChatMessage.length() > 0) {
            NetworkManager.getInstance().getClientThread().sendMessage(
                    RTSProtocolManager.getInstance().createNetworkMessageChatMessage(
                            GameMain.getInstance().getPlayer().getName(),
                            userChatMessage));
            userChatMessage = "";
            isChatTypingOn = false;
        }
    }

    public CharSequence getUserChatMessage() {
        return userChatMessage;
    }

    public boolean isChatTypingOn() {
        return isChatTypingOn;
    }
}
