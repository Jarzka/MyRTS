package org.voimala.myrts.gameplay.units.infantry;

public class M4Unit extends Infantry {

    public M4Unit() {
        initializeMovement();
    }

    private void initializeMovement() {
        movement.setMaxVelocity(120);
        movement.setAcceleration(50);
        movement.setDeceleration(50);
        movement.setMaxRotationVelocity(100);
        movement.setRotationAcceleration(50);
        movement.setRotationDeceleration(50);
    }
}
