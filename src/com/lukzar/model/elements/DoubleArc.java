package com.lukzar.model.elements;

import com.lukzar.config.Configuration;
import com.lukzar.model.Point;
import com.lukzar.utils.BezierUtil;

import java.util.List;

/**
 * NOT USED for now
 * <p>
 * Created by lukasz on 04.06.17.
 */
public class DoubleArc extends Part {

    private final Point q1;
    private final Point q2;

    public DoubleArc(Point endPos, Point q1, Point q2) {
        super(endPos);
        this.q1 = q1;
        this.q2 = q2;
    }

    @Override
    public String toSvg() {
        return String.format("C%s %s %s ", q1.toSvg(), q2.toSvg(), endPos.toSvg());
    }

    @Override
    public String toSvgReversed() {
        return String.format("C%f,%f %f,%f %f,%f",
                Configuration.Piece.WIDTH - q2.getX(), q2.getY(),
                Configuration.Piece.WIDTH - q1.getX(), q1.getY(),
                Configuration.Piece.WIDTH - startPos.getX(), startPos.getY());
    }

    @Override
    public List<Line> convertToLines() {
        return BezierUtil.convertToLine(this);
    }

    public Point getQ1() {
        return this.q1;
    }

    public Point getQ2() {
        return this.q2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DoubleArc doubleArc = (DoubleArc) o;

        if (!q1.equals(doubleArc.q1)) return false;
        return q2.equals(doubleArc.q2);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + q1.hashCode();
        result = 31 * result + q2.hashCode();
        return result;
    }
}
