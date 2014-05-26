package org.voimala.myrts.screens.gameplay.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.screens.gameplay.GameplayScreen;
import org.voimala.myrts.screens.gameplay.input.GameplayInputManager;
import org.voimala.myrts.screens.gameplay.input.GameplayInputProcessor;
import org.voimala.myrts.screens.gameplay.units.Unit;
import org.voimala.myrts.screens.gameplay.units.UnitContainer;
import org.voimala.myrts.screens.gameplay.units.infantry.M4Unit;
import org.voimala.myrts.graphics.SpriteContainer;

public class WorldController {

    private static final String TAG = WorldController.class.getName();

    private UnitContainer unitContainer = new UnitContainer();

    private GameplayInputProcessor inputHandler = new GameplayInputProcessor(this);
    private GameplayInputManager gameplayInputManager;

    private GameplayScreen gameplayScreen;

    private OrthographicCamera worldCamera;

    private double hudSize = 1; // TODO Hud size needs to be implemented

    public final int TILE_SIZE_PIXELS = 256;

    private long currentSimTick = 1; // "Communication turn" in multiplayer game.
    private long SimTickDurationMs = 100;

    public WorldController(final GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
        initialize();
    }

    private void initialize() {
        initializePointer();
        initializeCamera();
        initializeInputProcessor();
        initializeSprites();
        initializeMap();
    }

    private void initializePointer() {
        Pixmap pixelmap = new Pixmap(Gdx.files.internal("graphics/pointers/pointer-basic-0.png"));
        Gdx.input.setCursorImage(pixelmap, 0, 0);
        pixelmap.dispose();
    }

    private void initializeCamera() {
        worldCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        worldCamera.lookAt(0, 0, 0);
        worldCamera.translate(800, 800);
        worldCamera.zoom = 4;
        worldCamera.update();

        gameplayInputManager = new GameplayInputManager(this);
    }

    private void initializeSprites() {
        initializeCursorSprites();
        initializeGroundSprites();
        initializeUnitSprites();
    }

    private void initializeCursorSprites() {
        Texture texture = new Texture("graphics/pointers/pointer-basic-0.png");
        Sprite sprite = new Sprite(texture);
        SpriteContainer.getInstance().addSprite("pointer-basic-0", sprite);
    }

    private void initializeInputProcessor() {
        Gdx.input.setInputProcessor(inputHandler);
    }

    private void initializeGroundSprites() {
        Texture texture = new Texture("graphics/textures/ground/grass1.jpg");
        Sprite sprite = new Sprite(texture);
        SpriteContainer.getInstance().addSprite("grass1", sprite);
    }

    private void initializeUnitSprites() {
        Texture texture = new Texture("graphics/units/m4/m4-stopped-0.png");
        Sprite sprite = new Sprite(texture);
        SpriteContainer.getInstance().addSprite("m4-stopped-0", sprite);
    }

    private void initializeMap() {
        // TODO For now we just create a simple test map.
        // The final implementation should load the map from hard disk.
        createTestUnit();
    }

    private void createTestUnit() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                M4Unit unit = new M4Unit(String.valueOf(i) + String.valueOf(j));
                unit.setPosition(TILE_SIZE_PIXELS * i, TILE_SIZE_PIXELS  * j);
                unit.setTeam(i % 2 + 1);
                unit.setAngle(0);
                unitContainer.addUnit(unit);
            }
        }
    }

    public UnitContainer getUnitContainer() {
        return unitContainer;
    }

    /* TODO Uncomment when updateInputManager has been moved to scene class
    public void update(float deltaTime) {
        updateWorld(deltaTime);
    }*/

    public void updateWorld(final float deltaTime) {
        updateUnits(deltaTime);
    }

    // TODO Move to scene class?
    public void updateInputManager(final float deltaTime) {
        gameplayInputManager.update();
    }

    private void updateUnits(float deltaTime) {
        for (Unit unit : unitContainer.getUnits()) {
            unit.update(deltaTime);
        }
    }

    public OrthographicCamera getWorldCamera() {
        return worldCamera;
    }

    public GameplayInputManager getGameplayInputManager() {
        return gameplayInputManager;
    }

    /** Returns null if unit is not found. */
    public Unit findUnitById(final String id) {
        for (Unit unit : unitContainer.getUnits()) {
            if (unit.getObjectId().equals(id)) {
                return unit;
            }
        }

        return null;
    }

    public GameMain getGameMain() {
        return GameMain;
    }
}
