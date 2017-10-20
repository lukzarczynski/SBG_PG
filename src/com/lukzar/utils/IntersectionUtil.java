package com.lukzar.utils;

import com.lukzar.model.Point;
import com.lukzar.model.elements.Line;

import java.util.Optional;

/**
 * Created by lukasz on 09.07.17.
 */
public class IntersectionUtil {

    private static final double epsilon = 0.00001;

    public static Optional<Point> lineToLineIntersection(Line line1, Line line2) {
        return lineToLineIntersection(line1.getStartPos(), line1.getEndPos(),
                line2.getStartPos(), line2.getEndPos());
    }

    public static Optional<Point> lineToLineIntersection(Point line1Start, Point l1end,
                                                         Point line2start, Point l2end) {
        if (line1Start.equals(l2end) || line2start.equals(l1end)) {
            return Optional.empty();
        }
        double denominator = (line2start.getX() - l2end.getX()) * (line1Start.getY() - l1end.getY())
                - (line2start.getY() - l2end.getY()) * (line1Start.getX() - l1end.getX());
        if (denominator == 0) {
            return Optional.empty();
        }
        Point point = Point.of((
                        (line2start.getX() * l2end.getY() - line2start.getY() * l2end.getX()) *
                                (line1Start.getX() - l1end.getX()) -
                                (line2start.getX() - l2end.getX()) *
                                        (line1Start.getX() * l1end.getY() - line1Start.getY() * l1end.getX()))
                        / denominator,
                (
                        (line2start.getX() * l2end.getY() - line2start.getY() * l2end.getX()) *
                                (line1Start.getY() - l1end.getY())
                                - (line2start.getY() - l2end.getY())
                                * (line1Start.getX() * l1end.getY() - line1Start.getY() * l1end.getX()))
                        / denominator);

        if (isInRangeInclusive(point.getX(), line1Start.getX(), l1end.getX())
                && isInRangeInclusive(point.getX(), line2start.getX(), l2end.getX())) {
            return Optional.of(point);
        }
        return Optional.empty();
    }

    private static boolean isInRangeInclusive(double x, double a, double b) {
        double min = Math.min(a, b);
        double max = Math.max(a, b);
        return (x >= min || doubleEquals(x, min)) && (x <= max || doubleEquals(x, max));
    }

    private static boolean doubleEquals(double a, double b) {
        return Math.abs(a - b) <= epsilon;
    }
}