package com.lukzar.model.elements;

import com.lukzar.config.Configuration;
import com.lukzar.model.Point;
import com.lukzar.utils.BezierUtil;

import java.util.List;
import java.util.Locale;

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
    public String toSvgReversed() {
        return String.format(Locale.US, "Q%f,%f %f,%f ",
                Configuration.Piece.WIDTH - q.getX(), q.getY(),
                Configuration.Piece.WIDTH - startPos.getX(), startPos.getY());
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
}
