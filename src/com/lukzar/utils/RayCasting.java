/*
 * Copyright (C) 2017 giffgaff All rights reserved
 */
package com.lukzar.utils;

import com.lukzar.config.Configuration;
import com.lukzar.model.Piece;
import com.lukzar.model.Point;
import com.lukzar.model.elements.Line;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class RayCasting {


    public static boolean[][] cast(Piece svg) {
        svg.convertToAsymmetric();
        List<Line> converted = svg.getAsLines();
        LinkedList<Line> ll = new LinkedList<>(converted);

        ll.addLast(new Line(ll.getLast().getEndPos(), ll.getFirst().getStartPos()));

        return RayCasting.cast(ll);
    }

    public static boolean[][] cast(List<Line> polygon) {

        int columns = (int) Math.ceil(Configuration.Piece.WIDTH);
        int rows = (int) Math.ceil(Configuration.Piece.HEIGHT);
        boolean[][] result = new boolean[columns][rows];

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                Point testPoint = Point.of(column, row);
                result[row][column] = isInside(polygon, testPoint);
            }
        }

        return result;
    }


    public static boolean isInside(List<Line> polygon, Point point) {
        Point start = Point.of(0.0, point.getY());
        return polygon.stream()
                .filter(l -> {
                    Optional<Point> point1 = IntersectionUtil.lineToLineIntersection(
                            l.getStartPos(), l.getEndPos(),
                            start, Point.of(point.getX() - 0.1, point.getY() - 0.1));

                    return point1.isPresent();
                })
                .count() % 2 == 1;
    }
}
