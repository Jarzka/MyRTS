package org.voimala.myrts.scenes.gameplay.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import org.voimala.myrts.scenes.gameplay.WorldController;

/* This class is used for handling desktop zoom with mouse wheel. */

public class GameplayInputProcessor implements InputProcessor{
    WorldController worldController = null;

    public GameplayInputProcessor(WorldController worldController) {
        this.worldController = worldController;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        handleInputZoom(amount);

        return true;
    }

    private void handleInputZoom(int amount) {
        OrthographicCamera camera = worldController.getWorldCamera();

        if (amount < 0) {
            if (camera.zoom > 1) {
                camera.zoom -= 1;
                camera.update();
            }
        } else if (amount > 0) {
            if (camera.zoom < 10) {
                camera.zoom += 1;
                camera.update();
            }
        }
    }
}
