package com.lukzar.model.elements;

import com.lukzar.Main;
import com.lukzar.model.Point;
import com.lukzar.utils.IntersectionUtil;

/**
 * Created by lukasz on 28.05.17.
 */
public class Line extends Part {

    public Line(Point endPos) {
        super(endPos);
    }

    @Override
    public String toSvg() {
        return String.format("L%s ", endPos.toSvg());
    }

    @Override
    public String toSvgReversed() {
        return String.format("L%d,%d", Main.CONFIG.getPiece().getWidth() - startPos.getX(), startPos.getY());
    }

    @Override
    protected boolean intersectsLine(Line line) {
        return !this.equals(line)
                && IntersectionUtil.lineToLineIntersection(startPos, endPos, line.getStartPos(), line.getEndPos()).isPresent();
    }

    @Override
    protected boolean intersectsArc(Arc arc) {
        return !IntersectionUtil.lineToArcIntersection(startPos, endPos, arc).isEmpty();
    }


}
