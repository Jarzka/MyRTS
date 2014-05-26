package org.voimala.myrts.app;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import org.voimala.myrts.graphics.SpriteContainer;
import org.voimala.myrts.multiplayer.NetworkManager;
import org.voimala.myrts.screens.gameplay.GameplayScreen;

public class GameMain extends Game {

    public static int LOG_LEVEL = Application.LOG_DEBUG;

    private static final String TAG = GameMain.class.getName();

    private boolean paused = false;

    private NetworkManager networkManager = null;
    private CommandLineParser commandLineParser = null;

    public GameMain(String[] commandLineArguments) {
        super();
        CommandLineParser.getInstance().saveCommandLineArguments(commandLineArguments);
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(GameMain.LOG_LEVEL);

        setScreen(new GameplayScreen(this));
    }


    @Override
    public void render() {
        if (!paused) {
            getScreen().render(Gdx.graphics.getDeltaTime());
        }
    }


    @Override
    public void resize(int width, int height) {
        getScreen().resize(width, height);
    }

    @Override
    public void pause() {
        /* TODO This is called on the desktop when the windows is minimized.
         * This is not the desired case. */
        //paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void dispose() {
        SpriteContainer.freeResources();
        NetworkManager.getInstance().quit();
        getScreen().dispose();
    }

}
