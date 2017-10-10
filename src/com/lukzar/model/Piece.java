package com.lukzar.model;

import com.lukzar.config.Templates;
import com.lukzar.fitness.FitnessUtil;
import com.lukzar.model.elements.Line;
import com.lukzar.model.elements.Part;
import com.lukzar.utils.IntersectionUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Piece {

    private final Point start;
    private final LinkedList<Part> parts = new LinkedList<>();
    private final Map<Part, List<Line>> convertedToLines = new HashMap<>();
    private boolean asymmetric;

    public Piece(Point start) {
        this.start = start;
    }

    public String toSvg() {
        return asymmetric ? toSvgAsymmetric() : toSvgSymmetric();
    }

    private String toSvgSymmetric() {
        StringBuilder reverse = new StringBuilder("\n");

        updateStartPoints();

        parts.descendingIterator()
                .forEachRemaining(p -> reverse.append(p.toSvgReversed()).append("\n"));

        return String.format(Templates.getImageTemplate(),
                this.start.toSvg(),
                parts.stream()
                        .map(Part::toSvg)
                        .collect(Collectors.joining("\n")),
                reverse.toString(),
                FitnessUtil.getAttributes(this).stream()
                        .map(s -> "<li>" + s + "</li>")
                        .collect(Collectors.joining("\n")));
    }

    private String toSvgAsymmetric() {
        updateStartPoints();

        return String.format(Templates.getImageTemplate(),
                this.start.toSvg(),
                parts.stream()
                        .map(Part::toSvg)
                        .collect(Collectors.joining("\n")),
                "  \n",
                FitnessUtil.getAttributes(this).stream()
                        .map(s -> "<li>" + s + "</li>")
                        .collect(Collectors.joining("\n")));
    }

    public void convertToAsymmetric() {

        if (asymmetric) {
            return;
        }

        LinkedList<Part> reversedParts = new LinkedList<>();
        this.parts.descendingIterator().forEachRemaining(p -> reversedParts.add(p.reverse()));

        this.parts.addAll(reversedParts);
        updateStartPoints();
        this.asymmetric = true;
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
        return IntStream.range(0, parts.size())
                .anyMatch(i -> intersectsWithAny(parts.get(i), i));
    }

    public void updateStartPoints() {
        Point s = this.start;
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

    public Point getStart() {
        return start;
    }

    public LinkedList<Part> getParts() {
        return this.parts;
    }

    public void add(Part part) {
        this.parts.add(part);
        part.setStartPos(this.parts.isEmpty()
                ? this.start
                : this.parts.getLast().getEndPos());
    }

    public void addAll(List<? extends Part> parts) {
        parts.forEach(this::add);
    }

    public Map<Part, List<Line>> getConvertedToLines() {
        return this.convertedToLines;
    }

    public boolean isAsymmetric() {
        return asymmetric;
    }

    public void setAsymmetric(boolean asymmetric) {
        this.asymmetric = asymmetric;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Piece piece = (Piece) o;

        return start.equals(piece.start) && parts.equals(piece.parts);
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + parts.hashCode();
        return result;
    }

    public String toString() {
        return "com.lukzar.model.Piece(parts=" + this.getParts() + ", convertedToLines=" + this.getConvertedToLines() + ")";
    }
}
