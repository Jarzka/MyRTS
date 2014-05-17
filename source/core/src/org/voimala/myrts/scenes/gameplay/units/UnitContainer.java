package org.voimala.myrts.scenes.gameplay.units;

import java.util.ArrayList;

public class UnitContainer {

    private ArrayList<Unit> units = new ArrayList<Unit>();

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public void addUnit(final Unit unit) {
        units.add(unit);
    }

    public void removeUnit(final Unit unit) {
        units.remove(unit);
    }

}
