package org.voimala.myrts.screens.gameplay.world;

import junit.framework.TestCase;
import org.junit.Test;
import org.voimala.myrts.screens.gameplay.ammunition.AbstractAmmunition;
import org.voimala.myrts.screens.gameplay.units.AbstractUnit;
import org.voimala.myrts.screens.gameplay.units.turrets.AbstractTurret;

public class WorldControllerTest extends TestCase {

    @Test
    public void testClone() {
        WorldController worldControllerSource = new WorldController();
        worldControllerSource.getUnitContainer().getAllUnits().get(0).getTurrets().get(0).setTarget(
                worldControllerSource.getUnitContainer().getAllUnits().get(5));
        String originalWorldHash = worldControllerSource.getGameStateHash();

        WorldController worldControllerClone = new WorldController(worldControllerSource);

        // Check that not a single object in the cloned world has a reference to the original world.

        for (AbstractUnit unit : worldControllerClone.getUnitContainer().getAllUnits()) {
            assertEquals(unit.getWorldController(), worldControllerClone);

            for (AbstractTurret turret : unit.getTurrets()) {
                assertEquals(turret.getWorldController(), worldControllerClone);
            }
        }

        for (AbstractAmmunition ammunition : worldControllerClone.getAmmunitionContainer()) {
            assertEquals(ammunition.getWorldController(), worldControllerClone);
        }

        // Check that turret have a correct owner

        for (AbstractUnit unit : worldControllerClone.getUnitContainer().getAllUnits()) {
            for (AbstractTurret turret : unit.getTurrets()) {
                assertEquals(turret.getOwnerUnit(), unit);
            }
        }

        // Check that turrets target are correct

        for (AbstractUnit unit : worldControllerClone.getUnitContainer().getAllUnits()) {
            for (AbstractTurret turret : unit.getTurrets()) {
                if (turret.hasTarget()) {
                    AbstractUnit unitSource = worldControllerSource.getUnitContainer().findUnitById(unit.getObjectId());
                    AbstractTurret turretSource = null;

                    for (AbstractTurret turretInSourceWorld : unitSource.getTurrets()) {
                        if (turretInSourceWorld.getObjectId() == turret.getObjectId()) {
                            turretSource = turretInSourceWorld;
                            break;
                        }
                    }

                    assertEquals(turret.getTarget().getObjectId(), turretSource.getTarget().getObjectId());
                    assertTrue(turret.getTarget() != turretSource.getTarget());
                }

            }
        }

        // Check that the original world has not been modified
        assertEquals(originalWorldHash, worldControllerSource.getGameStateHash());

        // Check that the hashes are same
        assertEquals(originalWorldHash, worldControllerClone.getGameStateHash());
    }

    // TODO Create two simulations are make sure they are deterministic
}