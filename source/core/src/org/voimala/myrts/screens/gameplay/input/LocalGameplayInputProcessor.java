package org.voimala.myrts.screens.gameplay.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import org.voimala.myrts.screens.gameplay.GameplayScreen;

/* This class is used for handling desktop zoom with mouse wheel. */

public class LocalGameplayInputProcessor implements InputProcessor{

    private GameplayScreen gameplayScreen = null;

    public LocalGameplayInputProcessor(final GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
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
        gameplayScreen.getGameplayChatInputManager().handleUserChatInput(character);
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
        OrthographicCamera camera = gameplayScreen.getWorldCamera();

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
