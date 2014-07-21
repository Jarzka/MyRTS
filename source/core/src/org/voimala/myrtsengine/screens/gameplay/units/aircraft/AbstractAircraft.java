package org.voimala.myrtsengine.screens.gameplay.units.aircraft;

import org.voimala.myrtsengine.screens.gameplay.GameplayScreen;
import org.voimala.myrtsengine.screens.gameplay.units.AbstractUnit;
import org.voimala.myrtsengine.screens.gameplay.world.WorldController;

public abstract class AbstractAircraft extends AbstractUnit {
    public AbstractAircraft(WorldController worldController) {
        super(worldController);
    }
}
