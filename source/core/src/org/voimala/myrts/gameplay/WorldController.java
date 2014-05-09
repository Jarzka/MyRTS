package org.voimala.myrts.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.voimala.myrts.gameplay.units.Unit;
import org.voimala.myrts.gameplay.units.UnitContainer;
import org.voimala.myrts.gameplay.units.infantry.M4Unit;
import org.voimala.myrts.graphics.SpriteContainer;
import org.voimala.myrts.input.RTSInputProcessor;

public class WorldController {
    private static final String TAG = WorldController.class.getName();
    private UnitContainer unitContainer = new UnitContainer();
    private RTSInputProcessor inputHandler = new RTSInputProcessor(this);
    private WorldRenderer worldRenderer = null;

    public WorldController() {
        initialize();
    }

    public void setWorldRenderer(WorldRenderer worldRenderer) {
        this.worldRenderer = worldRenderer;
    }

    public WorldRenderer getWorldRenderer() {
        return worldRenderer;
    }

    private void initialize() {
        initializeInputProcessor();
        initializeSprites();
        initializeMap();
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

        M4Unit unit1 = new M4Unit();
        unit1.setPosition(0, 0);
        unit1.setAngle(0);
        unitContainer.addUnit(unit1);

        M4Unit unit2 = new M4Unit();
        unit2.setPosition(400, 400);
        unit2.setAngle(90);
        unitContainer.addUnit(unit2);
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
}
