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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by lukasz on 16.07.17.
 */
public class Evolution {

    public static final double changePoints = 0.7;
    public static final double changeParts = 0.25;
    public static final double assymetric = 1.0 - changePoints - changeParts;

    public static final Comparator<Piece> FITNESS_COMPARATOR = (a, b) -> {
        double d1 = FitnessUtil.calculateFitness(b);
        double d2 = FitnessUtil.calculateFitness(a);
        return Double.compare(d1, d2);
    };

    private List<Piece> population = new ArrayList<>();


    public List<Piece> getPopulation() {
        return population;
    }

    //    public void initialize() {
//        while (population.size() < Configuration.Evolution.INITIAL_SIZE) {
//            try {
//                population.add(PieceGenerator.generate());
//            } catch (IntersectsException ignored) {
//            }
//        }
//    }
    public void initialize() {
        while (population.size() < 10) {
            Piece triangle = new Piece(Point.of(200, 200));
            triangle.add(new Line(Point.of(100, 0)));
            population.add(triangle);
        }
    }

    // Evolve a population
    public void evolvePopulation() {
        List<Piece> newPopulation = new ArrayList<>();
//
//        for (int i = 0; i < 5; i++) {
//            final List<Piece> crossover = crossover(tournamentSelection(), tournamentSelection());
//            crossover
//                    .stream()
//                    .peek(Piece::updateStartPoints)
//                    .map(this::mutate)
//                    .forEach(newPopulation::add);
//        }

        // Mutate population
        population.stream()
                .map(this::mutate)
                .forEach(newPopulation::add);

        if (!Configuration.ALLOW_INTERSECTIONS) {
            newPopulation.removeIf(Piece::intersects);
        }
        newPopulation.removeIf(p -> FitnessUtil.getMinDegree(p) < Configuration.Piece.MIN_DEGREE);
        population.addAll(newPopulation);
        population.sort(FITNESS_COMPARATOR);
        if (population.size() > 100) {
            population.subList(100, population.size()).clear();
        }
    }

    private Piece mutate(Piece piece) {
        final Piece result = new Piece(piece);

        final Point minBound = Point.of(result.isAsymmetric() ? 0 : Configuration.Piece.WIDTH / 2.0, 0);
        final Point maxBound = Point.of(Configuration.Piece.WIDTH, Configuration.Piece.HEIGHT);

        double random = Math.random();

        int partsSize = result.getParts().size();
        int partToChange = RandomUtils.randomRange(0, partsSize - 1);
        final Part part = result.getParts().get(partToChange);

        final Part newPart;

        if (random <= changePoints) {
            // change points
            newPart = PointMutation.mutate(part, minBound, maxBound);
            result.getParts().set(partToChange, newPart);

        } else if(random <= changePoints + changeParts) {
            // change part
            List<Part> mutate = PartMutation.mutate(part);
            result.getParts().remove(partToChange);
            for (int i = 0; i < mutate.size(); i++) {
                result.getParts().add(partToChange + i, mutate.get(i));
            }
        } else {
            System.out.println("converting to asymmetric");
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
            first.setStartPos(Point.of(first.getStartPos().getX(), Configuration.Piece.HEIGHT));
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

        for (int i = 0; i < Math.ceil(piece1.getParts().size() / 2); i++) {
            child.add(piece1.getParts().get(i));
        }
        for (int i = piece2.getParts().size() / 2; i < piece2.getParts().size(); i++) {
            child.add(piece2.getParts().get(i));
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
            tournament.add(population.get(randomId));
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
