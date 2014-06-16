package org.voimala.myrts.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.graphics.SpriteContainer;
import org.voimala.myrts.networking.ConnectionState;
import org.voimala.myrts.networking.NetworkManager;
import org.voimala.myrts.networking.RTSProtocolManager;
import org.voimala.myrts.screens.AbstractGameScreen;
import org.voimala.myrts.screens.gameplay.input.GameplayInputManager;
import org.voimala.myrts.screens.gameplay.input.GameplayInputProcessor;
import org.voimala.myrts.screens.gameplay.states.AbstractGameplayState;
import org.voimala.myrts.screens.gameplay.states.GameplayStateRunning;
import org.voimala.myrts.screens.gameplay.world.GameMode;
import org.voimala.myrts.screens.gameplay.world.RenderMode;
import org.voimala.myrts.screens.gameplay.world.WorldController;
import org.voimala.myrts.screens.gameplay.world.WorldRenderer;

public class GameplayScreen extends AbstractGameScreen {

    private WorldController worldController;
    private WorldRenderer worldRenderer;

    private AbstractGameplayState currentState = new GameplayStateRunning(this);

    private GameMode gameMode = GameMode.SINGLEPLAYER;
    private long lastWorldUpdateTimestamp = 0;
    private long worldUpdateTick = 0;
    private long renderTick = 0;
    /** SimTick is used for network communication.
     * 1 simTick = 5 world update ticks by default.
     * When a new SimTick is reached, the game executes other player's input information.
     * If such information is not available, wait for it.
     */
    private long simTick = 0;
    private boolean isWaitingInputForNextSimTick = false;
    private boolean isNoInputSentForTheNextTurn = false;

    private GameplayInputProcessor inputHandler = new GameplayInputProcessor(this);
    private GameplayInputManager gameplayInputManager;

    /** @param worldController Preloaded WorldController object.
     */
    public GameplayScreen(final WorldController worldController) {
        this.worldController = worldController;
        RTSProtocolManager.getInstance().setWorldController(worldController);
        initializeWorldRenderer();
        initializeGameMode();
        initializeInputListeners();
    }

    private void initializeWorldRenderer() {
        worldRenderer = new WorldRenderer();
        worldRenderer.setWorldController(worldController);
    }

    private void initializeGameMode() {
        if (NetworkManager.getInstance().getClientConnectionState() == ConnectionState.CONNECTED) {
            setGameMode(GameMode.MULTIPLAYER);
            simTick = 1;
        } else {
            GameMain.getInstance().getPlayer().setNumber(1);
            GameMain.getInstance().getPlayer().setTeam(1);
            setGameMode(GameMode.SINGLEPLAYER);
        }
    }

    private void initializeInputListeners() {
        gameplayInputManager = new GameplayInputManager(this);
        Gdx.input.setInputProcessor(inputHandler);
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public void render(float deltaTime) {
        deltaTime = fixDeltaTimeMinAndMaxValues(deltaTime);
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
    }

    public void updateWorld(float deltaTime) {
        if (gameMode == GameMode.SINGLEPLAYER) {
            updateWorldUsingVariablePhysics(deltaTime);
        } else if (gameMode == GameMode.MULTIPLAYER) {
            if (!isWaitingInputForNextSimTick) {
                updateWorldUsingFixedPhysics();
            }

            // New SimTick reached.
            if (worldUpdateTick % 5.0 == 0 && gameMode == GameMode.MULTIPLAYER) {
                handleNewSimTick(); // TODO Move to Network Manager or create a new SimTickCommunication class?
            }
        }
    }

    private void handleNewSimTick() {
        isWaitingInputForNextSimTick = true;

        if (checkInputInfoForNextSimTick()) {
            // TODO If no input in this SimTick, send "no input"
            checkAndSendNoInputIfNeeded();
            isWaitingInputForNextSimTick = false;
            isNoInputSentForTheNextTurn = false;
            simTick++;
        }
    }

    /** @return True if input ok for the next SimTick. */
    private boolean checkInputInfoForNextSimTick() {
        // We never wait input for SimTick 2.
        if (simTick == 1) {
            return true;
        }

        /* TODO Check that we have input information for the next SimTick so that we can
        continue executing the simulation. */

        return false;
    }

    private void checkAndSendNoInputIfNeeded() {
        if (!isNoInputSentForTheNextTurn) {
            NetworkManager.getInstance().getClientThread().sendMessage(
                    RTSProtocolManager.getInstance().createNetworkMessageInputNoInput(simTick));
            );
            isNoInputSentForTheNextTurn = true;
        }
    }

    private void updateWorldUsingVariablePhysics(final float deltaTime) {
        worldController.updateWorld(deltaTime);
        worldUpdateTick++;
        lastWorldUpdateTimestamp = System.currentTimeMillis();
    }

    private void updateWorldUsingFixedPhysics() {
        // Update game world when 1 / fixedPhysicsFps seconds have passed. Use a constant delta time.
        long fixedPhysicsFps = 20;
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

    public long getSimTick() {
        return simTick;
    }

    public boolean isWaitingInputForNextSimTick() {
        return isWaitingInputForNextSimTick;
    }
}
