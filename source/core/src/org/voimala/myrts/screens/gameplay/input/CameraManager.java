package org.voimala.myrts.screens.gameplay.input;

/* Allows the player to move the camera while holding right mouse button. */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraManager {
    private boolean isMovingCamera = false;
    private boolean isStartPointSet = false;
    private int startX = -1;
    private int startY = -1;
    private OrthographicCamera worldCamera = null;
    private long cameraMovementStoppedTimestampMs = 0;

    public CameraManager(OrthographicCamera worldCamera) {
        this.worldCamera = worldCamera;
    }

    public void update() {
        handleDesktopCameraMovement();
        moveCamera();
    }

    private void handleDesktopCameraMovement() {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            if (isStartPointSet()) {
                if (Gdx.input.getX() != startX || Gdx.input.getY() != startY) {
                    setMovingCamera(true);
                }
            } else {
                setStartX(Gdx.input.getX());
                setStartY(Gdx.input.getY());
                setStartPointSet(true);
            }
        } else {
            if (isMovingCamera()) {
                cameraMovementStoppedTimestampMs = System.currentTimeMillis();
            }

            setMovingCamera(false);
        }
    }

    private void moveCamera() {
        if (isMovingCamera()) {
            worldCamera.translate(Gdx.input.getX() - getStartX(),
                    getStartY() - Gdx.input.getY());
            worldCamera.update();
        }
    }

    public boolean isStartPointSet() {
        return isStartPointSet;
    }

    public void setStartPointSet(boolean isStartPointSet) {
        this.isStartPointSet = isStartPointSet;
    }

    public boolean isMovingCamera() {
        return isMovingCamera;
    }

    /** If argument is false, resets starting point. */
    public void setMovingCamera(boolean isMovingCamera) {
        this.isMovingCamera = isMovingCamera;

        if (isMovingCamera == false) {
            startX = -1;
            startY = -1;
            isStartPointSet = false;
        }
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public long timeSinceCameraMovementStoppedInMs() {
        return System.currentTimeMillis() - cameraMovementStoppedTimestampMs;
    }

}
