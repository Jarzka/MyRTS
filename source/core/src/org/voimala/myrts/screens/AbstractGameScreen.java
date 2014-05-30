package org.voimala.myrts.screens;

import com.badlogic.gdx.Screen;
import org.voimala.myrts.app.GameMain;

public abstract class AbstractGameScreen implements Screen {

    protected GameMain gameMain;

    public AbstractGameScreen (GameMain gameMain) {
        this.gameMain = gameMain;
    }
}
