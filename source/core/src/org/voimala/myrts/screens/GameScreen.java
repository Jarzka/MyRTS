package org.voimala.myrts.screens;

import com.badlogic.gdx.Screen;
import org.voimala.myrts.app.GameMain;

public abstract class GameScreen implements Screen {

    protected GameMain gameMain = null;

    public GameScreen(GameMain gameMain) {
        this.gameMain = gameMain;
    }
}
