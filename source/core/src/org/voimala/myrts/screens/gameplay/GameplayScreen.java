package org.voimala.myrts.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.networking.ConnectionState;
import org.voimala.myrts.networking.NetworkManager;
import org.voimala.myrts.networking.RTSProtocolManager;
import org.voimala.myrts.screens.AbstractGameScreen;
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

    /** @param worldController Preloaded WorldController object.
     */
    public GameplayScreen(final WorldController worldController) {
        this.worldController = worldController;
        RTSProtocolManager.getInstance().setWorldController(worldController);
        initializeWorldRenderer();
        initializeGameMode();
    }

    private void initializeWorldRenderer() {
        worldRenderer = new WorldRenderer();
        worldRenderer.setWorldController(worldController);
    }

    private void initializeGameMode() {
        if (NetworkManager.getInstance().getClientConnectionState() == ConnectionState.CONNECTED) {
            setGameMode(GameMode.MULTIPLAYER);
        } else {
            GameMain.getInstance().getPlayer().setNumber(1);
            GameMain.getInstance().getPlayer().setTeam(1);
            setGameMode(GameMode.SINGLEPLAYER);
        }
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public void render(float deltaTime) {
        currentState.update(deltaTime);
    }

    private float fixDeltaTimeMinAndMaxValues(float deltaTime) {
        deltaTime = fixDeltaTimeMinAndMaxValues(deltaTime);

        if (deltaTime > 0.06) {
            deltaTime = (float) 0.06;
        }

        return deltaTime;
    }

    public void updateInput(final float deltaTime) {
        worldController.updateInputManager(deltaTime);
    }

    public void updateWorld(float deltaTime) {
        if (gameMode == GameMode.SINGLEPLAYER) {
            updateWorldUsingVariablePhysics(deltaTime);
        } else if (gameMode == GameMode.MULTIPLAYER) {
            updateWorldUsingFixedPhysics();
        }
    }

    private void updateWorldUsingVariablePhysics(final float deltaTime) {
        worldController.updateWorld(deltaTime);
        worldUpdateTick++;
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
        worldRenderer.dispose();
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
}
