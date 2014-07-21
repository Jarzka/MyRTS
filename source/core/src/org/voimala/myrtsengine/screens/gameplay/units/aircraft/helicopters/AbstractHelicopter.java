package org.voimala.myrtsengine.screens.gameplay.units.aircraft.helicopters;

import org.voimala.myrtsengine.screens.gameplay.GameplayScreen;
import org.voimala.myrtsengine.screens.gameplay.units.aircraft.AbstractAircraft;
import org.voimala.myrtsengine.screens.gameplay.world.WorldController;

public abstract class AbstractHelicopter extends AbstractAircraft {
    public AbstractHelicopter(WorldController worldController) {
        super(worldController);
    }
}