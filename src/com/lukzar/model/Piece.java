package com.lukzar.model;

import com.lukzar.config.Templates;
import com.lukzar.model.elements.Line;
import com.lukzar.model.elements.Part;
import com.lukzar.utils.IntersectionUtil;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Piece {

    private final Point start;
    private final LinkedList<Part> parts = new LinkedList<>();

    private boolean asymmetric;

    public Piece(Point start) {
        this.start = start;
    }

    public Piece(Point start, Piece piece) {
        this.start = start;
        this.parts.addAll(piece.getParts());
        this.asymmetric = piece.isAsymmetric();
    }
    public Piece(Piece piece) {
        this.start = piece.getStart();
        this.parts.addAll(piece.getParts());
        this.asymmetric = piece.isAsymmetric();
    }

    public String toSvg() {
        updateStartPoints();

        return String.format(Templates.getImageTemplate(),
                this.start.toSvg(),
                getAllParts().stream()
                        .map(Part::toSvg)
                        .collect(Collectors.joining("\n")), ""
//                FitnessUtil.getAttributes(this).stream()
//                        .map(s -> "<li>" + s + "</li>")
//                        .collect(Collectors.joining("\n"))
        );
    }

    public void convertToAsymmetric() {

        if (asymmetric) {
            return;
        }
        updateStartPoints();

        for (int i = this.parts.size() - 1; i >= 0; i--) {
            this.parts.add(this.parts.get(i).reverse());
        }

        this.asymmetric = true;
        updateStartPoints();
    }

    public LinkedList<Part> getSymmetricHalf() {
        if (asymmetric) {
            return new LinkedList<>();
        }
        updateStartPoints();

        LinkedList<Part> reversedParts = new LinkedList<>();

        for (int i = this.parts.size() - 1; i >= 0; i--) {
            reversedParts.add(this.parts.get(i).reverse());
        }
        return reversedParts;
    }

    public LinkedList<Part> getAllParts() {
        if (asymmetric) {
            return this.parts;
        }
        LinkedList<Part> result = new LinkedList<>(this.parts);
        result.addAll(getSymmetricHalf());
        updateStartPoints(result);
        return result;
    }

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
        updateStartPoints(this.parts);
    }

    public void updateStartPoints(LinkedList<Part> parts) {
        Point s = this.start;
        for (Part p : parts) {
            p.setStartPos(s);
            s = p.getEndPos();
        }
    }

    public List<Line> getAsLines() {
        return getAllParts().stream()
                .map(Part::convertToLines)
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

    public boolean isAsymmetric() {
        return asymmetric;
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
        return "com.lukzar.model.Piece(parts=" + this.getParts() + ")";
    }
}
