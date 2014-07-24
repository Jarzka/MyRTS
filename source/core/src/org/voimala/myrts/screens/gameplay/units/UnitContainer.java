package org.voimala.myrts.screens.gameplay.units;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UnitContainer {

    private ArrayList<AbstractUnit> allUnits = new ArrayList<AbstractUnit>();
    /** Contains all units used by a specific player for fast access. Integer = Player number */
    private HashMap<Integer, ArrayList<AbstractUnit>> unitsByPlayer = new HashMap<Integer, ArrayList<AbstractUnit>>();

    public UnitContainer() {
        initializeSpecificContainers();
    }

    private void initializeSpecificContainers() {
        for (int i = 0; i <= 8; i++) {
            unitsByPlayer.put(i, new ArrayList<AbstractUnit>());
        }
    }

    public void addUnit(AbstractUnit unit) {
        unitsByPlayer.get(unit.getPlayerNumber()).add(unit);
        allUnits.add(unit);
    }

    public void removeUnit(final AbstractUnit unit) {
        unitsByPlayer.get(unit.getPlayerNumber()).remove(unit);
        allUnits.remove(unit);
    }

    /** NOTE: Do not modify the returned list! Modifications are made using addUnit
     * and removeUnit methods! */
    public List<AbstractUnit> getAllUnits() {
        return allUnits;
    }

    /** Returns null if unit is not found. */
    public AbstractUnit findUnitById(final long id) {
        // TODO Do not use linear search here?
        for (AbstractUnit unit : getAllUnits()) {
            if (unit.getObjectId() == id) {
                return unit;
            }
        }

        return null;
    }

    public List<AbstractUnit> findUnitsByPlayerNumber(final int playerNumber) {
        return unitsByPlayer.get(playerNumber);
    }

}
