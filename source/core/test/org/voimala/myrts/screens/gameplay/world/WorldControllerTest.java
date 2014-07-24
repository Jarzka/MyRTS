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
        worldControllerSource.getUnitContainerAllUnits().getUnits().get(0).getTurrets().get(0).setTarget(
                worldControllerSource.getUnitContainerAllUnits().getUnits().get(5));
        String originalWorldHash = worldControllerSource.getGameStateHash();

        WorldController worldControllerClone = new WorldController(worldControllerSource);

        // Check that not a single object in the cloned world has a reference to the original world.

        for (AbstractUnit unit : worldControllerClone.getUnitContainerAllUnits().getUnits()) {
            assertEquals(unit.getWorldController(), worldControllerClone);

            for (AbstractTurret turret : unit.getTurrets()) {
                assertEquals(turret.getWorldController(), worldControllerClone);
            }
        }

        for (AbstractAmmunition ammunition : worldControllerClone.getAmmunitionContainer()) {
            assertEquals(ammunition.getWorldController(), worldControllerClone);
        }

        // Check that turret have a correct owner

        for (AbstractUnit unit : worldControllerClone.getUnitContainerAllUnits().getUnits()) {
            for (AbstractTurret turret : unit.getTurrets()) {
                assertEquals(turret.getOwnerUnit(), unit);
            }
        }

        // Check that turrets target are correct

        for (AbstractUnit unit : worldControllerClone.getUnitContainerAllUnits().getUnits()) {
            for (AbstractTurret turret : unit.getTurrets()) {
                if (turret.hasTarget()) {
                    AbstractUnit unitSource = worldControllerSource.getUnitContainerAllUnits().findUnitById(unit.getObjectId());
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

    @Test
    public void testSynchronizeWith() {
        WorldController world1 = new WorldController();

        WorldController world2 = new WorldController();
        world2.getUnitContainerAllUnits().getUnits().clear();

        world2 = WorldController.synchronizeWorlds(world2, world1);

        assertEquals(world1.getGameStateHash(), world2.getGameStateHash());
    }

}