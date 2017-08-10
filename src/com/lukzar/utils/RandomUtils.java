package com.lukzar.utils;

/**
 * Created by lukasz on 16.07.17.
 */
public final class RandomUtils {

    /**
     * Random int from range [min, max] both inclusive
     */
    public static double randomRange(double min, double max) {
        double range = (max - min) + 1;
        return (Math.random() * range) + min;
    }
}
