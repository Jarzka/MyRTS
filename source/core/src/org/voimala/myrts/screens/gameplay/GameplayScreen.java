package org.voimala.myrts.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.multiplayer.RTSProtocolManager;
import org.voimala.myrts.screens.GameScreen;
import org.voimala.myrts.screens.gameplay.states.GameplayState;
import org.voimala.myrts.screens.gameplay.states.GameplayStateInitialize;
import org.voimala.myrts.screens.gameplay.world.GameMode;
import org.voimala.myrts.screens.gameplay.world.RenderMode;
import org.voimala.myrts.screens.gameplay.world.WorldController;
import org.voimala.myrts.screens.gameplay.world.WorldRenderer;

public class GameplayScreen extends GameScreen {

    private WorldController worldController;
    private WorldRenderer worldRenderer;

    private GameplayState currentGameplayState = new GameplayStateInitialize(this);

    private GameMode gameMode = GameMode.SINGLEPLAYER;
    private long lastWorldUpdateTimestamp = 0;
    private long worldUpdateTick = 0;
    private long renderTick = 0;

    public GameplayScreen(GameMain gameMain) {
        super(gameMain);
        initialize();
    }

    private void initialize() {
        worldController = new WorldController(this);
        RTSProtocolManager.getInstance().setWorldController(worldController);
        worldRenderer = new WorldRenderer(worldController);
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public void render(float deltaTime) {
        // TODO update current state
        deltaTime = fixDeltaTimeMinAndMaxValues(deltaTime);

        updateInput(deltaTime);
        updateWorld(deltaTime);
        renderWorld();
    }

    private float fixDeltaTimeMinAndMaxValues(float deltaTime) {
        if (deltaTime > 0.06) {
            deltaTime = (float) 0.06;
        }

        return deltaTime;
    }

    private void updateInput(final float deltaTime) {
        worldController.updateInputManager(deltaTime);
    }

    private void updateWorld(float deltaTime) {
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
        // Update game world when 1 / fixedPhysicsFps seconds have passed.
        long fixedPhysicsFps = 20;
        if (System.currentTimeMillis() >= lastWorldUpdateTimestamp + (long) (((float) 1 / (float) fixedPhysicsFps) * 1000)) {
            float deltaTime = (float) 1 / (float) fixedPhysicsFps;
            worldController.updateWorld(deltaTime);
            worldUpdateTick++;

            lastWorldUpdateTimestamp = System.currentTimeMillis();
        }
    }

    private void renderWorld() {
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
        worldRenderer.resize(width, height);
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
}
