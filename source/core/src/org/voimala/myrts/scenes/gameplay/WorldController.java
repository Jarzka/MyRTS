package org.voimala.myrts.scenes.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.app.MyRTS;
import org.voimala.myrts.multiplayer.ServerThread;
import org.voimala.myrts.scenes.gameplay.units.Unit;
import org.voimala.myrts.scenes.gameplay.units.UnitContainer;
import org.voimala.myrts.scenes.gameplay.units.infantry.M4Unit;
import org.voimala.myrts.graphics.SpriteContainer;
import org.voimala.myrts.input.InputManager;
import org.voimala.myrts.input.RTSInputProcessor;
import org.voimala.myrts.multiplayer.ClientThread;

public class WorldController {

    private static final String TAG = WorldController.class.getName();

    private UnitContainer unitContainer = new UnitContainer();

    private RTSInputProcessor inputHandler = new RTSInputProcessor(this);
    private InputManager inputManager;

    private MyRTS myRTS;

    private OrthographicCamera worldCamera;

    private double hudSize = 1; // TODO Hud size needs to be implemented

    public final int TILE_SIZE_PIXELS = 256;

    public WorldController(final MyRTS myRTS) {
        this.myRTS = myRTS;
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

        inputManager = new InputManager(this);
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

    public void update(float deltaTime) {
        inputManager.update();
        updateWorld(deltaTime);
    }

    private void updateWorld(final float deltaTime) {
        updateUnits(deltaTime);

    }

    private void updateUnits(float deltaTime) {
        for (Unit unit : unitContainer.getUnits()) {
            unit.update(deltaTime);
        }
    }

    public OrthographicCamera getWorldCamera() {
        return worldCamera;
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    // TODO Move to another class?
    public void moveUnit(final Unit unit, final int x, final int y) {
        unit.getMovement().setPathPoint(new Vector2(x, y));
    }

    /** Returns null if unit is not found. */
    public Unit findUnitById(final String id) {
        for (Unit unit : unitContainer.getUnits()) {
            if (unit.getUnitId().equals(id)) {
                return unit;
            }
        }

        return null;
    }

    public MyRTS getMyRTS() {
        return myRTS;
    }
}
