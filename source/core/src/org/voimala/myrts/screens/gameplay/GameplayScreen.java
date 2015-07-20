package org.voimala.myrts.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.graphics.SpriteContainer;
import org.voimala.myrts.networking.ConnectionState;
import org.voimala.myrts.networking.NetworkManager;
import org.voimala.myrts.networking.RTSProtocolManager;
import org.voimala.myrts.screens.AbstractGameScreen;
import org.voimala.myrts.screens.gameplay.input.LocalGameplayInputManager;
import org.voimala.myrts.screens.gameplay.input.LocalGameplayInputProcessor;
import org.voimala.myrts.screens.gameplay.input.LocalInputQueue;
import org.voimala.myrts.screens.gameplay.input.NetworkInputQueue;
import org.voimala.myrts.screens.gameplay.input.RTSCommandExecuter;
import org.voimala.myrts.screens.gameplay.multiplayer.GameplayChatInputManager;
import org.voimala.myrts.screens.gameplay.multiplayer.MultiplayerSynchronizationManager;
import org.voimala.myrts.screens.gameplay.states.AbstractGameplayState;
import org.voimala.myrts.screens.gameplay.states.GameplayStateRunning;
import org.voimala.myrts.screens.gameplay.world.GameMode;
import org.voimala.myrts.screens.gameplay.world.RenderMode;
import org.voimala.myrts.screens.gameplay.world.WorldController;
import org.voimala.myrts.screens.gameplay.world.WorldRenderer;

public class GameplayScreen extends AbstractGameScreen {

    private static final String TAG = GameplayScreen.class.getName();

    private WorldController worldController;
    private WorldRenderer worldRenderer;

    private AbstractGameplayState currentState = new GameplayStateRunning(this);

    private LocalGameplayInputManager localGameplayInputManager; // TODO Convert to singleton?
    private LocalGameplayInputProcessor localGameplayInputProcessor; // TODO Convert to singleton?
    private GameplayChatInputManager gameplayChatInputManager; // TODO Convert to singleton?
    private RTSCommandExecuter rtsCommandExecuter; // TODO Convert to singleton?

    private OrthographicCamera worldCamera;

    private GameMode gameMode = GameMode.SINGLEPLAYER;
    private long lastWorldUpdateTimestamp = 0;

    private long renderTick = 0;

    /** @param worldController Preloaded WorldController object.
     */
    public GameplayScreen(final WorldController worldController) {
        this.worldController = worldController;
        RTSProtocolManager.getInstance().setWorldController(worldController);
        initializeWorldRenderer();
        initializeCamera();
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


    private void initializeCamera() {
        worldCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        worldCamera.lookAt(0, 0, 0);
        worldCamera.translate(800, 800);
        worldCamera.zoom = 4;
        worldCamera.update();
    }

    private void initializeInputManagers() {
        localGameplayInputManager = new LocalGameplayInputManager(this);
        localGameplayInputProcessor = new LocalGameplayInputProcessor(this);
        MultiplayerSynchronizationManager.getInstance().setGameplayScreen(this);
        gameplayChatInputManager = new GameplayChatInputManager(this);
        Gdx.input.setInputProcessor(localGameplayInputProcessor);

        LocalInputQueue.getInstance().setGameplayScreen(this);
        NetworkInputQueue.getInstance().setGameplayScreen(this);

        rtsCommandExecuter = new RTSCommandExecuter(worldController);
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
        localGameplayInputManager.update();
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
            /* TODO Current value is hardcoded.
             * Implement variable turn length. If players are lagging, either increase simtick rate globally so that simtick is
             * increased for example every 10 world updates. Another solution is to decide that commands sent in turn x
             * will be executed for example in turn x + 3 globally. You should find out which method is simpler and more
             * reliable to implement. */
            if (Float.valueOf(MultiplayerSynchronizationManager.getInstance().getSimTick()) == worldController.getWorldUpdateTick() / 5f) {
                MultiplayerSynchronizationManager.getInstance().handleNewSimTick();
            }
        }
    }

    private void updateWorldUsingVariablePhysics(final float deltaTime) {
        worldController.updateWorld(deltaTime);
        lastWorldUpdateTimestamp = System.currentTimeMillis();
    }

    private void updateWorldUsingFixedPhysics() {
        // Update game world when 1 / fixedPhysicsFps seconds have passed. Use a constant delta time.
        final long fixedPhysicsFps = 30;
        if (System.currentTimeMillis() >= lastWorldUpdateTimestamp + (long) (((float) 1 / (float) fixedPhysicsFps) * 1000)) {
            float deltaTime = (float) 1 / (float) fixedPhysicsFps;
            worldController.updateWorld(deltaTime);
            lastWorldUpdateTimestamp = System.currentTimeMillis();
        }
    }

    public void renderWorld(final float deltaTime) {
        renderTick++;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameMode == GameMode.SINGLEPLAYER) {
            worldRenderer.render(RenderMode.WORLD_STATE, deltaTime);
        } else if (gameMode == GameMode.MULTIPLAYER) {
            worldRenderer.render(RenderMode.WORLD_STATE_WITH_PHYSICS_PREDICTION, deltaTime);
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

    public LocalGameplayInputManager getLocalGameplayInputManager() {
        return localGameplayInputManager;
    }

    public GameplayChatInputManager getGameplayChatInputManager() {
        return gameplayChatInputManager;
    }

    public RTSCommandExecuter getRTSCommandExecuter() {
        return rtsCommandExecuter;
    }

    public WorldRenderer getWorldRenderer() {
        return worldRenderer;
    }

    public OrthographicCamera getWorldCamera() {
        return worldCamera;
    }

}
