package com.lukzar.services;

import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.Part;
import com.lukzar.exceptions.IntersectsException;
import com.lukzar.fitness.FitnessUtil;
import com.lukzar.model.Piece;
import com.lukzar.model.Point;
import com.lukzar.utils.RandomUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import static com.lukzar.Main.CONFIG;

/**
 * Created by lukasz on 16.07.17.
 */
public class Evolution {

    private static final Comparator<Piece> FITNESS_COMPARATOR = Comparator
            .comparing(FitnessUtil::calculateFitness).reversed();

    private List<Piece> population = new ArrayList<>();

    public List<Piece> getPopulation() {
        return population;
    }

    public void initialize() {
        while (population.size() < CONFIG.getEvolution().getInitialSize()) {
            try {
                population.add(PieceGenerator.generate());
            } catch (IntersectsException ignored) {
            }
        }
    }

    // Evolve a population
    public void evolvePopulation() {
        for (int i = 0; i < CONFIG.getEvolution().getCrossoverSize(); i++) {
            population.add(crossover(tournamentSelection(), tournamentSelection()));
        }

        // Mutate population
        population.forEach(this::mutate);
        population.forEach(Piece::updateStartPoints);
        population.removeIf(Piece::intersects);
        population.sort(FITNESS_COMPARATOR);
        if (population.size() > CONFIG.getEvolution().getInitialSize()) {
            population.subList(CONFIG.getEvolution().getInitialSize(), population.size()).clear();
        }
    }

    private void mutate(Piece piece) {
        for (Part part : piece.getParts()) {
            if (Math.random() <= CONFIG.getEvolution().getMutationRate()) {
                mutate(part.getEndPos(), !part.equals(piece.getParts().peekLast()));
                if (part instanceof Arc) {
                    mutate(((Arc) part).getQ(), true);
                }
            }
        }
        piece.updateStartPoints();
    }

    private void mutate(Point point, boolean mutateX) {
        point.setY(RandomUtils.randomRange(
                point.getY() - CONFIG.getEvolution().getMutationOffset(),
                point.getY() + CONFIG.getEvolution().getMutationOffset()
        ));

        if (mutateX) {
            point.setX(RandomUtils.randomRange(
                    point.getX() - CONFIG.getEvolution().getMutationOffset(),
                    point.getX() + CONFIG.getEvolution().getMutationOffset()
            ));
        }
    }

    private Piece crossover(Piece piece1, Piece piece2) {
        Piece newSol = new Piece();
        for (int i = 0; i < Math.ceil(piece1.getParts().size() / 2); i++) {
            newSol.getParts().add(piece1.getParts().get(i));
        }
        for (int i = piece2.getParts().size() / 2; i < piece2.getParts().size(); i++) {
            newSol.getParts().add(piece2.getParts().get(i));
        }
        newSol.updateStartPoints();
        return newSol;
    }

    // Select individuals for crossover
    private Piece tournamentSelection() {
        final TreeSet<Piece> tournament = new TreeSet<>(FITNESS_COMPARATOR);
        for (int i = 0; i < CONFIG.getEvolution().getTournamentSize(); i++) {
            int randomId = (int) (Math.random() * population.size());
            tournament.add(population.get(randomId));
        }
        return tournament.first();
    }

}
