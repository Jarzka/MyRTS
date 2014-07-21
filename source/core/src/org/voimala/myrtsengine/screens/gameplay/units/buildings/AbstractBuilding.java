package org.voimala.myrtsengine.screens.gameplay.units.buildings;

import org.voimala.myrtsengine.screens.gameplay.GameplayScreen;
import org.voimala.myrtsengine.screens.gameplay.units.AbstractUnit;
import org.voimala.myrtsengine.screens.gameplay.world.WorldController;

public abstract class AbstractBuilding extends AbstractUnit {
    public AbstractBuilding(WorldController worldController) {
        super(worldController);
    }
}
