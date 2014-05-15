package org.voimala.myrts.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.voimala.myrts.gameplay.WorldController;
import org.voimala.myrts.gameplay.units.Unit;

public class InputManager {

    private WorldController worldController;
    private boolean mouseButtonLeftPressedLastFrame;
    private boolean mouseButtonRightPressedLastFrame;
    private CameraManager cameraManager;

    public InputManager(WorldController worldController) {
        this.worldController = worldController;
        cameraManager = new CameraManager(worldController.getWorldCamera());
    }

    public void update() {
        handleUserInput();
    }

    private void handleUserInput() {
        handleCameraManagement();

        handleDesktopUnitSelection();
        handleDesktopUnitCommands();

        handleTouchUnitSelection();
        handleTouchDesktopUnitCommands();

        mouseButtonLeftPressedLastFrame = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        mouseButtonRightPressedLastFrame = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
    }

    private void handleCameraManagement() {
        cameraManager.update();
    }

    private void handleDesktopUnitSelection() {
        if (mouseButtonLeftPressedLastFrame && !Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            unselectAllOwnUnits();
            for (Unit unit : worldController.getUnitContainer().getUnits()) {
                Vector3 mouseLocationInWorld = worldController.getWorldCamera().unproject(new Vector3(Gdx.input.getX(),
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
                    unit.getMovement().setPathPoint(new Vector2(mouseLocationInWorld.x, mouseLocationInWorld.y));
                }
            }
        }
    }


    private void handleTouchCameraManagement() {
    }

    private void handleTouchUnitSelection() {

    }

    private void handleTouchDesktopUnitCommands() {

    }

    private void unselectAllOwnUnits() {
        // TODO CHECK TEAM
        for (Unit unit : worldController.getUnitContainer().getUnits()) {
            unit.setSelected(false);
        }
    }


}
