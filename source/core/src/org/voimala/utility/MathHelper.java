package org.voimala.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathHelper {
    /** Is it faster to turn clockwise or counter-clockwise from angle1 to reach angle2. */
    public static RotationDirection getFasterTurningDirection(double angle1Radians,
                                                 double angle2Radians) {
        if (angle1Radians < 0 || angle1Radians > Math.PI * 2) {
            throw new IllegalArgumentException("Angle 1 must be between 0 and PI * 2 (inclusive), "
                    + angle1Radians + " " + "given.");
        }

        if (angle2Radians < 0 || angle2Radians > Math.PI * 2) {
            throw new IllegalArgumentException("Angle 2 must be between 0 and PI * 2 (inclusive), "
                    + angle2Radians + " " + "given.");
        }

        double distanceClockwise = getDistanceFromAngle1ToAngle2(angle1Radians,
                angle2Radians,
                RotationDirection.CLOCKWISE);
        double distanceCounterClockwise = getDistanceFromAngle1ToAngle2(angle1Radians,
                angle2Radians,
                RotationDirection.COUNTERCLOCKWISE);

        if (distanceClockwise < distanceCounterClockwise) {
            return RotationDirection.CLOCKWISE;
        }

        return RotationDirection.COUNTERCLOCKWISE;
    }

    public static double getDistanceFromAngle1ToAngle2(double angleSourceRadians,
                                                       double angleTargetRadians,
                                                       RotationDirection rotationDirection) {
        if (angleSourceRadians < 0 || angleSourceRadians > Math.PI * 2) {
            throw new IllegalArgumentException("Angle 1 must be between 0 and PI * 2 (inclusive)");
        }

        if (angleTargetRadians < 0 || angleTargetRadians > Math.PI * 2) {
            throw new IllegalArgumentException("Angle 2 must be between 0 and PI * 2 (inclusive)");
        }

        double distance = 0;

        if (angleSourceRadians > angleTargetRadians && rotationDirection == RotationDirection.CLOCKWISE) {
            distance = angleSourceRadians - angleTargetRadians;
        }

        if (angleSourceRadians>angleTargetRadians && rotationDirection == RotationDirection.COUNTERCLOCKWISE) {
            distance = (Math.PI * 2) - angleSourceRadians + angleTargetRadians;
        }

        if (angleSourceRadians<angleTargetRadians && rotationDirection == RotationDirection.CLOCKWISE) {
            distance = (Math.PI * 2) - angleTargetRadians + angleSourceRadians;
        }

        if (angleSourceRadians<angleTargetRadians && rotationDirection == RotationDirection.COUNTERCLOCKWISE) {
            distance = angleTargetRadians - angleSourceRadians;
        }

        return distance;
    }

    /* This method is not needed and it has not been tested.
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
    */

    /** @return Returns positive radians between two points. */
    public static double getAngleBetweenPointsInRadians(final double x1,
                                                        final double y1,
                                                        final double x2,
                                                        final double y2) {
        double radians = Math.atan2(y2 - y1, x2 - x1);

        // No negative angles
        while (radians < 0) {
            radians += Math.PI * 2;
        }

        return radians;
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double getDistanceBetweenPoints(float x1, float y1, float x2, float y2) {
        return Math.sqrt((Math.pow(x2 - x1, 2)) + (Math.pow(y1 - y2, 2)));
    }
}
