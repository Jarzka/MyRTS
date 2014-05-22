package org.voimala.myrts.scenes.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import org.voimala.myrts.scenes.gameplay.units.Unit;
import org.voimala.myrts.graphics.SpriteContainer;

public class WorldRenderer implements Disposable {

    private SpriteBatch batch;
    private SpriteBatch hudBatch;
    private WorldController worldController;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    public WorldRenderer (WorldController worldController) {
        this. worldController = worldController;

        initialize();
    }

    private void initialize () {
        initializeBatches();
    }

    private void initializeBatches() {
        batch = new SpriteBatch();
        hudBatch = new SpriteBatch();
    }


    public void render () {
        /* This game uses the standard mathematic circle where 0 degrees point to right,
         * 90 degrees point to up etc. Libgdx seems to use a circle where 0 degrees
         * point to up, 90 degrees point to left etc. WorldRenderer makes the conversion
         * automatically.
         */
        batch.setProjectionMatrix(worldController.getWorldCamera().combined);

        renderGround();
        renderUnits();
        renderUnitEnergyBars();
        renderHud();
        renderUnitSelectionRectangle();
        renderInfoText();
    }

    private void renderGround() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                batch.begin();
                Sprite sprite = SpriteContainer.getInstance().getSprite("grass1");
                sprite.setPosition(i * worldController.TILE_SIZE_PIXELS, j * worldController.TILE_SIZE_PIXELS);
                sprite.draw(batch);
                batch.end();
            }
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
                        (float) (Gdx.graphics.getPpiY() * 0.1)); // TODO ppi is not supported on the desktop
                shapeRenderer.end();
            }
        }
    }

    private void renderHud() {
        // TODO
    }

    private void renderUnitSelectionRectangle() {
        Rectangle selectionRectangle = worldController.getInputManager().getUnitSelectionRectangle();

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
        BitmapFont font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.draw(hudBatch,
                "Project named \"MyRTS\", early alpha version",
                10,
                Gdx.graphics.getHeight() - 10);
        font.draw(hudBatch,
                String.valueOf(Gdx.graphics.getFramesPerSecond()) + "fps",
                10,
                Gdx.graphics.getHeight() - 10 - font.getLineHeight());
        hudBatch.end();
    }

    public void resize(int width, int height) {
        worldController.getWorldCamera().viewportWidth = Gdx.graphics.getWidth();
        worldController.getWorldCamera().viewportHeight = Gdx.graphics.getHeight();
        worldController.getWorldCamera().update();

        // Resize hud
        hudBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void dispose() {

    }

}