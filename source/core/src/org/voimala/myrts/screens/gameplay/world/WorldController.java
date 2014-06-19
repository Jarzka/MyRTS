package org.voimala.myrts.screens.gameplay.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.screens.gameplay.GameplayScreen;
import org.voimala.myrts.screens.gameplay.units.AbstractUnit;
import org.voimala.myrts.screens.gameplay.units.UnitContainer;
import org.voimala.myrts.screens.gameplay.units.infantry.M4Unit;

public class WorldController {

    private static final String TAG = WorldController.class.getName();

    private UnitContainer unitContainer = new UnitContainer();

    private GameplayScreen gameplayScreen;
    private OrthographicCamera worldCamera;

    private double hudSize = 1; // TODO Hud needs to be implemented

    public final int TILE_SIZE_PIXELS = 256;

    private long currentSimTick = 1; // "Communication turn" in multiplayer game. // TODO Move to NetworkManager?
    private long SimTickDurationMs = 100;

    public WorldController() {
        initialize();
    }

    private void initialize() {
        initializeCamera();
        initializeMap();
    }

    private void initializeCamera() {
        worldCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        worldCamera.lookAt(0, 0, 0);
        worldCamera.translate(800, 800);
        worldCamera.zoom = 4;
        worldCamera.update();
    }

    private void initializeMap() {
        // TODO For now we just create a simple test map.
        // The final implementation should load the map from hard disk.
        createTestUnit();
    }

    private void createTestUnit() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                M4Unit unit = new M4Unit(Long.valueOf(String.valueOf(i) + String.valueOf(j)));
                unit.setPosition(new Vector2(500 + TILE_SIZE_PIXELS * i, 500 + TILE_SIZE_PIXELS  * j));
                unit.setTeam(1);
                unit.setPlayerNumber(1);
                unit.setAngle(0);
                unitContainer.addUnit(unit);
            }
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                M4Unit unit = new M4Unit(Long.valueOf(String.valueOf(i * 100) + String.valueOf(j * 100)));
                unit.setPosition(new Vector2(4000 + TILE_SIZE_PIXELS * i, 4000 + TILE_SIZE_PIXELS  * j));
                unit.setPlayerNumber(2);
                unit.setTeam(2);
                unit.setAngle(180);
                unitContainer.addUnit(unit);
            }
        }
    }

    public UnitContainer getUnitContainer() {
        return unitContainer;
    }

    public void updateWorld(final float deltaTime) {
        updateUnits(deltaTime);
    }

    private void updateUnits(float deltaTime) {
        for (AbstractUnit unit : unitContainer.getUnits()) {
            unit.update(deltaTime);
        }
    }

    public OrthographicCamera getWorldCamera() {
        return worldCamera;
    }

    public GameplayScreen getGameplayScreen() {
        return gameplayScreen;
    }

    public void setGameplayScreen(final GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
    }
}
