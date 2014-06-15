package org.voimala.myrts.app;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import org.voimala.myrts.graphics.SpriteContainer;
import org.voimala.myrts.networking.ConnectionState;
import org.voimala.myrts.networking.NetworkManager;
import org.voimala.myrts.screens.ScreenName;
import org.voimala.myrts.screens.gameplay.GameplayScreen;
import org.voimala.myrts.screens.gameplay.world.GameMode;
import org.voimala.myrts.screens.gameplay.world.Player;
import org.voimala.myrts.screens.menu.MenuScreen;

public class GameMain extends Game {

    public static int LOG_LEVEL = Application.LOG_DEBUG;

    private static final String TAG = GameMain.class.getName();

    private boolean paused = false;

    private Player player = new Player();

    private static GameMain instanceOfThis;

    private ScreenName nextScreen = null; /* If not null, change the screen to this. */

    private GameMain() {}

    public static GameMain getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new GameMain();
        }

        return instanceOfThis;
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(GameMain.LOG_LEVEL);

        setScreen(new MenuScreen());
        CommandLineParser.getInstance().handleCommandLineArguments();
    }


    @Override
    public void render() {
        if (!paused) {
            getScreen().render(Gdx.graphics.getDeltaTime());
        }

        checkNextScreen();
    }

    private void checkNextScreen() {
        if (nextScreen == ScreenName.GAMEPLAY) {
            startGame();
            nextScreen = null;
        }
    }

    private void startGame() {
        GameplayScreen gameplayScreen = new GameplayScreen();
        if (NetworkManager.getInstance().getClientConnectionState() == ConnectionState.CONNECTED) {
            gameplayScreen.setGameMode(GameMode.MULTIPLAYER);
        } else {
            gameplayScreen.setGameMode(GameMode.SINGLEPLAYER);
        }

        setScreen(gameplayScreen);
    }


    @Override
    public void resize(int width, int height) {
        getScreen().resize(width, height);
    }

    @Override
    public void pause() {
        /* TODO This is called on the desktop when the window is minimized.
         * This is not the desired case. */
        //paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void dispose() {
        SpriteContainer.getInstance().freeResources();
        NetworkManager.getInstance().disconnectAll();
        getScreen().dispose();
    }

    public void setNextScreenToGameplay() {
        nextScreen = ScreenName.GAMEPLAY;
    }

    public Player getPlayer() {
        return player;
    }

}
