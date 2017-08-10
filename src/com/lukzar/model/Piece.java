package com.lukzar.model;

import com.lukzar.config.Templates;
import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.Line;
import com.lukzar.model.elements.Part;
import com.lukzar.utils.BezierUtil;
import com.lukzar.utils.IntersectionUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Value;

import static com.lukzar.Main.CONFIG;

@Value
public class Piece {

    LinkedList<Part> parts = new LinkedList<>();
    Map<Part, List<Line>> convertedToLines = new HashMap<>();

    public String toSvg() {
        StringBuilder reverse = new StringBuilder("\n");

        updateStartPoints();

        parts.descendingIterator()
                .forEachRemaining(p -> reverse.append(p.toSvgReversed()).append("\n"));

        return String.format(Templates.getImageTemplate(),
                CONFIG.getPiece().getStart().toSvg(),
                parts.stream()
                        .map(Part::toSvg)
                        .collect(Collectors.joining("\n")),
                reverse.toString());
    }

    /**
     * @return return true when part p intersects with any other parts
     */
    public boolean intersectsWithAny(Part p) {
        return intersectsWithAny(p, parts.size());
    }

    private boolean intersectsWithAny(Part partToAdd, int until) {
        updateStartPoints();
        if (parts.isEmpty() || until == 0) {
            return false;
        }
        partToAdd.setStartPos(parts.get(until - 1).getEndPos());

        for (int i = 0; i < until; i++) {
            final Part part = parts.get(i);
            for (Line l1 : part.convertToLines()) {
                for (Line l2 : partToAdd.convertToLines()) {
                    if (IntersectionUtil.lineToLineIntersection(l1, l2).isPresent()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public boolean intersects() {
        updateStartPoints();
        return IntStream.range(0, parts.size())
                .anyMatch(i -> intersectsWithAny(parts.get(i), i));
    }

    public void updateStartPoints() {
        Point s = CONFIG.getPiece().getStart();
        for (Part p : parts) {
            p.setStartPos(s);
            s = p.getEndPos();
            convertedToLines.computeIfAbsent(p, Part::convertToLines);
        }
    }

    public List<Line> getConverted() {
        updateStartPoints();
        return getParts().stream().map(convertedToLines::get)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }


}
