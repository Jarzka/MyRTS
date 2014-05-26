package org.voimala.myrts.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.app.GameplayStartMethod;
import org.voimala.myrts.screens.GameScreen;

public class MenuScreen extends GameScreen {
    public MenuScreen(GameMain gameMain) {
        super(gameMain);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            gameMain.startGame(GameplayStartMethod.SINGLEPLAYER);
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
