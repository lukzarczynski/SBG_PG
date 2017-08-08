package com.lukzar.model.elements;

import com.lukzar.model.Point;

/**
 * Created by lukasz on 04.06.17.
 */
public class Arc extends Part {
    private final Point q;

    public Arc(Point endPos, Point q) {
        super(endPos);
        this.q = q;
    }

    public Point getQ() {
        return q;
    }

    @Override
    public String toSvg() {
        return String.format("Q%d,%d %d,%d ", q.getX(), q.getY(),
                endPos.getX(), endPos.getY());
    }

    @Override
    public String toSvgReversed(Point startPoint, int middle_x) {
        return String.format("Q%d,%d %d,%d ",
                2 * middle_x - q.getX(), q.getY(),
                2 * middle_x - startPoint.getX(), startPoint.getY());
    }
}
