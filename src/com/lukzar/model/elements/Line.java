package com.lukzar.model.elements;

import com.lukzar.Main;
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

    @Override
    public String toSvg() {
        return String.format("L%s ", endPos.toSvg());
    }

    @Override
    public String toSvgReversed() {
        return String.format("L%f,%f", Main.CONFIG.getPiece().getWidth() - startPos.getX(), startPos.getY());
    }

    @Override
    public List<Line> convertToLines() {
        return Collections.singletonList(this);
    }

}
