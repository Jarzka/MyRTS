package org.voimala.myrtsengine.screens.gameplay.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.voimala.myrtsengine.app.GameMain;
import org.voimala.myrtsengine.screens.gameplay.GameplayScreen;
import org.voimala.myrtsengine.screens.gameplay.input.commands.ExecuteCommandMethod;
import org.voimala.myrtsengine.screens.gameplay.input.commands.RTSCommandMoveUnit;
import org.voimala.myrtsengine.screens.gameplay.input.commands.RTSCommandSelectUnit;
import org.voimala.myrtsengine.screens.gameplay.units.AbstractUnit;
import org.voimala.myrtsengine.screens.gameplay.world.GameMode;

/** This class is used for handling local player input.
 * NOTE: Chat input is handled in GameplayChatInput class. */

public class LocalGameplayInputManager {

    private static final String TAG = LocalGameplayInputManager.class.getName();

    private GameplayScreen gameplayScreen;

    private boolean mouseButtonLeftPressedLastFrame;
    private boolean mouseButtonRightPressedLastFrame;

    private CameraManager cameraManager;

    private Rectangle unitSelectionRectangle;
    private boolean isDrawingRectangle = false;
    private float rectangleStartXScreen = -1;
    private float rectangleStartYScreen = -1;

    public LocalGameplayInputManager(final GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
        cameraManager = new CameraManager(gameplayScreen.getWorldController().getWorldCamera());
    }

    /** Returns null if there is no active selection rectangle. */
    public Rectangle getUnitSelectionRectangle() {
        return unitSelectionRectangle;
    }

    public void update() {
        handleUserInput();
    }

    private void handleUserInput() {
        handleCameraManagement();

        handleSingleUnitSelection();
        handleSelectUnitsWithinRectangle();
        handleDrawSelectionRectangle();

        handleUnitMoveCommand();

        mouseButtonLeftPressedLastFrame = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        mouseButtonRightPressedLastFrame = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
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
            for (AbstractUnit unit : gameplayScreen.getWorldController().getUnitContainerForSpecificPlayer(
                    GameMain.getInstance().getPlayer().getNumber()).getUnits()) {
                Vector3 mouseLocationInWorld = gameplayScreen.getWorldController().getWorldCamera().unproject(new Vector3(
                        Gdx.input.getX(),
                        Gdx.input.getY(),
                        0));
                if (unit.onCollision(new Vector2(mouseLocationInWorld.x, mouseLocationInWorld.y))
                        && unit.getPlayerNumber() == GameMain.getInstance().getPlayer().getNumber()) {
                    gameplayScreen.getRTSCommandExecuter().executeCommand(ExecuteCommandMethod.EXECUTE_LOCALLY,
                            new RTSCommandSelectUnit(GameMain.getInstance().getPlayer().getNumber(), unit.getObjectId()));
                    break;
                }
            }

        }
    }

    private void handleSelectUnitsWithinRectangle() {
        if (unitSelectionRectangle != null) {
            if (mouseButtonLeftPressedLastFrame && !Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                Vector3 rectangleTopLeftWorld = gameplayScreen.getWorldController().getWorldCamera().unproject(
                        new Vector3(unitSelectionRectangle.x,
                                Gdx.graphics.getHeight() - unitSelectionRectangle.y - unitSelectionRectangle.height,
                                0)
                );
                Vector3 rectangleTopRightWorld = gameplayScreen.getWorldController().getWorldCamera().unproject(
                        new Vector3(unitSelectionRectangle.x + unitSelectionRectangle.width,
                                Gdx.graphics.getHeight() - unitSelectionRectangle.y - unitSelectionRectangle.height,
                                0)
                );
                Vector3 rectangleBottomLeftWorld = gameplayScreen.getWorldController().getWorldCamera().unproject(
                        new Vector3(unitSelectionRectangle.x,
                                Gdx.graphics.getHeight() - unitSelectionRectangle.y,
                                0)
                );
                Vector3 rectangleBottomRightWorld = gameplayScreen.getWorldController().getWorldCamera().unproject(
                        new Vector3(unitSelectionRectangle.x + unitSelectionRectangle.width,
                                Gdx.graphics.getHeight() - unitSelectionRectangle.y,
                                0)
                );

                Rectangle rectangleWorld = new Rectangle(rectangleBottomLeftWorld.x,
                        rectangleBottomLeftWorld.y,
                        rectangleBottomRightWorld.x - rectangleBottomLeftWorld.x,
                        rectangleTopRightWorld.y - rectangleBottomRightWorld.y);

                for (AbstractUnit unit : gameplayScreen.getWorldController().getUnitContainerForSpecificPlayer(
                        GameMain.getInstance().getPlayer().getNumber()).getUnits()) {
                    if (rectangleWorld.contains(unit.getX(), unit.getY())
                            && unit.getPlayerNumber() == GameMain.getInstance().getPlayer().getNumber()) {
                       gameplayScreen.getRTSCommandExecuter().executeCommand(ExecuteCommandMethod.EXECUTE_LOCALLY,
                               new RTSCommandSelectUnit(GameMain.getInstance().getPlayer().getNumber(), unit.getObjectId()));
                        unit.setSelected(true);
                    }
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

    private void handleUnitMoveCommand() {
        handleMouseInputUnitMoveCommand();
    }

    private void handleMouseInputUnitMoveCommand() {
        /** It is possible that at least one unit is selected while the player
         * stops moving camera by stopping pressing right mouse button. To prevent this,
         * at least x seconds need to be passed sincle camera movement stopped. */
        if (cameraManager.timeSinceCameraMovementStoppedInMs() > 100
                && mouseButtonRightPressedLastFrame
                && !Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            for (AbstractUnit unit : gameplayScreen.getWorldController().getUnitContainerForSpecificPlayer(
                    GameMain.getInstance().getPlayer().getNumber()).getUnits()) {
                if (unit.isSelected() && unit.getPlayerNumber() == GameMain.getInstance().getPlayer().getNumber()) {
                    handleCommandMoveUnit(unit);
                }
            }
        }
    }

    private void handleCommandMoveUnit(AbstractUnit unit) {
        Vector3 mouseLocationInWorld = gameplayScreen.getWorldController().getWorldCamera().unproject(
                new Vector3(Gdx.input.getX(),
                        Gdx.input.getY(),
                        0));

        ExecuteCommandMethod method = ExecuteCommandMethod.EXECUTE_LOCALLY;
        if (gameplayScreen.getWorldController().getGameplayScreen().getGameMode() == GameMode.SINGLEPLAYER) {
            method = ExecuteCommandMethod.EXECUTE_LOCALLY;
        } else if (gameplayScreen.getWorldController().getGameplayScreen().getGameMode() == GameMode.MULTIPLAYER) {
            method = ExecuteCommandMethod.ADD_TO_LOCAL_INPUT_QUEUE;
        }

        gameplayScreen.getRTSCommandExecuter().executeCommand(
                method,
                new RTSCommandMoveUnit(
                        GameMain.getInstance().getPlayer().getNumber(),
                        unit.getObjectId(),
                        new Vector2(mouseLocationInWorld.x, mouseLocationInWorld.y)));
    }

    private void unselectAllUnits() {
        for (AbstractUnit unit : gameplayScreen.getWorldController().getUnitContainerForSpecificPlayer(
                GameMain.getInstance().getPlayer().getNumber()).getUnits()) {
            unit.setSelected(false);
        }
    }

}
