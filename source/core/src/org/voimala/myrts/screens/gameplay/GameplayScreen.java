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
import org.voimala.myrts.screens.gameplay.input.commands.RTSCommandExecuter;
import org.voimala.myrts.screens.gameplay.multiplayer.GameplayChatInputManager;
import org.voimala.myrts.screens.gameplay.multiplayer.MultiplayerSynchronizationManager;
import org.voimala.myrts.screens.gameplay.states.AbstractGameplayState;
import org.voimala.myrts.screens.gameplay.states.GameplayStateRunning;
import org.voimala.myrts.screens.gameplay.units.AbstractUnit;
import org.voimala.myrts.screens.gameplay.world.GameMode;
import org.voimala.myrts.screens.gameplay.world.RenderMode;
import org.voimala.myrts.screens.gameplay.world.WorldController;
import org.voimala.myrts.screens.gameplay.world.WorldRenderer;

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

            // New SimTick reached.
            // TODO Variable turn length?
            if (worldUpdateTick % 5.0 == 0 && gameMode == GameMode.MULTIPLAYER) {
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
            worldRenderer.render(RenderMode.GAME_STATE); // TODO TESTING
            //worldRenderer.render(RenderMode.GAME_STATE_WITH_PHYSICS_PREDICTION);
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

    public String getGameStateHash() {
        String hash = "";

        // TODO Make sure that every client has a same container (units are in the same order etc.)

        StringBuilder hashBuilder = new StringBuilder();
        hashBuilder.append("simtick " + MultiplayerSynchronizationManager.getInstance().getSimTick() + ". ");
        for (AbstractUnit unit : worldController.getUnitContainer().getUnits()) {
            hashBuilder.append("unit" + unit.getObjectId() + "x:" + unit.getX() + ". ");
            hashBuilder.append("unit" + unit.getObjectId() + "y:" +unit.getY() + ". ");
            hashBuilder.append("unit" + unit.getObjectId() + "angle:" + unit.getAngleInRadians() + ". ");

        }

        hash += hashBuilder.toString();
        return hash;

        /* Final hash for production version
        for (AbstractUnit unit : worldController.getUnitContainer().getUnits()) {
            StringBuilder hashBuilder = new StringBuilder();
            hashBuilder.append(unit.getX());
            hashBuilder.append(unit.getY());
            hashBuilder.append(unit.getAngleInRadians());
            hash += hashBuilder.toString();
        }

        return md5(hash);
        */
    }

    // TODO http://javarevisited.blogspot.fi/2013/03/generate-md5-hash-in-java-string-byte-array-example-tutorial.html
    public static String md5(String message){
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes("UTF-8"));

            //converting byte array to Hexadecimal String
            StringBuilder sb = new StringBuilder(2*hash.length);
            for(byte b : hash){
                sb.append(String.format("%02x", b&0xff));
            }

            digest = sb.toString();

        } catch (UnsupportedEncodingException e) {
            Gdx.app.debug(TAG, e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Gdx.app.debug(TAG, e.getMessage());
        }

        return digest;
    }

}
