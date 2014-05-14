package org.voimala.myrts.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.gameplay.units.Unit;
import org.voimala.myrts.gameplay.units.UnitContainer;
import org.voimala.myrts.gameplay.units.infantry.M4Unit;
import org.voimala.myrts.gameplay.units.movements.CarMovement;
import org.voimala.myrts.graphics.SpriteContainer;
import org.voimala.myrts.input.RTSInputProcessor;

public class WorldController {
    private static final String TAG = WorldController.class.getName();
    private UnitContainer unitContainer = new UnitContainer();
    private RTSInputProcessor inputHandler = new RTSInputProcessor(this);
    private OrthographicCamera worldCamera;
    private CameraManagement cameraManagement;
    private double hudSize = 1; // TODO Hud size needs to be implemented

    public final int TILE_SIZE_PIXELS = 256;

    public WorldController() {
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

        cameraManagement = new CameraManagement(worldCamera);
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
        M4Unit unit = new M4Unit();
        unit.setPosition(TILE_SIZE_PIXELS / 2, TILE_SIZE_PIXELS / 2);
        unit.setTeam(1);
        unit.setAngle(0);
        CarMovement unitMovement = (CarMovement) unit.getMovement();
        unitMovement.addPathPoint(new Vector2(TILE_SIZE_PIXELS / 2 * 8, TILE_SIZE_PIXELS / 2 * 8));
        unitMovement.addPathPoint(new Vector2(TILE_SIZE_PIXELS / 2 * 14, TILE_SIZE_PIXELS / 2 * 8));
        unitMovement.addPathPoint(new Vector2(TILE_SIZE_PIXELS / 2 * 14, TILE_SIZE_PIXELS / 2 * 1));
        unitMovement.addPathPoint(new Vector2(TILE_SIZE_PIXELS / 2 * 14, TILE_SIZE_PIXELS / 2 * 10));
        unitMovement.addPathPoint(new Vector2(TILE_SIZE_PIXELS / 2 * 1, TILE_SIZE_PIXELS / 2 * 1));
        unitMovement.addPathPoint(new Vector2(TILE_SIZE_PIXELS / 2 * 2, TILE_SIZE_PIXELS / 2 * 2));
        unitMovement.addPathPoint(new Vector2(0, 0));
        unitContainer.addUnit(unit);

        M4Unit unit2 = new M4Unit();
        unit2.setPosition(TILE_SIZE_PIXELS / 2 * 9, TILE_SIZE_PIXELS / 2 * 10);
        unit2.setTeam(2);
        unitContainer.addUnit(unit2);
    }

    public UnitContainer getUnitContainer() {
        return unitContainer;
    }

    public void update(float deltaTime) {
        handleUserInput();
        updateWorld(deltaTime);
    }

    private void handleUserInput() {
        handleCameraManagement();
        handleUnitSelection();
    }

    private void handleCameraManagement() {
        cameraManagement.update();
    }

    private void handleUnitSelection() {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            // TODO
        }
    }

    private void updateWorld(float deltaTime) {
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
}
