package org.voimala.myrts.gameplay.units.infantry;

import org.voimala.myrts.gameplay.units.Unit;
import org.voimala.myrts.gameplay.units.movements.CarMovement;

public class Infantry extends Unit {

    public Infantry() {
        movement = new CarMovement(this);
    }
}
