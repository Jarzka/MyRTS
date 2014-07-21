package org.voimala.myrtsengine.screens.gameplay.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.screens.gameplay.GameplayScreen;
import org.voimala.myrtsengine.screens.gameplay.units.AbstractUnit;
import org.voimala.myrtsengine.screens.gameplay.units.UnitContainer;
import org.voimala.myrtsgame.screens.gameplay.units.infantry.M4Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorldController {

    private static final String TAG = WorldController.class.getName();

    /** Integer = Player number */
    private HashMap<Integer, UnitContainer> unitContainers = new HashMap<Integer, UnitContainer>();

    private GameplayScreen gameplayScreen;
    private OrthographicCamera worldCamera;

    private double hudSize = 1; // TODO Hud needs to be implemented

    public final int TILE_SIZE_PIXELS = 256;

    private long currentSimTick = 1; // "Communication turn" in multiplayer game. // TODO Move to NetworkManager?
    private long SimTickDurationMs = 100;
    private AbstractUnit[] allUnits;

    public WorldController() {
        initialize();
    }

    private void initialize() {
        initializeContainers();
        initializeCamera();
        initializeMap();
    }

    private void initializeContainers() {
        for (int i = 1; i <= 8; i++) {
            unitContainers.put(i, new UnitContainer());
        }
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
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                M4Unit unit = new M4Unit(AbstractUnit.getNextFreeId());
                unit.setPosition(new Vector2(500 + TILE_SIZE_PIXELS * i, 500 + TILE_SIZE_PIXELS  * j));
                unit.setTeam(1);
                unit.setPlayerNumber(1);
                unit.setAngle(0);
                unitContainers.get(1).addUnit(unit);
            }
        }

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                M4Unit unit = new M4Unit(AbstractUnit.getNextFreeId());
                unit.setPosition(new Vector2(4000 + TILE_SIZE_PIXELS * i, 4000 + TILE_SIZE_PIXELS  * j));
                unit.setPlayerNumber(2);
                unit.setTeam(2);
                unit.setAngle(180);
                unitContainers.get(2).addUnit(unit);
            }
        }
    }

    public UnitContainer getUnitContainer(final int playerNumber) {
        return unitContainers.get(playerNumber);
    }

    public void updateWorld(final float deltaTime) {
        updateUnits(deltaTime);
    }

    private void updateUnits(float deltaTime) {
        for (int i = 1; i <= 8; i++) {
            for (AbstractUnit unit : unitContainers.get(i).getUnits()) {
                unit.update(deltaTime);
            }
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

    public List<AbstractUnit> getAllUnits() {
        ArrayList<AbstractUnit> units= new ArrayList<AbstractUnit>();
        
        for (int i = 1; i <= 8; i++) {
            units.addAll(this.unitContainers.get(i).getUnits());
        }

        return units;
    }

}
