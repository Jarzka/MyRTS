package org.voimala.myrts.screens.gameplay.effects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.graphics.SpriteContainer;
import org.voimala.myrts.screens.gameplay.world.WorldController;
import org.voimala.utility.RandomNumberGenerator;

public class GeneralMuzzleFire extends MuzzleFire {

    private int muddleFireSpriteId = RandomNumberGenerator.random(1, 3);

    public GeneralMuzzleFire(final WorldController worldController, final Vector2 position, final float angleDeg) {
        super(worldController, position, angleDeg);

        lifeTimeMs = 70;
    }

    @Override
    public void updateState(final float deltaTime) {
        super.updateState(deltaTime);
        increaseLivedLive(deltaTime);
        checkIfItIsTimeToDie();
    }

    private void increaseLivedLive(final float deltaTime) {
        livedLife += deltaTime * 1000;
    }

    private void checkIfItIsTimeToDie() {
        if (livedLife >= lifeTimeMs) {
            die();
        }
    }

    private void die() {
        worldController.tagEffectToBeRemoved(this);
    }

    @Override
    protected void initializeDimensions() {
        width = 58;
        height = 76;
    }

    @Override
    protected void initializeCollisionMask() {
        // Can be left empty
    }

    @Override
    protected void initializeMovement() {
        // Can be left empty
    }

    @Override
    public Sprite getSprite() {
        return SpriteContainer.getInstance().getSprite("general-muzzle-fire" + muddleFireSpriteId);
    }

    @Override
    protected void updateCollisionMask() {
        // Can be left empty
    }

    @Override
    public boolean onCollision(Vector2 point) {
        return false;
    }

}
