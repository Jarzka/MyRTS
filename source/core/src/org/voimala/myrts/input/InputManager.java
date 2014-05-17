package org.voimala.myrts.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.voimala.myrts.scenes.gameplay.WorldController;
import org.voimala.myrts.scenes.gameplay.units.Unit;

public class InputManager {

    private WorldController worldController;

    private boolean mouseButtonLeftPressedLastFrame;
    private boolean mouseButtonRightPressedLastFrame;

    private CameraManager cameraManager;

    private Rectangle unitSelectionRectangle;
    private boolean isDrawingRectangle = false;
    private float rectangleStartX = -1;
    private float rectangleStartY = -1;

    /** Returns null if there is no active selection rectangle. */
    public Rectangle getUnitSelectionRectangle() {
        return unitSelectionRectangle;
    }

    public InputManager(WorldController worldController) {
        this.worldController = worldController;
        cameraManager = new CameraManager(worldController.getWorldCamera());
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

        mouseButtonLeftPressedLastFrame = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        mouseButtonRightPressedLastFrame = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
    }

    private void handleCameraManagement() {
        cameraManager.update();
    }

    private void handleSingleUnitSelection() {
        handleDesktopSingleUnitSelection();
    }

    private void handleDesktopSingleUnitSelection() {
        if (mouseButtonLeftPressedLastFrame && !Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            unselectAllOwnUnits();
            for (Unit unit : worldController.getUnitContainer().getUnits()) {
                Vector3 mouseLocationInWorld = worldController.getWorldCamera().unproject(new Vector3(
                        Gdx.input.getX(),
                        Gdx.input.getY(),
                        0));
                // TODO CHECK TEAM
                if (unit.onCollision(new Vector2(mouseLocationInWorld.x, mouseLocationInWorld.y))) {
                    unit.setSelected(true);
                    break;
                }
            }

        }
    }

    private void handleSelectionRectangle() {
        if (mouseButtonLeftPressedLastFrame && !Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            // TODO Toimii vain vetämällä hiirellä oikealle ylös.
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
                if (rectangleWorld.contains(unit.getX(), unit.getY())) {
                    unit.setSelected(true);
                }
            }
        }
    }

    private void handleDrawSelectionRectangle() {
        handleDesktopDrawSelectionArea();
    }

    private void handleDesktopDrawSelectionArea() {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (unitSelectionRectangle == null) {
                unitSelectionRectangle = new Rectangle();
            }

            if (!isDrawingRectangle) {
                isDrawingRectangle = true;
                rectangleStartX = Gdx.input.getX();
                rectangleStartY = Gdx.graphics.getHeight() - Gdx.input.getY();
            }

            unitSelectionRectangle.x = rectangleStartX;
            unitSelectionRectangle.y = rectangleStartY;
            unitSelectionRectangle.width = Gdx.input.getX() - rectangleStartX;
            unitSelectionRectangle.height = (Gdx.graphics.getHeight() - Gdx.input.getY()) - rectangleStartY;
        } else {
            isDrawingRectangle = false;
            rectangleStartX = -1;
            rectangleStartY = -1;
            unitSelectionRectangle = null;
        }
    }

    private void handleUnitCommands() {
        handleDesktopUnitCommands();
    }

    private void handleDesktopUnitCommands() {
        handleDesktopUnitMoveCommand();
    }

    private void handleDesktopUnitMoveCommand() {
        /** It is possible that at least one unit is selected while the player
         * stops moving camera by stopping pressing right mouse button. To prevent this,
         * at least x seconds need to be passed sincle camera movement stopped. */
        if (cameraManager.timeSinceCameraMovementStoppedInMs() > 100
                && mouseButtonRightPressedLastFrame
                && !Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            for (Unit unit : worldController.getUnitContainer().getUnits()) {
                if (unit.isSelected()) { // TODO CHECK TEAM
                    Vector3 mouseLocationInWorld = worldController.getWorldCamera().unproject(new Vector3(Gdx.input.getX(),
                            Gdx.input.getY(),
                            0));
                    sendUnitMoveCommandToServer(unit, mouseLocationInWorld);
                }
            }
        }
    }

    // TODO Move to another class?
    private void sendUnitMoveCommandToServer(final Unit unit, final Vector3 mouseLocationInWorld) {
        worldController.getClientThread().sendMessage(
                "<UNIT_MOVE|" + unit.getUnitId() + "|" + mouseLocationInWorld.x + "|" + mouseLocationInWorld.y + ">");
    }

    private void unselectAllOwnUnits() {
        // TODO CHECK TEAM
        for (Unit unit : worldController.getUnitContainer().getUnits()) {
            unit.setSelected(false);
        }
    }

}
