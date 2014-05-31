package org.voimala.myrts.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.app.GameplayStartMethod;
import org.voimala.myrts.screens.AbstractGameScreen;
import org.voimala.myrts.screens.menu.windows.MainMenuWindow;

public class MenuScreen extends AbstractGameScreen {

    private Stage stage;
    Stack uiStack;
    private Skin skin;
    private MainMenuWindow mainMenuWindow;

    public MenuScreen(GameMain gameMain) {
        super(gameMain);
    }

    private void initialize() {
        initializeSkin();
        initializeWindows();
    }

    private void initializeWindows() {
        stage.clear();
        uiStack = new Stack();
        stage.addActor(uiStack);
        uiStack.setSize(Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());
        initializeMainMenuWindow();
    }

    private void initializeMainMenuWindow() {
        mainMenuWindow = new MainMenuWindow("Main Menu", skin);
        mainMenuWindow.initialize();
        mainMenuWindow.setVisible(true);
        uiStack.add(mainMenuWindow);
    }

    private void initializeSkin() {
        skin = new Skin(
                Gdx.files.internal("layout/uiskin.json"),
                new TextureAtlas("layout/uiskin.atlas"));
    }

    @Override
    public void render(float deltaTime) {
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
        // TODO
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
}
