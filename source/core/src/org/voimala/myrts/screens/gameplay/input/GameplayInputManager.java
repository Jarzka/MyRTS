package org.voimala.myrts.screens.gameplay.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.networking.Chat;
import org.voimala.myrts.networking.ListenSocketThread;
import org.voimala.myrts.networking.NetworkManager;
import org.voimala.myrts.networking.RTSProtocolManager;
import org.voimala.myrts.screens.gameplay.GameplayScreen;
import org.voimala.myrts.screens.gameplay.world.GameMode;
import org.voimala.myrts.screens.gameplay.world.WorldController;
import org.voimala.myrts.screens.gameplay.units.Unit;

public class GameplayInputManager {

    private static final String TAG = GameplayInputManager.class.getName();

    private GameplayScreen gameplayScreen;
    private WorldController worldController;

    private boolean mouseButtonLeftPressedLastFrame;
    private boolean mouseButtonRightPressedLastFrame;

    private CameraManager cameraManager;

    private Rectangle unitSelectionRectangle;
    private boolean isDrawingRectangle = false;
    private float rectangleStartXScreen = -1;
    private float rectangleStartYScreen = -1;

    private boolean isChatTypingOn = false;
    private String userChatMessage = "";

    /** Returns null if there is no active selection rectangle. */
    public Rectangle getUnitSelectionRectangle() {
        return unitSelectionRectangle;
    }

    public GameplayInputManager(final GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
        this.worldController = gameplayScreen.getWorldController();
        cameraManager = new CameraManager(gameplayScreen.getWorldController().getWorldCamera());
    }

    public void update() {
        handleUserInput();
    }

    private void handleUserInput() {
        handleCameraManagement();
        handleSingleUnitSelection();
        handleSelectionRectangle();
        handleDrawSelectionRectangle();
        handleUnitCommands();
        handleChat();

        mouseButtonLeftPressedLastFrame = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        mouseButtonRightPressedLastFrame = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
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

    private void handleUserInputSendMessage() {
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER) && isChatTypingOn && userChatMessage.length() > 0) {
            NetworkManager.getInstance().getClientThread().sendMessage(userChatMessage);
            userChatMessage = "";
            isChatTypingOn = false;
        }
    }

    private void handleCameraManagement() {
        cameraManager.update();
    }

    private void handleSingleUnitSelection() {
        handleMouseInputSelectSingleUnit();
    }

    private void handleMouseInputSelectSingleUnit() {
        if (mouseButtonLeftPressedLastFrame && !Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            unselectAllUnits();
            for (Unit unit : worldController.getUnitContainer().getUnits()) {
                Vector3 mouseLocationInWorld = worldController.getWorldCamera().unproject(new Vector3(
                        Gdx.input.getX(),
                        Gdx.input.getY(),
                        0));
                if (unit.onCollision(new Vector2(mouseLocationInWorld.x, mouseLocationInWorld.y))
                        && unit.getPlayerNumber() == GameMain.getInstance().getPlayer().getNumber()) {
                    unit.setSelected(true);
                    break;
                }
            }

        }
    }

    private void handleSelectionRectangle() {
        if (mouseButtonLeftPressedLastFrame && !Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            Vector3 rectangleTopLeftWorld = worldController.getWorldCamera().unproject(
                    new Vector3(unitSelectionRectangle.x,
                            Gdx.graphics.getHeight() - unitSelectionRectangle.y - unitSelectionRectangle.height,
                            0));
            Vector3 rectangleTopRightWorld = worldController.getWorldCamera().unproject(
                    new Vector3(unitSelectionRectangle.x + unitSelectionRectangle.width,
                            Gdx.graphics.getHeight() - unitSelectionRectangle.y - unitSelectionRectangle.height,
                            0));
            Vector3 rectangleBottomLeftWorld = worldController.getWorldCamera().unproject(
                    new Vector3(unitSelectionRectangle.x,
                            Gdx.graphics.getHeight() - unitSelectionRectangle.y,
                            0));
            Vector3 rectangleBottomRightWorld = worldController.getWorldCamera().unproject(
                    new Vector3(unitSelectionRectangle.x + unitSelectionRectangle.width,
                            Gdx.graphics.getHeight() - unitSelectionRectangle.y,
                            0));

            Rectangle rectangleWorld = new Rectangle(rectangleBottomLeftWorld.x,
                    rectangleBottomLeftWorld.y,
                    rectangleBottomRightWorld.x - rectangleBottomLeftWorld.x,
                    rectangleTopRightWorld.y - rectangleBottomRightWorld.y);

            for (Unit unit : worldController.getUnitContainer().getUnits()) {
                if (rectangleWorld.contains(unit.getX(), unit.getY())
                        && unit.getPlayerNumber() == GameMain.getInstance().getPlayer().getNumber()) {
                    unit.setSelected(true);
                }
            }
        }
    }

    private void handleDrawSelectionRectangle() {
        handleMouseInputDrawSelectionArea();
    }

    private void handleMouseInputDrawSelectionArea() {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (unitSelectionRectangle == null) {
                unitSelectionRectangle = new Rectangle();
            }

            if (!isDrawingRectangle) {
                isDrawingRectangle = true;
                rectangleStartXScreen = Gdx.input.getX();
                rectangleStartYScreen = Gdx.input.getY();
            }

            // Screen coordinates ("y goes down") are converted to libgdx coordinates ("y goes up") for rectangle

            if (Gdx.input.getX() > rectangleStartXScreen && Gdx.input.getY() < rectangleStartYScreen) {
                // Top right
                unitSelectionRectangle.x = rectangleStartXScreen;
                unitSelectionRectangle.y = Gdx.graphics.getHeight() - rectangleStartYScreen;
                unitSelectionRectangle.width = Math.abs(Gdx.input.getX() - rectangleStartXScreen);
                unitSelectionRectangle.height = Math.abs(Gdx.input.getY() - rectangleStartYScreen);
            } else if (Gdx.input.getX() > rectangleStartXScreen && Gdx.input.getY() > rectangleStartYScreen) {
                // Bottom right
                unitSelectionRectangle.x = rectangleStartXScreen;
                unitSelectionRectangle.y = Gdx.graphics.getHeight() - Gdx.input.getY();
                unitSelectionRectangle.width = Math.abs(Gdx.input.getX() - rectangleStartXScreen);
                unitSelectionRectangle.height = Math.abs(Gdx.input.getY() - rectangleStartYScreen);
            } else if (Gdx.input.getX() < rectangleStartXScreen && Gdx.input.getY() > rectangleStartYScreen) {
                // Bottom left
                unitSelectionRectangle.x = Gdx.input.getX();
                unitSelectionRectangle.y = Gdx.graphics.getHeight() - Gdx.input.getY();
                unitSelectionRectangle.width = Math.abs(Gdx.input.getX() - rectangleStartXScreen);
                unitSelectionRectangle.height = Math.abs(Gdx.input.getY() - rectangleStartYScreen);
            } else if (Gdx.input.getX() < rectangleStartXScreen && Gdx.input.getY() < rectangleStartYScreen) {
                // Top left
                unitSelectionRectangle.x = Gdx.input.getX();
                unitSelectionRectangle.y = Gdx.graphics.getHeight() - rectangleStartYScreen;
                unitSelectionRectangle.width = Math.abs(Gdx.input.getX() - rectangleStartXScreen);
                unitSelectionRectangle.height = Math.abs(Gdx.input.getY() - rectangleStartYScreen);
            }
        } else {
            isDrawingRectangle = false;
            rectangleStartXScreen = -1;
            rectangleStartYScreen = -1;
            unitSelectionRectangle = null;
        }
    }

    private void handleUnitCommands() {
        handleMouseInputUnitCommands();
    }

    private void handleMouseInputUnitCommands() {
        handleMouseInputUnitMoveCommand();
    }

    private void handleMouseInputUnitMoveCommand() {
        /** It is possible that at least one unit is selected while the player
         * stops moving camera by stopping pressing right mouse button. To prevent this,
         * at least x seconds need to be passed sincle camera movement stopped. */
        if (cameraManager.timeSinceCameraMovementStoppedInMs() > 100
                && mouseButtonRightPressedLastFrame
                && !Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            for (Unit unit : worldController.getUnitContainer().getUnits()) {
                if (unit.isSelected() && unit.getPlayerNumber() == GameMain.getInstance().getPlayer().getNumber()) {
                    handleCommandMoveUnit(unit);
                }
            }
        }
    }

    private void handleCommandMoveUnit(Unit unit) {
        Vector3 mouseLocationInWorld = worldController.getWorldCamera().unproject(
                new Vector3(Gdx.input.getX(),
                Gdx.input.getY(),
                0));


        if (worldController.getGameplayScreen().getGameMode() == GameMode.SINGLEPLAYER) {
            // Process command locally
            unit.getMovement().setPathPoint(new Vector2(mouseLocationInWorld.x, mouseLocationInWorld.y));
        } else if (worldController.getGameplayScreen().getGameMode() == GameMode.MULTIPLAYER) {
            // Send command to the server
            String message = RTSProtocolManager.getInstance().createNetworkMessageMoveUnit(
                    unit.getObjectId(),
                    mouseLocationInWorld);
            ListenSocketThread listenSocketThread = NetworkManager.getInstance().getClientThread();
            if (listenSocketThread != null) {
                listenSocketThread.sendMessage(message);
            }
        }
    }


    private void unselectAllUnits() {
        for (Unit unit : worldController.getUnitContainer().getUnits()) {
            unit.setSelected(false);
        }
    }

    public void handleUserChatInput(char character) {
        if (isChatTypingOn) { // TODO Backspace?
            userChatMessage += character;
        }
    }

    public CharSequence getUserChatMessage() {
        return userChatMessage;
    }

    public boolean isChatTypingOn() {
        return isChatTypingOn;
    }
}
