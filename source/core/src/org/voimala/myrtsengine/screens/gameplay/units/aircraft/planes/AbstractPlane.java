package org.voimala.myrtsengine.screens.gameplay.units.aircraft.planes;

import org.voimala.myrtsengine.screens.gameplay.GameplayScreen;
import org.voimala.myrtsengine.screens.gameplay.units.aircraft.AbstractAircraft;
import org.voimala.myrtsengine.screens.gameplay.world.WorldController;

public abstract class AbstractPlane extends AbstractAircraft {

    public AbstractPlane(WorldController worldController) {
        super(worldController);
    }
}