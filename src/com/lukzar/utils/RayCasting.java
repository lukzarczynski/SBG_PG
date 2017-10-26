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
import java.util.stream.IntStream;

public class RayCasting {


    public static boolean[][] cast(Piece svg) {
        return castLines(svg.getAsLines());
    }

    public static boolean[][] castLines(List<Line> converted) {
        Timer t = Timer.start();
        final LinkedList<Line> ll = new LinkedList<>(converted);

        ll.addLast(new Line(ll.getLast().getEndPos(), ll.getFirst().getStartPos()));
        boolean[][] cast = RayCasting.cast(ll);
        t.end("Ray");
        return cast;
    }

    public static boolean[][] cast(List<Line> polygon) {

        int columns = (int) Math.ceil(Configuration.Piece.WIDTH);
        int rows = (int) Math.ceil(Configuration.Piece.HEIGHT);
        boolean[][] result = new boolean[columns][rows];
        IntStream.range(0, rows)
                .parallel()
                .forEach(row ->
                        IntStream.range(0, columns)
                                .forEach(col ->
                                        result[row][col] = isInside(polygon, Point.of(col, row)))
                );
        return result;
    }


    private static boolean isInside(List<Line> polygon, Point point) {
        Point start = Point.of(0.0, point.getY());
        Point of = Point.of(point.getX() - 0.1, point.getY() - 0.1);
        return polygon
                .stream()
                .map(l -> IntersectionUtil.lineToLineIntersection(
                        l.getStartPos(), l.getEndPos(), start, of))
                .filter(Optional::isPresent)
                .count() % 2 == 1;
    }
}
