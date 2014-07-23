package org.voimala.myrtsengine.app;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import org.voimala.myrtsengine.graphics.SpriteContainer;
import org.voimala.myrtsengine.networking.NetworkManager;
import org.voimala.myrtsengine.screens.ScreenName;
import org.voimala.myrtsengine.screens.gameplay.GameplayScreen;
import org.voimala.myrtsengine.screens.gameplay.world.GameMode;
import org.voimala.myrtsengine.screens.gameplay.world.Player;
import org.voimala.myrtsengine.screens.gameplay.world.WorldController;
import org.voimala.myrtsengine.screens.loadgameplay.LoadGameScreen;
import org.voimala.myrtsengine.screens.menu.MenuScreen;

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
        if (nextScreen == ScreenName.LOAD_GAMEPLAY) {
            loadGame();
            nextScreen = null;
        } else if (nextScreen == ScreenName.GAMEPLAY) {
            startGame();
            nextScreen = null;
        }
    }

    private void loadGame() {
        LoadGameScreen loasGameScreen = new LoadGameScreen();
        setScreen(loasGameScreen);
    }

    private void startGame() {
        if (getScreen() instanceof  LoadGameScreen) {
            LoadGameScreen loadGameScreen = (LoadGameScreen) getScreen();
            if (loadGameScreen.isEverythingLoaded()) {
                WorldController preloadedWorldController = loadGameScreen.getWorldController();
                GameplayScreen gameplayScreen = new GameplayScreen(preloadedWorldController);
                preloadedWorldController.setGameplayScreen(gameplayScreen);
                setScreen(gameplayScreen);
            } else {
                Gdx.app.debug(TAG, "ERROR: Could not start the game because world or graphics not loaded!!!");
            }
        }
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

    public void setNextScreen(ScreenName screenName) {
        nextScreen = screenName;
    }

    public Player getPlayer() {
        return player;
    }

}
