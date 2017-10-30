package com.lukzar.services;

import com.lukzar.config.Configuration;
import com.lukzar.fitness.FitnessUtil;
import com.lukzar.model.Piece;
import com.lukzar.model.Point;
import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.DoubleArc;
import com.lukzar.model.elements.Line;
import com.lukzar.model.elements.Part;
import com.lukzar.utils.RandomUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by lukasz on 16.07.17.
 */
public class Evolution {

    private static final double change_starting_point = 0.1;

    private static final double change_point = 0.6;
    private static final double change_part = 0.38;
//    change to assymetric = 1.0 - change_point - change_part;

    private static final int crossover_size = 20;
    private static final int maximum_population_size = 200;
    private static final int initial_population_size = 10;


    private static final Comparator<Piece> FITNESS_COMPARATOR = (a, b) -> Double.compare(b.getFitness(), a.getFitness());

    private TreeSet<Piece> population = new TreeSet<>(FITNESS_COMPARATOR);

    public TreeSet<Piece> getPopulation() {
        return population;
    }

    public void initialize() {
        /*
        while (population.size() < Configuration.Evolution.INITIAL_SIZE) {
            try {
                population.add(PieceGenerator.generate());
            } catch (IntersectsException ignored) {
            }
        }
        */
        for (int i = 0; i < initial_population_size; i++) {
            Piece triangle = new Piece(Point.of(150, 200));
            triangle.add(new Line(Point.of(100, 50 - Math.random())));
            triangle.setFitness(FitnessUtil.calculateFitness(triangle));
            population.add(triangle);
        }

    }

    // Evolve a population
    public void evolvePopulation() {
        Set<Piece> newPopulation = new HashSet<>();

        for (int i = 0; i < Math.min(crossover_size, population.size() / 2); i++) {
            final List<Piece> crossover = crossover(tournamentSelection(), tournamentSelection());
            crossover
                    .stream()
                    .peek(Piece::updateStartPoints)
                    .map(this::mutate)
                    .forEach(newPopulation::add);
        }

        // Mutate population
        for (Piece piece : population) {
            Piece mutate = mutate(piece);
            newPopulation.add(mutate);
        }

        if (!Configuration.ALLOW_INTERSECTIONS) {
            newPopulation.removeIf(Piece::intersects);
        }
        newPopulation.removeIf(p -> FitnessUtil.getMinDegree(p) < Configuration.Piece.MIN_DEGREE);
        newPopulation.forEach(Piece::update);
        newPopulation.forEach(p -> p.setFitness(FitnessUtil.calculateFitness(p)));

        newPopulation.removeIf(p -> Double.isNaN(p.getFitness()));
        newPopulation.removeIf(p -> Double.isInfinite(p.getFitness()));

        population.addAll(newPopulation);
        while (population.size() > maximum_population_size) {
            population.remove(population.last());
        }
    }

    private Piece mutate(Piece piece) {
        double random = Math.random();

        Point startPoint;
        if (random < change_starting_point) {
            startPoint = Point.of(
                    RandomUtils.ensureRange(RandomUtils.randomRange(
                            piece.getStart().getX() - Configuration.Evolution.MUTATION_OFFSET,
                            piece.getStart().getX() + Configuration.Evolution.MUTATION_OFFSET),
                            110, 190),
                    Configuration.Piece.HEIGHT
            );
        } else {
            startPoint = piece.getStart();
        }

        final Piece result = new Piece(startPoint, piece);

        final Point minBound = Point.of(result.isAsymmetric() ? 0 : Configuration.Piece.WIDTH / 2.0, 0);
        final Point maxBound = Point.of(Configuration.Piece.WIDTH, Configuration.Piece.HEIGHT);


        int partsSize = result.getParts().size();
        int partToChange = RandomUtils.randomRange(0, partsSize - 1);
        final Part part = result.getParts().get(partToChange);

        final Part newPart;

        if (random <= change_point) {
            // change points


            newPart = PointMutation.mutate(part, minBound, maxBound);
            newPart.setStartPos(part.getStartPos());
            result.getParts().set(partToChange, newPart);

        } else if (random <= change_point + change_part) {
            // change part
            List<Part> mutate = PartMutation.mutate(part);
            result.getParts().remove(partToChange);
            for (int i = 0; i < mutate.size(); i++) {
                result.getParts().add(partToChange + i, mutate.get(i));
            }
        } else {
            // convert to asymmetric
            result.convertToAsymmetric();
        }

        result.updateStartPoints();

        fixStartAndEndPoints(result);

        return result;
    }

    private void fixStartAndEndPoints(Piece result) {
        final Part first = result.getParts().getFirst();
        if (Double.compare(first.getStartPos().getY(), Configuration.Piece.HEIGHT) != 0) {
            first.setStartPos(Point.of(
                    RandomUtils.ensureRange(first.getStartPos().getX(), Configuration.Piece.WIDTH / 200, Configuration.Piece.WIDTH),
                    Configuration.Piece.HEIGHT));
        }

        final Part last = result.getParts().getLast();
        if (!result.isAsymmetric()) {
            double x = Configuration.Piece.WIDTH / 2.0;
            last.setEndPos(Point.of(x, last.getEndPos().getY()));
        } else {
            double y = Configuration.Piece.HEIGHT;
            last.setEndPos(Point.of(last.getEndPos().getX(), y));
        }
        result.updateStartPoints();
    }

    private List<Piece> crossover(Piece piece1, Piece piece2) {
        return Arrays.asList(
                produceChild(piece1, piece2),
                produceChild(piece2, piece1));
    }

    private Piece produceChild(Piece piece1, Piece piece2) {
        final Piece child = new Piece(piece1.getStart());

        List<Part> left;
        List<Part> right;

        if (piece1.isAsymmetric() || piece2.isAsymmetric()) {
            left = piece1.getAllParts();
            right = piece2.getAllParts();
            child.convertToAsymmetric();
        } else {
            left = piece1.getParts();
            right = piece2.getParts();
        }

        for (int i = 0; i < Math.ceil(left.size() / 2); i++) {
            child.add(left.get(i));
        }

        for (int i = right.size() / 2; i < right.size(); i++) {
            child.add(right.get(i));
        }

        child.updateStartPoints();
        return child;
    }

    // Select individuals for crossover
    // BEST out of 5 random
    private Piece tournamentSelection() {
        final TreeSet<Piece> tournament = new TreeSet<>(FITNESS_COMPARATOR);
        for (int i = 0; i < Configuration.Evolution.TOURNAMENT_SIZE; i++) {
            int randomId = (int) (Math.random() * population.size());
            Iterator<Piece> it = population.iterator();
            Piece p = null;
            for (int j = 0; j <= randomId; j++) {
                if (it.hasNext()) {
                    p = it.next();
                }
            }
            tournament.add(p);
        }
        return tournament.first();
    }

    private static final class PartMutation {

        static List<Part> mutate(Part part) {
            if (part instanceof Line) {
                return mutate((Line) part);
            } else if (part instanceof Arc) {
                return Collections.singletonList(mutate((Arc) part));
            } else if (part instanceof DoubleArc) {
                // do nothing
                return Collections.singletonList(part);
            }
            throw new RuntimeException("New part type?");
        }

        static List<Part> mutate(Line line) {
            double random = Math.random();

            final Point start = line.getStartPos();
            final Point end = line.getEndPos();
            final Point middle = Point.of(
                    (start.getX() + end.getX()) / 2.0,
                    (start.getY() + end.getY()) / 2.0);

            if (random < 0.8) {
                // split line
                return Arrays.asList(
                        new Line(start, middle),
                        new Line(middle, end)
                );
            } else {
                // convert Line to Arc
                double newX = RandomUtils.randomRange(middle.getX() - 5, middle.getX() + 5);
                double newY = RandomUtils.randomRange(middle.getY() - 5, middle.getY() + 5);
                return Collections.singletonList(new Arc(end, Point.of(newX, newY)));
            }
        }

        static Part mutate(Arc arc) {
            final Point end = arc.getEndPos();

            return new DoubleArc(end, arc.getQ(), arc.getQ());
        }
    }

    private static final class PointMutation {

        static Part mutate(Part part, Point min, Point max) {
            if (part instanceof Line) {
                return mutate((Line) part, min, max);
            } else if (part instanceof Arc) {
                return mutate((Arc) part, min, max);

            } else if (part instanceof DoubleArc) {
                return mutate((DoubleArc) part, min, max);
            }
            throw new RuntimeException("New part type?");
        }

        static Line mutate(Line line, Point min, Point max) {
            return new Line(line.getStartPos(), mutate(line.getEndPos(), min, max));
        }

        static Arc mutate(Arc part, Point min, Point max) {
            if (Math.random() <= 0.5) {
                return new Arc(mutate(part.getEndPos(), min, max), part.getQ());
            } else {
                return new Arc(part.getEndPos(), mutate(part.getQ(), min, max));
            }
        }

        static DoubleArc mutate(DoubleArc part, Point min, Point max) {
            double random = Math.random();
            if (random <= 0.33) {
                return new DoubleArc(mutate(part.getEndPos(), min, max), part.getQ1(), part.getQ2());
            } else if (random <= 0.66) {
                return new DoubleArc(part.getEndPos(), mutate(part.getQ1(), min, max), part.getQ2());
            } else {
                return new DoubleArc(part.getEndPos(), part.getQ1(), mutate(part.getQ2(), min, max));
            }
        }

        static Point mutate(Point point, Point min, Point max) {
            double newX = RandomUtils.randomRange(
                    point.getX() - Configuration.Evolution.MUTATION_OFFSET,
                    point.getX() + Configuration.Evolution.MUTATION_OFFSET);
            double newY = RandomUtils.randomRange(
                    point.getY() - Configuration.Evolution.MUTATION_OFFSET,
                    point.getY() + Configuration.Evolution.MUTATION_OFFSET);

            return Point.of(
                    RandomUtils.ensureRange(newX, min.getX(), max.getX()),
                    RandomUtils.ensureRange(newY, min.getY(), max.getY())
            );
        }

    }

}
