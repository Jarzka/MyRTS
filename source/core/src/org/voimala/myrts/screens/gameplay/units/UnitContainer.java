package org.voimala.myrts.screens.gameplay.units;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UnitContainer {

    /** All units are kept in this array */
    private ArrayList<AbstractUnit> allUnits = new ArrayList<AbstractUnit>();

    /** The following arrays are used for fast access. These arrays contain the same references as allUnits.
     *  When a unit is added to this container, it should be added to allUnits and these arrays as well.
     *  Of course when a unit is removed from this container, it should be removed from allUnits and these specific
     *  arrays as well. */
    /** Unit id --> Unit */
    private HashMap<Long, AbstractUnit> unitsById = new HashMap<Long, AbstractUnit>();
    /** Player number --> Units */
    private HashMap<Integer, ArrayList<AbstractUnit>> unitsByPlayer = new HashMap<Integer, ArrayList<AbstractUnit>>();
    /** Team --> Units */
    private HashMap<Integer, ArrayList<AbstractUnit>> unitsByTeam = new HashMap<Integer, ArrayList<AbstractUnit>>();
    // TODO How to keep the containers in sync if a unit changes its' owner, team etc?

    public UnitContainer() {
        initializeSpecificContainers();
    }

    private void initializeSpecificContainers() {
        for (int i = 0; i <= 8; i++) {
            unitsByPlayer.put(i, new ArrayList<AbstractUnit>());
            unitsByTeam.put(i, new ArrayList<AbstractUnit>());
        }
    }

    public void addUnit(AbstractUnit unit) {
        unitsById.put(unit.getObjectId(), unit);
        unitsByPlayer.get(unit.getPlayerNumber()).add(unit);
        unitsByTeam.get(unit.getTeam()).add(unit);
        allUnits.add(unit);
    }

    public void removeUnit(final AbstractUnit unit) {
        unitsById.put(unit.getObjectId(), null);
        unitsByPlayer.get(unit.getPlayerNumber()).remove(unit);
        unitsByTeam.get(unit.getTeam()).remove(unit);
        allUnits.remove(unit);
    }

    /** NOTE: Do not modify the returned list! Modifications are made using addUnit
     * and removeUnit methods! */
    public List<AbstractUnit> getAllUnits() {
        return allUnits;
    }

    /** Returns null if unit is not found. */
    public AbstractUnit findUnitById(final long id) {
        return unitsById.get(id);
    }

    /** Returns an empty list if nothing if found. */
    public List<AbstractUnit> findUnitsByPlayerNumber(final int playerNumber) {
        return unitsByPlayer.get(playerNumber);
    }

    /** Returns an empty list if nothing if found. */
    public List<AbstractUnit> findUnitsByTeam(final int team) {
        return unitsByTeam.get(team);
    }

}
