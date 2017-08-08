package com.lukzar.model.elements;

import com.lukzar.Main;
import com.lukzar.model.Point;
import com.lukzar.utils.IntersectionUtil;

import lombok.Getter;

/**
 * Created by lukasz on 04.06.17.
 */
@Getter
public class Arc extends Part {

    private final Point q;

    public Arc(Point endPos, Point q) {
        super(endPos);
        this.q = q;
    }

    @Override
    public String toSvg() {
        return String.format("Q%d,%d %d,%d ", q.getX(), q.getY(),
                endPos.getX(), endPos.getY());
    }

    @Override
    public String toSvgReversed() {
        return String.format("Q%d,%d %d,%d ",
                Main.CONFIG.getPiece().getWidth() - q.getX(), q.getY(),
                Main.CONFIG.getPiece().getWidth() - startPos.getX(), startPos.getY());
    }

    @Override
    protected boolean intersectsLine(Line line) {
        return line.intersects(this);
    }

    @Override
    protected boolean intersectsArc(Arc arc) {
        return !arc.equals(this) || IntersectionUtil.arcToArcIntersection(arc, this);
    }
}
