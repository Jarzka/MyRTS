package org.voimala.myrts.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import org.voimala.myrts.gameplay.units.Unit;
import org.voimala.myrts.graphics.SpriteContainer;

public class WorldRenderer implements Disposable {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private WorldController worldController;

    public final int TILE_SIZE_PIXELS = 256;

    public WorldRenderer (WorldController worldController) {
        this. worldController = worldController;

        initialize();
    }

    private void initialize () {
        initializeBatch();
        initializeCamera();
    }

    private void initializeBatch() {
        batch = new SpriteBatch();
    }

    private void initializeCamera() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.lookAt(0, 0, 0);
        camera.translate(800, 800);
        camera.zoom = 4;
        camera.update();
    }

    public void render () {
        batch.setProjectionMatrix(camera.combined);

        renderGround();
        renderUnits();
    }

    private void renderGround() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                batch.begin();
                Sprite sprite = SpriteContainer.getInstance().getSprite("grass1");
                sprite.setPosition(i * TILE_SIZE_PIXELS, j * TILE_SIZE_PIXELS);
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
            sprite.setRotation(unit.getAngle());
            sprite.draw(batch);
            batch.end();
        }
    }

    public void resize(int width, int height) {
        camera.viewportWidth = Gdx.graphics.getWidth();
        camera.viewportHeight = Gdx.graphics.getHeight();
        camera.update();
    }

    @Override
    public void dispose() {

    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}