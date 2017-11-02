package com.lukzar.utils;

import com.lukzar.model.Point;
import com.lukzar.model.elements.Line;

import java.awt.geom.Line2D;

public class IntersectionUtil {

    public static boolean linesIntersect(Line line1, Line line2) {
        return linesIntersect(line1.getStartPos(), line1.getEndPos(),
                line2.getStartPos(), line2.getEndPos());
    }

    public static boolean linesIntersect(Point line1Start, Point l1end,
                                         Point line2start, Point l2end) {
        if (line1Start.equals(l2end) || line2start.equals(l1end)) {
            return false;
        }
        return Line2D.linesIntersect(line1Start.getX() + 0.1, line1Start.getY() + 0.1,
                l1end.getX() + 0.1, l1end.getY() + 0.1,
                line2start.getX(), line2start.getY(),
                l2end.getX(), l2end.getY());
    }

}