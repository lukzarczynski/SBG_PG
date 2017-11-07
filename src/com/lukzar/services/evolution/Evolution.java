package com.lukzar.services.evolution;

import com.lukzar.config.Configuration;
import com.lukzar.exceptions.IntersectsException;
import com.lukzar.fitness.FitnessUtil;
import com.lukzar.model.Piece;
import com.lukzar.model.Point;
import com.lukzar.model.elements.Part;
import com.lukzar.services.PieceGenerator;
import com.lukzar.utils.RandomUtils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by lukasz on 16.07.17.
 */
public abstract class Evolution {

    private static final Comparator<Piece> FITNESS_COMPARATOR = Comparator.comparingDouble(Piece::getFitness);
    // note - min ordering

    public static Collection<Piece> initialize(int size) {
        final Collection<Piece> population = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            switch (Configuration.INIT_POP_SHAPE) {
                case pawn:
                    population.add(PieceGenerator.pawn());
                    break;
                case triangle:
                    population.add(PieceGenerator.triangle());
                    break;
                case random:
                    try {
                        population.add(PieceGenerator.random());
                    } catch (IntersectsException ignored) {
                    }
                    break;
            }
        }

        return population;
    }

    // Evolve a population
    public static Collection<Piece> evolvePopulation(Collection<Piece> input) {
        final Set<Piece> newPopulation = new HashSet<>();

        for (int i = 0; i < Math.min(Configuration.Evolution.CROSSOVER_SIZE, input.size() / 2); i++) {
            final List<Piece> crossover = crossover(tournamentSelection(input), tournamentSelection(input));
            crossover
                    .stream()
                    .peek(Piece::updateStartPoints)
                    .map(Evolution::mutate)
                    .forEach(newPopulation::add);
        }

        // Mutate population
        for (Piece piece : input) {
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
        return newPopulation;

    }

    public static List<Piece> collectNewGeneration(final Collection<Piece> oldGeneration,
                                                   final Collection<Piece> newGeneration) {
        List<Piece> result = new LinkedList<>();
        result.addAll(oldGeneration);
        result.addAll(newGeneration);
        result.sort(FITNESS_COMPARATOR);
        removeDuplicates(result);
        if (result.size() > Configuration.Evolution.MAXIMUM_POPULATION_SIZE) {
            result.subList(Configuration.Evolution.MAXIMUM_POPULATION_SIZE, result.size()).clear();
        }
        return result;
    }

    private static void removeDuplicates(List<Piece> pieces) {
        int i = 0;
        while (i < pieces.size()) {
            final Piece best = pieces.get(i);
            pieces.subList(i + 1, pieces.size()).removeIf(p ->
            {
                double v = FitnessUtil.overlapRatio(best, p);
                return v > Configuration.MAXIMUM_SIMILARITY;
            });
            i++;
        }
    }

    private static Piece mutate(Piece piece) {
        double random = Math.random();

        Point startPoint;
        if (random < Configuration.Evolution.Mutation.STARTING_POINT_CHANCE) {
            startPoint = Point.of(
                    RandomUtils.ensureRange(RandomUtils.randomRange(
                            piece.getStart().getX() - Configuration.Evolution.Mutation.OFFSET,
                            piece.getStart().getX() + Configuration.Evolution.Mutation.OFFSET),
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

        if (random <= Configuration.Evolution.Mutation.CHANCE_TO_CHANGE_POINT) {
            // change points


            newPart = PointMutation.mutate(part, minBound, maxBound);
            newPart.setStartPos(part.getStartPos());
            result.getParts().set(partToChange, newPart);

        } else if (random <= Configuration.Evolution.Mutation.CHANCE_TO_CHANGE_POINT + Configuration.Evolution.Mutation.CHANCE_TO_CHANGE_PART) {
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

    private static void fixStartAndEndPoints(Piece result) {
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

    private static List<Piece> crossover(Piece piece1, Piece piece2) {
        return Arrays.asList(
                produceChild(piece1, piece2),
                produceChild(piece2, piece1));
    }

    private static Piece produceChild(Piece piece1, Piece piece2) {
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
    private static Piece tournamentSelection(Collection<Piece> input) {
        final List<Piece> pieces = new ArrayList<>(input);
        final TreeSet<Piece> tournament = new TreeSet<>(FITNESS_COMPARATOR);
        for (int i = 0; i < Configuration.Evolution.TOURNAMENT_SIZE; i++) {
            tournament.add(pieces.get(new Random().nextInt(input.size())));
        }
        return tournament.first();
    }

}
