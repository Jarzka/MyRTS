package org.voimala.myrts.screens.gameplay.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import org.voimala.myrts.screens.gameplay.units.Unit;
import org.voimala.myrts.graphics.SpriteContainer;

public class WorldRenderer implements Disposable {

    private static final String TAG = WorldRenderer.class.getName();
    private SpriteBatch batch;
    private SpriteBatch hudBatch;
    private WorldController worldController;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private long lastRenderTimestamp = 0;

    private BitmapFont defaultFont;

    public WorldRenderer (WorldController worldController) {
        this. worldController = worldController;

        initialize();
    }

    private void initialize () {
        initializeFonts();
        initializeBatches();
    }

    private void initializeFonts() {
        defaultFont = new BitmapFont();
        defaultFont.setColor(Color.WHITE);
    }

    private void initializeBatches() {
        batch = new SpriteBatch();
        hudBatch = new SpriteBatch();
    }


    public void render(final RenderMode renderMode) {
        /* This game uses the standard mathematic circle where 0 degrees point to right,
         * 90 degrees point to up etc. Libgdx seems to use a circle where 0 degrees
         * point to up, 90 degrees point to left etc. WorldRenderer makes the conversion
         * automatically. */
        batch.setProjectionMatrix(worldController.getWorldCamera().combined);

        renderGround();
        renderUnits(renderMode);
        renderUnitEnergyBars(renderMode);
        renderHud();
        renderUnitSelectionRectangle();
        renderInfoText();

        lastRenderTimestamp = System.currentTimeMillis();
    }

    private void renderGround() {
        for (int i = 0; i < 25; i++) { // TODO Map size
            for (int j = 0; j < 25; j++) {
                batch.begin();
                Sprite sprite = SpriteContainer.getInstance().getSprite("grass1");
                sprite.setPosition(i * worldController.TILE_SIZE_PIXELS, j * worldController.TILE_SIZE_PIXELS);
                sprite.draw(batch);
                batch.end();
            }
        }
    }

    private void renderUnits(final RenderMode renderMode) {
        if (renderMode == RenderMode.GAME_STATE) {
            renderUnits();
        } else if (renderMode == RenderMode.GAME_STATE_WITH_PHYSICS_PREDICTION) {
            renderUnitsWithPhysicsPrediction(); // TODO Too much code duplication
        }
    }

    private void renderUnits() {
        for (Unit unit : worldController.getUnitContainer().getUnits()) {
            batch.begin();
            Sprite sprite = SpriteContainer.getInstance().getSprite("m4-stopped-0");
            sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2 - 70);
            sprite.setPosition(unit.getX() - sprite.getWidth() / 2, unit.getY() - sprite.getWidth() / 2 + 70);
            sprite.setRotation(unit.getAngle() - 90);
            sprite.draw(batch);
            batch.end();
        }
    }

    private void renderUnitsWithPhysicsPrediction() {
        for (Unit unit : worldController.getUnitContainer().getUnits()) {
            try {
                Unit unitClone = unit.clone();
                float deltaTime = calculateDeltaTimeBetweenLastWorldUpdateAndCurrentTime();
                unitClone.update(deltaTime);

                batch.begin();
                Sprite sprite = SpriteContainer.getInstance().getSprite("m4-stopped-0");
                sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2 - 70);
                sprite.setPosition(unitClone.getX() - sprite.getWidth() / 2, unitClone.getY() - sprite.getWidth() / 2 + 70);
                sprite.setRotation(unitClone.getAngle() - 90);
                sprite.draw(batch);
                batch.end();
            } catch (CloneNotSupportedException e) {
                Gdx.app.debug(TAG, "ERROR: " + e.getMessage());
            }
        }
    }

    private void renderUnitEnergyBars(final RenderMode renderMode) {
        if (renderMode == RenderMode.GAME_STATE) {
            renderUnitEnergyBars();
        } else if (renderMode == RenderMode.GAME_STATE_WITH_PHYSICS_PREDICTION) {
            renderUnitEnergyBarsWithPhysicsPrediction();
        }
    }

    private void renderUnitEnergyBars() {
        for (Unit unit : worldController.getUnitContainer().getUnits()) {
            if (unit.isSelected()) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(Color.WHITE);
                Vector3 unitTopLeftWorldCoordinates = new Vector3(unit.getX() - unit.getWidth() / 2,
                        unit.getY() + unit.getHeight() / 2, 0);
                Vector3 unitTopRightWorldCoordinates = new Vector3(unit.getX() + unit.getWidth() / 2,
                        unit.getY() + unit.getHeight() / 2, 0);

                Vector3 unitTopLeftScreenCoordinates = worldController.getWorldCamera().project(unitTopLeftWorldCoordinates);
                Vector3 unitTopRightScreenCoordinates = worldController.getWorldCamera().project(unitTopRightWorldCoordinates);

                shapeRenderer.rect(unitTopLeftScreenCoordinates.x,
                        unitTopLeftScreenCoordinates.y,
                        unitTopRightScreenCoordinates.x - unitTopLeftScreenCoordinates.x,
                        10);
                shapeRenderer.end();
            }
        }
    }

    private void renderUnitEnergyBarsWithPhysicsPrediction() {
        for (Unit unit : worldController.getUnitContainer().getUnits()) {
            if (unit.isSelected()) {
                try {
                    Unit unitClone = unit.clone();
                    float deltaTime = calculateDeltaTimeBetweenLastWorldUpdateAndCurrentTime();
                    unitClone.update(deltaTime);

                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    shapeRenderer.setColor(Color.WHITE);
                    Vector3 unitTopLeftWorldCoordinates = new Vector3(unitClone.getX() - unitClone.getWidth() / 2,
                            unitClone.getY() + unitClone.getHeight() / 2, 0);
                    Vector3 unitTopRightWorldCoordinates = new Vector3(unitClone.getX() + unitClone.getWidth() / 2,
                            unitClone.getY() + unitClone.getHeight() / 2, 0);

                    Vector3 unitTopLeftScreenCoordinates = worldController.getWorldCamera().project(unitTopLeftWorldCoordinates);
                    Vector3 unitTopRightScreenCoordinates = worldController.getWorldCamera().project(unitTopRightWorldCoordinates);

                    shapeRenderer.rect(unitTopLeftScreenCoordinates.x,
                            unitTopLeftScreenCoordinates.y,
                            unitTopRightScreenCoordinates.x - unitTopLeftScreenCoordinates.x,
                            10);
                    shapeRenderer.end();
                } catch (CloneNotSupportedException e) {
                    Gdx.app.debug(TAG, "ERROR: " + e.getMessage());
                }


            }
        }
    }

    private float calculateDeltaTimeBetweenLastWorldUpdateAndCurrentTime() {
        return (System.currentTimeMillis() - worldController.getGameplayScreen().getLastWorldUpdateTimestamp()) / (float) 1000;
    }

    private void renderHud() {
        // TODO Implement hud
    }

    private void renderUnitSelectionRectangle() {
        Rectangle selectionRectangle = worldController.getGameplayInputManager().getUnitSelectionRectangle();

        if (selectionRectangle != null) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 255, 0, (float) 0.5);
            shapeRenderer.rect(selectionRectangle.x,
                    selectionRectangle.y,
                    selectionRectangle.width,
                    selectionRectangle.height);
            shapeRenderer.end();
        }
    }

    private void renderInfoText() {
        hudBatch.begin();
        defaultFont.draw(hudBatch,
                "Project \"MyRTS\", early alpha version",
                10,
                Gdx.graphics.getHeight() - 10);
        defaultFont.draw(hudBatch,
                String.valueOf(Gdx.graphics.getFramesPerSecond()) + "fps",
                10,
                Gdx.graphics.getHeight() - 10 - defaultFont.getLineHeight());
        defaultFont.draw(hudBatch,
                "World Update Tick: " + worldController.getGameplayScreen().getWorldUpdateTick(),
                10,
                Gdx.graphics.getHeight() - 10 - defaultFont.getLineHeight() * 2);
        defaultFont.draw(hudBatch,
                "Render Tick: " + worldController.getGameplayScreen().getRenderTick(),
                10,
                Gdx.graphics.getHeight() - 10 - defaultFont.getLineHeight() * 3);
        defaultFont.draw(hudBatch,
                "SimTick: 0",
                10,
                Gdx.graphics.getHeight() - 10 - defaultFont.getLineHeight() * 4);
        hudBatch.end();
    }

    public void resize(int width, int height) {
        worldController.getWorldCamera().viewportWidth = width;
        worldController.getWorldCamera().viewportHeight = height;
        worldController.getWorldCamera().update();

        // Resize hud
        hudBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void dispose() {
        defaultFont.dispose();
    }

}