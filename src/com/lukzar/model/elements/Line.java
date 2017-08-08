package com.lukzar.model.elements;

import com.lukzar.model.Point;

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
    public String toSvgReversed(Point startPoint, int middle_x) {
        return String.format("L%d,%d", 2 * middle_x - startPoint.getX(), startPoint.getY());
    }

}
