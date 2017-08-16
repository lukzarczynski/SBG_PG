package com.lukzar.model.elements;

import com.lukzar.model.Point;

import java.util.List;

/**
 * Created by lukasz on 04.06.17.
 */
public abstract class Part {

    final Point endPos;
    Point startPos;

    public Part(Point endPos) {
        this.endPos = endPos;
    }

    public Part(Point startPos, Point endPos) {
        this.endPos = endPos;
        this.startPos = startPos;
    }

    public abstract String toSvg();

    public abstract String toSvgReversed();

    public abstract List<Line> convertToLines();

    public Point getEndPos() {
        return this.endPos;
    }

    public Point getStartPos() {
        return this.startPos;
    }

    public void setStartPos(Point startPos) {
        this.startPos = startPos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Part part = (Part) o;

        if (!endPos.equals(part.endPos)) return false;
        return startPos != null ? startPos.equals(part.startPos) : part.startPos == null;
    }

    @Override
    public int hashCode() {
        int result = endPos.hashCode();
        result = 31 * result + (startPos != null ? startPos.hashCode() : 0);
        return result;
    }
}
