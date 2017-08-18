package com.lukzar.model.elements;

import com.lukzar.config.Configuration;
import com.lukzar.model.Point;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
    public String toSvgReversed() {
        return String.format(Locale.US, "L%f,%f", Configuration.Piece.WIDTH - startPos.getX(), startPos.getY());
    }

    @Override
    public List<Line> convertToLines() {
        return Collections.singletonList(this);
    }


}
