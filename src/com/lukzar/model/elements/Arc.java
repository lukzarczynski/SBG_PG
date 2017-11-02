package com.lukzar.model.elements;

import com.lukzar.config.Configuration;
import com.lukzar.model.Point;
import com.lukzar.utils.BezierUtil;

import java.util.List;

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
        return String.format("Q%s %s ", q.toSvg(),
                endPos.toSvg());
    }

    @Override
    public Arc reverse() {
        double qx = Configuration.Piece.WIDTH - q.getX();
        double qy = q.getY();
        double ex = Configuration.Piece.WIDTH - startPos.getX();
        double ey = startPos.getY();
        return new Arc(Point.of(ex, ey), Point.of(qx, qy));
    }

    @Override
    public List<Line> convertToLines() {
        return BezierUtil.convertToLine(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Arc arc = (Arc) o;

        return q.equals(arc.q);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + q.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s - [%s] - %s", startPos.toSvg(), q.toString(), endPos.toString());
    }
}
