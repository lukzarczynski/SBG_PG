/*
 * Copyright (C) 2017 giffgaff All rights reserved
 */
package com.lukzar.utils;

import com.lukzar.config.Configuration;
import com.lukzar.model.Point;
import com.lukzar.model.elements.Line;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class RayCasting {


    public static BitSet[] castLines(List<Line> converted) {
        final LinkedList<Line> ll = new LinkedList<>(converted);

        ll.addLast(new Line(ll.getLast().getEndPos(), ll.getFirst().getStartPos()));
        return RayCasting.cast(ll);
    }

    public static BitSet[] cast(List<Line> polygon) {

        int columns = (int) Math.ceil(Configuration.Piece.WIDTH);
        int rows = (int) Math.ceil(Configuration.Piece.HEIGHT);

        BitSet[] res = new BitSet[rows];
        for (int i = 0; i < rows; i++) {
            res[i] = new BitSet(columns);
        }

        IntStream.range(0, rows)
                .parallel()
                .forEach(row ->
                        IntStream.range(0, columns)
                                .filter(col -> isInside(polygon, Point.of(col, row)))
                                .forEach(col -> res[row].set(col))
                );
        return res;
    }


    private static boolean isInside(List<Line> polygon, Point point) {
        final Point start = Point.of(0, point.getY());
        final Point of = Point.of(point.getX(), point.getY());
        return polygon
                .stream()
                .filter(l -> IntersectionUtil.linesIntersect(
                        l.getStartPos(), l.getEndPos(), start, of))
                .count() % 2 == 1;
    }
}
