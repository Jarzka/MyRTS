package org.voimala.myrtsengine.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import org.voimala.myrtsengine.app.GameMain;
import org.voimala.myrtsengine.graphics.SpriteContainer;
import org.voimala.myrtsengine.networking.ConnectionState;
import org.voimala.myrtsengine.networking.NetworkManager;
import org.voimala.myrtsengine.networking.RTSProtocolManager;
import org.voimala.myrtsengine.screens.AbstractGameScreen;
import org.voimala.myrtsengine.screens.gameplay.input.GameplayInputManager;
import org.voimala.myrtsengine.screens.gameplay.input.GameplayInputProcessor;
import org.voimala.myrtsengine.screens.gameplay.input.commands.RTSCommandExecuter;
import org.voimala.myrtsengine.screens.gameplay.multiplayer.GameplayChatInputManager;
import org.voimala.myrtsengine.screens.gameplay.multiplayer.MultiplayerSynchronizationManager;
import org.voimala.myrtsengine.screens.gameplay.states.AbstractGameplayState;
import org.voimala.myrtsengine.screens.gameplay.states.GameplayStateRunning;
import org.voimala.myrtsengine.screens.gameplay.units.AbstractUnit;
import org.voimala.myrtsengine.screens.gameplay.world.GameMode;
import org.voimala.myrtsengine.screens.gameplay.world.RenderMode;
import org.voimala.myrtsengine.screens.gameplay.world.WorldController;
import org.voimala.myrtsengine.screens.gameplay.world.WorldRenderer;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GameplayScreen extends AbstractGameScreen {

    private static final String TAG = GameplayScreen.class.getName();

    private WorldController worldController;
    private WorldRenderer worldRenderer;

    private AbstractGameplayState currentState = new GameplayStateRunning(this);

    private GameplayInputManager gameplayInputManager;
    private GameplayInputProcessor gameplayInputProcessor;
    private GameplayChatInputManager gameplayChatInputManager;
    private RTSCommandExecuter rtsCommandExecuter;

    private GameMode gameMode = GameMode.SINGLEPLAYER;
    private long lastWorldUpdateTimestamp = 0;
    private long worldUpdateTick = 0;
    private long renderTick = 0;

    /** @param worldController Preloaded WorldController object.
     */
    public GameplayScreen(final WorldController worldController) {
        this.worldController = worldController;
        RTSProtocolManager.getInstance().setWorldController(worldController);
        initializeWorldRenderer();
        initializeInputManagers();
        initializeGameMode();
    }

    private void initializeWorldRenderer() {
        worldRenderer = new WorldRenderer();
        worldRenderer.setWorldController(worldController);
    }

    private void initializeGameMode() {
        if (NetworkManager.getInstance().getClientConnectionState() == ConnectionState.CONNECTED) {
            setGameMode(GameMode.MULTIPLAYER);
            MultiplayerSynchronizationManager.getInstance().setSimTick(1);
        } else {
            GameMain.getInstance().getPlayer().setNumber(1);
            GameMain.getInstance().getPlayer().setTeam(1);
            setGameMode(GameMode.SINGLEPLAYER);
        }
    }

    private void initializeInputManagers() {
        gameplayInputManager = new GameplayInputManager(this);
        gameplayInputProcessor = new GameplayInputProcessor(this);
        MultiplayerSynchronizationManager.getInstance().setGameplayScreen(this);
        gameplayChatInputManager = new GameplayChatInputManager(this);
        Gdx.input.setInputProcessor(gameplayInputProcessor);

        rtsCommandExecuter = new RTSCommandExecuter(this);
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public void render(float deltaTime) {
        currentState.update(deltaTime);
    }

    private float fixDeltaTimeMinAndMaxValues(float deltaTime) {
        if (deltaTime > 0.06) {
            deltaTime = (float) 0.06;
        }

        return deltaTime;
    }

    public void handleUserInput(float deltaTime) {
        // TODO What to do when the game is waiting input from the network?
        gameplayInputManager.update();
        gameplayChatInputManager.update();
    }

    public void updateWorld(float deltaTime) {
        if (gameMode == GameMode.SINGLEPLAYER) {
            deltaTime = fixDeltaTimeMinAndMaxValues(deltaTime);
            updateWorldUsingVariablePhysics(deltaTime);
        } else if (gameMode == GameMode.MULTIPLAYER) {
            if (!MultiplayerSynchronizationManager.getInstance().isWaitingInputForNextSimTick()) {
                updateWorldUsingFixedPhysics();
            }

            // Update SimTick between 5 world updates.
            // TODO Variable turn length?
            if (Float.valueOf(MultiplayerSynchronizationManager.getInstance().getSimTick()) == worldUpdateTick / 5f) {
                MultiplayerSynchronizationManager.getInstance().handleNewSimTick();
            }
        }
    }

    private void updateWorldUsingVariablePhysics(final float deltaTime) {
        worldController.updateWorld(deltaTime);
        worldUpdateTick++;
        lastWorldUpdateTimestamp = System.currentTimeMillis();
    }

    private void updateWorldUsingFixedPhysics() {
        // Update game world when 1 / fixedPhysicsFps seconds have passed. Use a constant delta time.
        final long fixedPhysicsFps = 20;
        if (System.currentTimeMillis() >= lastWorldUpdateTimestamp + (long) (((float) 1 / (float) fixedPhysicsFps) * 1000)) {
            float deltaTime = (float) 1 / (float) fixedPhysicsFps;
            worldController.updateWorld(deltaTime);
            worldUpdateTick++;
            lastWorldUpdateTimestamp = System.currentTimeMillis();
        }
    }

    public void renderWorld() {
        renderTick++;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameMode == GameMode.SINGLEPLAYER) {
            worldRenderer.render(RenderMode.GAME_STATE);
        } else if (gameMode == GameMode.MULTIPLAYER) {
            worldRenderer.render(RenderMode.GAME_STATE_WITH_PHYSICS_PREDICTION);
        }
    }

    @Override
    public void resize(int width, int height) {
        if (worldRenderer != null) {
            worldRenderer.resize(width, height);
        }
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
        // TODO Needs to be tested
        worldRenderer.dispose();
        SpriteContainer.getInstance().freeResources();
        NetworkManager.getInstance().disconnectAll();
    }

    public long getLastWorldUpdateTimestamp() {
        return lastWorldUpdateTimestamp;
    }

    public long getWorldUpdateTick() {
        return worldUpdateTick;
    }

    public long getRenderTick() {
        return renderTick;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setState(final AbstractGameplayState newState) {
        currentState = newState;
    }

    public WorldController getWorldController() {
        return worldController;
    }

    public GameplayInputManager getGameplayInputManager() {
        return gameplayInputManager;
    }

    public GameplayChatInputManager getGameplayChatInputManager() {
        return gameplayChatInputManager;
    }

    public RTSCommandExecuter getRTSCommandExecuter() {
        return rtsCommandExecuter;
    }

}
