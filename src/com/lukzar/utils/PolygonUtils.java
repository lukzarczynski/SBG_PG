package com.lukzar.utils;

import com.lukzar.model.Point;
import com.lukzar.model.elements.Line;
import com.lukzar.model.elements.Part;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 13.08.17.
 */
public class PolygonUtils {


    public static List<Line> trim(List<Line> lines, int min, int max) {
        return lines.stream()
                .map(Part::getEndPos)
                .map(p -> Point.of(p.getX(), trim(p.getY(), min, max)))
                .map(Line::new)
                .collect(Collectors.toList());

    }

    private static double trim(double x, double min, double max) {
        return x > max ? max
                : x < min ? min
                : x;
    }


    /**
     * https://en.wikipedia.org/wiki/Polygon
     *
     * @return area of simple polygon
     */
    public static double calculateArea(List<Point> points) {
        int n = points.size() + 1;
        Point[] p = new Point[n];
        for (int i = 0; i < n - 1; i++) {
            p[i] = points.get(i);
        }
        p[n - 1] = p[0];

        double A = 0.0;
        for (int i = 0; i < n - 1; i++) {
            A += ((p[i].getX() * p[i + 1].getY()) - (p[i + 1].getX() * p[i].getY()));
        }
        return Math.abs(A / 2.0);
    }


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
