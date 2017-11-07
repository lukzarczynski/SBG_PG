package com.lukzar.model;

import com.lukzar.config.Templates;
import com.lukzar.fitness.FitnessUtil;
import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.DoubleArc;
import com.lukzar.model.elements.Line;
import com.lukzar.model.elements.Part;
import com.lukzar.utils.IntersectionUtil;

import java.util.BitSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;

public class Piece {

    private final Point start;
    private final LinkedList<Part> parts = new LinkedList<>();
    private LinkedList<Part> symmetricHalf;

    private boolean asymmetric;

    // transient
    private double fitness = 0.0;
    private BitSet[] ray = null;

    public Piece(Point start) {
        this.start = start;
    }

    public Piece(Point start, Piece piece) {
        this.start = start;
        this.parts.addAll(piece.getParts());
        this.asymmetric = piece.isAsymmetric();
        this.fitness = piece.getFitness();
        this.ray = piece.getRay();
    }

    public Piece(Piece piece) {
        this(piece.getStart(), piece);
    }

    public String toSvg() {
        updateStartPoints();

        List<String> attributesDescription = FitnessUtil.getAttributesDescription(this);
        attributesDescription.add(0, "Fitness: " + FitnessUtil.calculateFitness(this));
        return String.format(Templates.getImageTemplate(),
                this.start.toSvg(),
                getAllParts().stream()
                        .map(Part::toSvg)
                        .collect(Collectors.joining("\n")),
                attributesDescription.stream()
                        .map(s -> "<li>" + s + "</li>")
                        .collect(Collectors.joining("\n"))
        );
    }

    public Piece scale(double scale) {
        Piece result = new Piece(scale(this.getStart(), scale));
        if (this.isAsymmetric()) {
            result.convertToAsymmetric();
        }

        for (Part part : this.getParts()) {
            if (part instanceof Line) {
                result.add(new Line(scale(part.getEndPos(), scale)));
            } else if (part instanceof Arc) {
                result.add(new Arc(
                        scale(part.getEndPos(), scale),
                        scale(((Arc) part).getQ(), scale)));
            } else if (part instanceof DoubleArc) {
                result.add(new DoubleArc(
                        scale(part.getEndPos(), scale),
                        scale(((DoubleArc) part).getQ1(), scale),
                        scale(((DoubleArc) part).getQ2(), scale))
                );
            }
        }
        return result;
    }

    private Point scale(Point p, double scale) {
        return Point.of(

                p.getX() * scale + (100 * (1.0 - scale)),
                p.getY() * scale + (200 * (1.0 - scale))
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
        if (nonNull(this.symmetricHalf)) {
            return this.symmetricHalf;
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

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
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
                    if (IntersectionUtil.linesIntersect(l1, l2)) {
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

    private void updateStartPoints(LinkedList<Part> parts) {
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


    public BitSet[] getRay() {
        return ray;
    }

    public void setRay(BitSet[] ray) {
        this.ray = ray;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Piece piece = (Piece) o;

        //return toSvg().equals(piece.toSvg());

        return asymmetric == piece.asymmetric
                && start.equals(piece.start)
                && parts.equals(piece.parts);
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + parts.hashCode();
        result = 31 * result + (asymmetric ? 1 : 0);
        return result;
    }

    public void update() {
        updateStartPoints();
        this.symmetricHalf = null;
        this.symmetricHalf = getSymmetricHalf();
    }
}
