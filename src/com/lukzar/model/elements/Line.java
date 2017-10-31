package com.lukzar.model.elements;

import com.lukzar.config.Configuration;
import com.lukzar.model.Point;

import java.util.Collections;
import java.util.List;

/**
 * Created by lukasz on 28.05.17.
 */
public class Line extends Part {

    public Line(Point endPos) {
        super(endPos);
    }

    public Line(Point startPos, Point endPos) {
        super(startPos, endPos);
    }

    @Override
    public String toSvg() {
        return String.format("L%s ", endPos.toSvg());
    }

    @Override
    public Line reverse() {
        double x = Configuration.Piece.WIDTH - startPos.getX();
        double y = startPos.getY();
        return new Line(Point.of(x, y));
    }

    @Override
    public List<Line> convertToLines() {
        return Collections.singletonList(this);
    }

}
