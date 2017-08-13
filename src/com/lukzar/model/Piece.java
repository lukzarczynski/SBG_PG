package com.lukzar.model;

import com.lukzar.config.Configuration;
import com.lukzar.config.Templates;
import com.lukzar.fitness.FitnessUtil;
import com.lukzar.model.elements.Line;
import com.lukzar.model.elements.Part;
import com.lukzar.utils.IntersectionUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Piece {

    private final LinkedList<Part> parts = new LinkedList<>();
    private final Map<Part, List<Line>> convertedToLines = new HashMap<>();

    public String toSvg() {
        StringBuilder reverse = new StringBuilder("\n");

        updateStartPoints();

        parts.descendingIterator()
                .forEachRemaining(p -> reverse.append(p.toSvgReversed()).append("\n"));

        return String.format(Templates.getImageTemplate(),
                Configuration.Piece.START.toSvg(),
                parts.stream()
                        .map(Part::toSvg)
                        .collect(Collectors.joining("\n")),
                reverse.toString(),
                FitnessUtil.getAttributes(this).stream()
        .map(s -> "<li>" + s + "</li>")
        .collect(Collectors.joining("\n")));
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
        Point s = Configuration.Piece.START;
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


    public LinkedList<Part> getParts() {
        return this.parts;
    }

    public Map<Part, List<Line>> getConvertedToLines() {
        return this.convertedToLines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Piece piece = (Piece) o;

        if (!parts.equals(piece.parts)) return false;
        return convertedToLines.equals(piece.convertedToLines);
    }

    @Override
    public int hashCode() {
        int result = parts.hashCode();
        result = 31 * result + convertedToLines.hashCode();
        return result;
    }

    public String toString() {
        return "com.lukzar.model.Piece(parts=" + this.getParts() + ", convertedToLines=" + this.getConvertedToLines() + ")";
    }
}
