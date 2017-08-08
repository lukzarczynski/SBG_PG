package com.lukzar.utils;

/**
 * Created by lukasz on 16.07.17.
 */
public final class RandomUtils {

    /**
     * Random int from range [min, max] both inclusive
     */
    public static int randomRange(int min, int max) {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }
}
