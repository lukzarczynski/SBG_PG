package com.lukzar.utils;

import com.lukzar.config.Configuration;
import com.lukzar.model.Piece;
import com.lukzar.model.Point;
import com.lukzar.model.elements.Line;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 13.08.17.
 */
public class PolygonUtils {


    public static List<Point> trim(Piece piece, double min, double max) {
        final List<Line> lines = piece.getAsLines();

        List<Point> result = lines.stream()
                .map(line -> splitIntersectingLines(min, max, line))
                .flatMap(Collection::stream)
                .map(Line::getEndPos)
                .map(p -> Point.of(p.getX(), trim(p.getY(), min, max)))
                .collect(Collectors.toList());
        result.add(0, Point.of(piece.getStart().getX(), trim(piece.getStart().getY(), min, max)));
        result.add(Point.of(
                piece.isAsymmetric() ?
                        piece.getParts().getLast().getEndPos().getX() :
                        Configuration.Piece.WIDTH / 2.0,
                max));
        return result;

    }

    private static List<Line> splitIntersectingLines(double min, double max, Line line) {
        return splitIntersectingLines(min, line)
                .stream()
                .map(l -> splitIntersectingLines(max, l))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static List<Line> splitIntersectingLines(double y, Line line) {
        Optional<Point> intersection = IntersectionUtil.lineToLineIntersection(
                line.getStartPos(), line.getEndPos(),
                Point.of(Configuration.Piece.WIDTH / 2.0, y), Point.of(Configuration.Piece.WIDTH, y));
        return intersection.map(point -> Arrays.asList(
                new Line(line.getStartPos(), point),
                new Line(point, line.getEndPos())))
                .orElseGet(() -> Collections.singletonList(line));
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
