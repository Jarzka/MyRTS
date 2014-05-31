package org.voimala.myrts.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.app.GameplayStartMethod;
import org.voimala.myrts.screens.AbstractGameScreen;
import org.voimala.myrts.screens.menu.windows.MainMenuWindow;
import org.voimala.myrts.screens.menu.windows.MultiplayerWindow;
import org.voimala.myrts.screens.menu.windows.WindowName;

public class MenuScreen extends AbstractGameScreen {

    private Stage stage;
    Stack uiStack;
    private Skin skin;
    private MainMenuWindow mainMenuWindow;
    private MultiplayerWindow multiplayerWindow;

    public MenuScreen(GameMain gameMain) {
        super(gameMain);
    }

    private void initialize() {
        initializeSkin();
        initializeWindows();
    }

    private void initializeWindows() {
        stage.clear();
        //uiStack = new Stack();
        //stage.addActor(uiStack);
        //stage.setViewport(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        initializeMainMenuWindow();
        initializeMultiplayerWindow();
    }

    private void initializeMainMenuWindow() {
        mainMenuWindow = new MainMenuWindow("Main Menu", skin, this);
        mainMenuWindow.setVisible(true);
        stage.addActor(mainMenuWindow);
    }

    private void initializeMultiplayerWindow() {
        multiplayerWindow = new MultiplayerWindow("Multiplayer", skin, this);
        stage.addActor(multiplayerWindow);
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
        
        handleInput();

        stage.act(deltaTime);
        stage.draw();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            gameMain.startGame(GameplayStartMethod.SINGLEPLAYER);
        }
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
        stage = new Stage();
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
        if (windowName == WindowName.MULTIPLAYER) {
            multiplayerWindow.setVisible(true);
        }
    }

    public void hideWindow(WindowName windowName) {
        if (windowName == WindowName.MAIN_MENU) {
            mainMenuWindow.setVisible(false);
        }
    }
}
