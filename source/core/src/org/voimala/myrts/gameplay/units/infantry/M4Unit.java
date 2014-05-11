package org.voimala.myrts.gameplay.units.infantry;

public class M4Unit extends Infantry {

    public M4Unit() {
        initializeMovement();
    }

    private void initializeMovement() {
        movement.setMaxVelocity(400);
        movement.setAcceleration(440);
        movement.setDeceleration(440);
        movement.setMaxRotationVelocity(100);
        movement.setRotationAcceleration(20);
        movement.setRotationDeceleration(20);
    }
}
