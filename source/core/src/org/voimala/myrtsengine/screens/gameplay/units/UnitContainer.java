package org.voimala.myrtsengine.screens.gameplay.units;

import java.util.ArrayList;

public class UnitContainer {

    private ArrayList<AbstractGameplayObject> units = new ArrayList<AbstractGameplayObject>();

    public ArrayList<AbstractGameplayObject> getUnits() {
        return units;
    }

    public void addUnit(final AbstractGameplayObject unit) {
        units.add(unit);
    }

    public void removeUnit(final AbstractGameplayObject unit) {
        units.remove(unit);
    }

    /** Returns null if unit is not found. */
    public AbstractGameplayObject findUnitById(final long id) {
        // TODO Do not use linear search here?
        for (AbstractGameplayObject unit : getUnits()) {
            if (unit.getObjectId() == id) {
                return unit;
            }
        }

        return null;
    }

}
