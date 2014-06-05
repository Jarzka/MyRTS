package org.voimala.myrts.app;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import org.voimala.myrts.graphics.SpriteContainer;
import org.voimala.myrts.networking.NetworkManager;
import org.voimala.myrts.screens.gameplay.GameplayScreen;
import org.voimala.myrts.screens.gameplay.world.GameMode;
import org.voimala.myrts.screens.gameplay.world.Player;
import org.voimala.myrts.screens.menu.MenuScreen;

public class GameMain extends Game {

    public static int LOG_LEVEL = Application.LOG_DEBUG;

    private static final String TAG = GameMain.class.getName();

    private boolean paused = false;

    private NetworkManager networkManager = null;
    private CommandLineParser commandLineParser = null;

    private Player player = new Player();

    private static GameMain instanceOfThis;

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
        NetworkManager.getInstance().disconnectAll();
        getScreen().dispose();
    }

    public void startGame(GameplayStartMethod gameplayStartMethod) {
        if (gameplayStartMethod == GameplayStartMethod.SINGLEPLAYER) {
            GameplayScreen gameplayScreen = new GameplayScreen();
            gameplayScreen.setGameMode(GameMode.SINGLEPLAYER);
            setScreen(gameplayScreen);
        } else if (gameplayStartMethod == GameplayStartMethod.MULTIPLAYER_HOST) {
            NetworkManager.getInstance().hostGame(Integer.valueOf(CommandLineParser.getInstance().getCommandLineArguments().get("-port")));
            NetworkManager.getInstance().joinGame(CommandLineParser.getInstance().getCommandLineArguments().get("-ip"),
                    Integer.valueOf(CommandLineParser.getInstance().getCommandLineArguments().get("-port")));

            GameplayScreen gameplayScreen = new GameplayScreen();
            gameplayScreen.setGameMode(GameMode.MULTIPLAYER);
            setScreen(gameplayScreen);
        } else if (gameplayStartMethod == GameplayStartMethod.MULTIPLAYER_JOIN) {
            NetworkManager.getInstance().joinGame(CommandLineParser.getInstance().getCommandLineArguments().get("-ip"),
                    Integer.valueOf(CommandLineParser.getInstance().getCommandLineArguments().get("-port")));

            GameplayScreen gameplayScreen = new GameplayScreen();
            gameplayScreen.setGameMode(GameMode.MULTIPLAYER);
            setScreen(gameplayScreen);
        }
    }

    public Player getPlayer() {
        return player;
    }

}
