package com.lukzar.utils;

import com.lukzar.model.Point;

/**
 * Created by lukasz on 13.08.17.
 */
public class PolygonUtils {

    /**
     * https://en.wikipedia.org/wiki/Law_of_cosines
     *
     * @return arc < ACB in degrees
     */
    public static double calculateArc(Point A, Point C, Point B) {
        double c = distance(A, B);
        double a = distance(B, C);
        double b = distance(A, C);

        return Math.acos(((a * a) + (b * b) - (c * c)) / (2 * a * b)) * 180 / Math.PI;
    }

    public static double distance(Point a, Point b) {
        double x1 = a.getX();
        double x2 = b.getX();
        double y1 = a.getY();
        double y2 = b.getY();
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
}
