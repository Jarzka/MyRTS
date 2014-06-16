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

}
