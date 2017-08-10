package com.lukzar.model.elements;

import com.lukzar.Main;
import com.lukzar.model.Point;
import com.lukzar.utils.BezierUtil;

import java.util.List;

import lombok.Getter;

/**
 * Created by lukasz on 04.06.17.
 */
@Getter
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
                Main.CONFIG.getPiece().getWidth() - q2.getX(), q2.getY(),
                Main.CONFIG.getPiece().getWidth() - q1.getX(), q1.getY(),
                Main.CONFIG.getPiece().getWidth() - startPos.getX(), startPos.getY());
    }

    @Override
    public List<Line> convertToLines() {
        return BezierUtil.convertToLine(this);
    }

}
