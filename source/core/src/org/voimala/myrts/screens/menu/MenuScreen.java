package org.voimala.myrts.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.screens.AbstractGameScreen;
import org.voimala.myrts.screens.menu.windows.*;

public class MenuScreen extends AbstractGameScreen {

    private Stage stage;
    Stack uiStack;
    private Skin skin;
    private MainMenuWindow mainMenuWindow;
    private MultiplayerWindow multiplayerWindow;
    private MultiplayerLobbyWindow multiplayerLobbyWindow;
    private JoinByIPWindow joinByIPWindow;
    private ServerConnection serverConnection;

    public MenuScreen(GameMain gameMain) {
        super(gameMain);
    }

    private void initialize() {
        initializeSkin();
        initializeWindows();
    }

    private void initializeWindows() {
        stage.clear();
        initializeMainMenuWindow();
        initializeMultiplayerWindow();
        initializeMultiplayerLobbyWindow();
        initializeJoinByIpWindow();
        initializeConnectingToTheServerWindow();
    }

    private void initializeMainMenuWindow() {
        mainMenuWindow = new MainMenuWindow(skin, this);
        mainMenuWindow.setVisible(true);
        stage.addActor(mainMenuWindow);
    }

    private void initializeMultiplayerWindow() {
        multiplayerWindow = new MultiplayerWindow(skin, this);
        stage.addActor(multiplayerWindow);
    }

    private void initializeMultiplayerLobbyWindow() {
        multiplayerLobbyWindow = new MultiplayerLobbyWindow(skin, this);
        stage.addActor(multiplayerLobbyWindow);
    }

    private void initializeJoinByIpWindow() {
        joinByIPWindow = new JoinByIPWindow(skin, this);
        stage.addActor(joinByIPWindow);
    }

    private void initializeConnectingToTheServerWindow() {
        serverConnection = new ServerConnection(skin, this);
        stage.addActor(serverConnection);
    }

    private void initializeSkin() {
        skin = new Skin(
                Gdx.files.internal("layout/uiskin.json"),
                new TextureAtlas("layout/uiskin.atlas"));
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(deltaTime);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide () {
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void show () {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        initialize();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    public void showWindow(WindowName windowName) {
        if (windowName == WindowName.MAIN_MENU) {
            mainMenuWindow.setVisible(true);
        }
        else if (windowName == WindowName.MULTIPLAYER) {
            multiplayerWindow.setVisible(true);
        } else if (windowName == WindowName.JOIN_BY_IP) {
            joinByIPWindow.setVisible(true);
        } else if (windowName == WindowName.MULTIPLAYER_LOBBY) {
            multiplayerLobbyWindow.setVisible(true);
        } else if (windowName == WindowName.SERVER_CONNECTION) {
            serverConnection.setVisible(true);
        }
    }

    public void hideWindow(WindowName windowName) {
        if (windowName == WindowName.MAIN_MENU) {
            mainMenuWindow.setVisible(false);
        } else if (windowName == WindowName.MULTIPLAYER) {
            multiplayerWindow.setVisible(false);
        } else if (windowName == WindowName.JOIN_BY_IP) {
            joinByIPWindow.setVisible(false);
        } else if (windowName == WindowName.MULTIPLAYER_LOBBY) {
            multiplayerLobbyWindow.setVisible(false);
        } else if (windowName == WindowName.SERVER_CONNECTION) {
            serverConnection.setVisible(false);
        }
    }
}
