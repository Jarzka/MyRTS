package org.voimala.myrts.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
    private OrthographicCamera camera;

    public final int TILE_SIZE_PIXELS = 256;

    public WorldController() {
        initialize();
    }

    private void initialize() {
        initializeCamera();
        initializeInputProcessor();
        initializeSprites();
        initializeMap();
    }

    private void initializeCamera() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.lookAt(0, 0, 0);
        camera.translate(800, 800);
        camera.zoom = 4;
        camera.update();
    }


    private void initializeSprites() {
        initializeGroundSprites();
        initializeUnitSprites();
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
        unit.setAngle(0);
        CarMovement unitMovement = (CarMovement) unit.getMovement();
        unitMovement.addPathPoint(new Vector2(TILE_SIZE_PIXELS / 2 * 8, TILE_SIZE_PIXELS / 2 * 8));
        unitMovement.addPathPoint(new Vector2(TILE_SIZE_PIXELS / 2 * 14, TILE_SIZE_PIXELS / 2 * 8));
        unitMovement.addPathPoint(new Vector2(TILE_SIZE_PIXELS / 2 * 13, TILE_SIZE_PIXELS / 2 * 8));
        unitMovement.addPathPoint(new Vector2(TILE_SIZE_PIXELS / 2 * 11, TILE_SIZE_PIXELS / 2 * 8));
        unitMovement.addPathPoint(new Vector2(TILE_SIZE_PIXELS / 2 * 10, TILE_SIZE_PIXELS / 2 * 5));
        unitMovement.addPathPoint(new Vector2(0, 0));
        unitContainer.addUnit(unit);
    }

    public UnitContainer getUnitContainer() {
        return unitContainer;
    }

    public void update(float deltaTime) {
        updateUnits(deltaTime);
    }

    private void updateUnits(float deltaTime) {
        for (Unit unit : unitContainer.getUnits()) {
            unit.update(deltaTime);
        }
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
