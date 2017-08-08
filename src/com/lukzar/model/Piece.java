package com.lukzar.model;

import com.lukzar.config.Templates;
import com.lukzar.model.elements.Part;

import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Value;

import static com.lukzar.Main.CONFIG;

@Value
public class Piece {

    LinkedList<Part> parts = new LinkedList<>();

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
            if (part.intersects(partToAdd)) {
                return true;
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
