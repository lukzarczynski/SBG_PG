package com.lukzar.model;

import com.lukzar.config.Templates;
import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.Line;
import com.lukzar.model.elements.Part;
import com.lukzar.utils.IntersectionUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.lukzar.Main.CONFIG;

/**
 * Created by lukasz on 25.05.17.
 */
public class Piece {

    private final LinkedList<Part> parts = new LinkedList<>();

    public LinkedList<Part> getParts() {
        return parts;
    }

    public String toSvg() {
        StringBuilder reverse = new StringBuilder("\n");

        final Point endPoint = Point.of(CONFIG.getPiece().getWidth() - CONFIG.getPiece().getStart().getX(),
                CONFIG.getPiece().getStart().getY());

        final List<Point> p = new ArrayList<>();
        p.add(CONFIG.getPiece().getStart());
        parts.forEach(a -> p.add(a.getEndPos()));
        p.add(endPoint);
        for (int i = parts.size() - 1; i >= 0; i--) {
            reverse.append(parts.get(i).toSvgReversed(p.get(i), CONFIG.getPiece().getWidth() / 2)).append("\n");
        }
        return String.format(Templates.getImageTemplate(),
                CONFIG.getPiece().getStart().toSvg(),
                parts.stream().map(Part::toSvg)
                        .collect(Collectors.joining("\n")),
                reverse.toString());
    }


    /**
     * @return return true when part p intersects with any other parts
     */
    public boolean intersectsWithAny(Part p) {
        return intersectsWithAny(p, parts.size());
    }

    public boolean intersectsWithAny(Part p, int i) {
        if (parts.isEmpty() || i == 0) {
            return false;
        }
        final Point startPos = parts.get(i - 1).getEndPos();
        if (p instanceof Line) {
            final Line line = (Line) p;
            for (Part part : parts) {
                if (part.equals(p)) {
                    return false;
                }
                if (part instanceof Line) {
                    if (IntersectionUtil.lineToLineIntersection(startPos, line.getEndPos(),
                            part.getStartPos(), part.getEndPos()).isPresent()) {
                        return true;
                    }
                } else {
                    if (!IntersectionUtil.lineToArcIntersection(startPos, line.getEndPos(), part.getStartPos(), (Arc) part).isEmpty()) {
                        return true;
                    }
                }
            }
        } else {
            Arc arc = (Arc) p;
            for (Part part : parts) {
                if (part.equals(p)) {
                    return false;
                }
                if (part instanceof Line) {
                    if (!IntersectionUtil.lineToArcIntersection(part.getStartPos(), part.getEndPos(), startPos, arc).isEmpty()) {
                        return true;
                    }
                } else {
                    if (IntersectionUtil.arcToArcIntersection(startPos, arc, part.getStartPos(), (Arc) part)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean intersects() {
        return IntStream.range(0, parts.size())
                .anyMatch(i -> intersectsWithAny(parts.get(i), i));
    }

    public void updateStartPoints() {
        Point s = CONFIG.getPiece().getStart();
        for (Part p : parts) {
            p.setStartPos(s);
            s = p.getEndPos();
        }
    }
}
