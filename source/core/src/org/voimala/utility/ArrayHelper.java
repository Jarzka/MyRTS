package org.voimala.utility;

public class ArrayHelper {

    public static String[] reverseArray(String[] array) {
        String[] arrayReversed = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            arrayReversed[array.length - 1 - i] = array[i];
        }

        return arrayReversed;
    }
}
