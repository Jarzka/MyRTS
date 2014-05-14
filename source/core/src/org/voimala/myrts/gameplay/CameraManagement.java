package org.voimala.myrts.gameplay;

/* Allows the player to move the camera while holding right mouse button. */

public class CameraManagement {
    private boolean isMovingCamera = false;
    private boolean isStartPointSet = false;
    private int startX = -1;
    private int startY = -1;

    public CameraManagement() {
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
}
