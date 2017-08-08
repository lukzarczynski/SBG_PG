package com.lukzar.model.elements;

import com.lukzar.model.Point;

/**
 * Created by lukasz on 04.06.17.
 */
public abstract class Part {

    protected final Point endPos;
    protected Point startPos;

    public Part(Point pos) {
        this.endPos = pos;
    }

    public Point getEndPos() {
        return endPos;
    }

    public Point getStartPos() {
        return startPos;
    }

    public void setStartPos(Point startPos) {
        this.startPos = startPos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Part part = (Part) o;

        return endPos.equals(part.endPos);
    }

    @Override
    public int hashCode() {
        return endPos.hashCode();
    }

    public abstract String toSvg();

    public abstract String toSvgReversed(Point startPoint, int middle_x);

}
