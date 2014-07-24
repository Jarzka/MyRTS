package org.voimala.myrts.screens.gameplay.units;

import java.util.ArrayList;

public class UnitContainer {

    private ArrayList<AbstractUnit> units = new ArrayList<AbstractUnit>();

    public ArrayList<AbstractUnit> getUnits() {
        return units;
    }

    public void addUnit(final AbstractUnit unit) {
        units.add(unit);
    }

    public void removeUnit(final AbstractUnit unit) {
        units.remove(unit);
    }

    /** Returns null if unit is not found. */
    public AbstractUnit findUnitById(final long id) {
        // TODO Do not use linear search here?
        for (AbstractUnit unit : getUnits()) {
            if (unit.getObjectId() == id) {
                return unit;
            }
        }

        return null;
    }

}
