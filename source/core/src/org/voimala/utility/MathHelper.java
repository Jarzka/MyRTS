package org.voimala.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathHelper {
    /** Is it faster to turn clockwise or counter-clockwise from angle1 to reach angle2.
     * @return 1 = clockwise, 2 = counter-clockwise
     */
    public static byte getFasterTurningDirection(double angle1Radians,
                                                 double angle2Radians) {
        // Calculate radians clockwise
        double clockwise = getDistanceBetweenAngles(angle1Radians, angle2Radians, 1);
        double counterCockwise = getDistanceBetweenAngles(angle1Radians, angle2Radians, 2);

        if (Math.abs(clockwise) < Math.abs(counterCockwise)) {
            return 1;
        }

        return 2;
    }

    /** @return Returns positive radians between two points. */
    public static double getAngleBetweenPointsInRadians(final double x1,
                                                        final double y1,
                                                        final double x2,
                                                        final double y2) {
        double radians = Math.atan2(y2 - y1, x2 - x1);

        while (radians < 0) {
            radians += Math.PI * 2;
        }

        return radians;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double getDistanceBetweenPoints(float x1, float y1, float x2, float y2) {
        return Math.sqrt((Math.pow(x2 - x1, 2)) + (Math.pow(y1 - y2, 2)));
    }

    public static double getShortestDistanceBetweenAngles(double angle1Radians,
                                                          double angle2Radians) {
        double result1 = 0;
        double result2 = 0;

        if (angle1Radians > angle2Radians) {
            result1 = angle1Radians - angle2Radians;
            result2 = (Math.PI * 2) - angle1Radians + angle2Radians;
        } else if (angle1Radians < angle2Radians) {
            result1 = angle2Radians - angle1Radians;
            result2 = (Math.PI * 2) - angle2Radians + angle1Radians;
        }

        if (result1 < result2) {
            return result1;
        }

        return result2;
    }

    /** @param rotationDirection 1 = clockwise, 2 = counter-clockwise */
    // TODO OLD VERSION
    public static double getDistanceBetweenAngles(double angle1Radians,
                                                          double angle2Radians,
                                                          int rotationDirection) {
        // TODO There has to be a faster way to calculate this.

        if (rotationDirection == 1) {
            // Calculate radians clockwise
            double clockwiseAngleInRadians = angle1Radians;
            double clockwiseTurnedRadians = 0;
            while (round(clockwiseAngleInRadians, 1) != round(angle2Radians, 1)) {
                clockwiseTurnedRadians -= 0.04;
                clockwiseAngleInRadians -= 0.04;

                if (clockwiseAngleInRadians < 0) {
                    clockwiseAngleInRadians = Math.PI * 2 - clockwiseAngleInRadians;
                }
            }

            return Math.abs(clockwiseTurnedRadians);
        } else if (rotationDirection == 2) {
            // Calculate radians counter-clockwise
            double counterClockwiseAngleInRadians = angle1Radians;
            double counterClockwiseTurnedRadians = 0;
            while (round(counterClockwiseAngleInRadians, 1) != round(angle2Radians, 1)) {
                counterClockwiseTurnedRadians += 0.04;
                counterClockwiseAngleInRadians += 0.04;

                if (counterClockwiseAngleInRadians > Math.PI * 2) {
                    counterClockwiseAngleInRadians = 0 + counterClockwiseAngleInRadians - Math.PI * 2;
                }
            }

            return Math.abs(counterClockwiseTurnedRadians);
        } else {
            throw new RuntimeException("rotationDirection should be 1 or 2.");
        }
    }
}
