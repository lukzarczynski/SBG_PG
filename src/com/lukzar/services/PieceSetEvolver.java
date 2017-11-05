package com.lukzar.services;

import com.lukzar.Main;
import com.lukzar.config.Configuration;
import com.lukzar.fitness.FitnessUtil;
import com.lukzar.model.Piece;
import com.lukzar.services.evolution.Evolution;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Kot on 2017-10-31.
 */
public class PieceSetEvolver {

    public static List<Piece> SimpleGeneration(String target,
                                               int generations,
                                               int populationSize,
                                               int initialPopulationSize,
                                               String subdir)
            throws IOException {

        final Collection<Piece> initialize = Evolution.initialize(initialPopulationSize);
        System.out.println("Initial population size: " + initialize.size());

        Main.writeToFile(initialize,
                String.format("out%s/%s-%s_%s",
                        subdir==null?"":"/"+subdir,
                        target,
                        Configuration.InitPopShapeStr(),
                        0));

        return SimpleGeneration(target, generations, populationSize, initialize, subdir);
    }

    public static List<Piece> SimpleGeneration(final String target,
                                               final int generations,
                                               final int populationSize,
                                               final Collection<Piece> startPopulation,
                                               String subdir) throws IOException {
        Configuration.TARGET_PIECE = target;
        Configuration.NUMBER_OF_GENERATIONS = generations;
        Configuration.Evolution.MAXIMUM_POPULATION_SIZE = populationSize;

        List<Piece> population = null;
        Collection<Piece> oldGeneration = startPopulation;

        for (int i = 1; i <= Configuration.NUMBER_OF_GENERATIONS; i++) {
            final Collection<Piece> newGeneration = Evolution.evolvePopulation(oldGeneration);
            population = Evolution.collectNewGeneration(oldGeneration, newGeneration);
            oldGeneration = population;

            System.out.println("Population " + i + " size: " + population.size());
            Main.writeToFile(population,
                    String.format("out%s/%s-%s_%s",
                            subdir==null?"":"/"+subdir,
                            target,
                            Configuration.InitPopShapeStr(),
                            i));
        }

        return population;
    }

    public static Collection<Piece> EvolverPlusPicker(String target,
                                                      int generations,
                                                      int populationSize,
                                                      int initPopulationSize,
                                                      String pickerPieces,
                                                      String testname) throws IOException {
        String subdir = String.format("PlusPicker-%s_%s-%s_%d-%d%s",
                Configuration.InitPopShapeStr(),
                target,
                pickerPieces,
                generations,
                populationSize,
                testname == null ? "" : ("_" + testname));

        File directory = new File("out/"+subdir);
        if (! directory.exists()){
            directory.mkdir();
        }

        final Collection<Piece> chosen = new ArrayList<>();
        final HashMap<Piece, String> chosenNames = new HashMap<>();
        final String[] piecesToPick = pickerPieces.split(";");

        final List<Piece> finalPopulation = SimpleGeneration(target, generations, populationSize, initPopulationSize, subdir);

        final Piece best = finalPopulation.get(0);
        chosen.add(best);
        chosenNames.put(best, target);

        final HashSet<Piece> picked = new HashSet<>();

        for (String subtarget : piecesToPick) {
            Configuration.TARGET_PIECE = subtarget;

            final Collection<Piece> copy = finalPopulation.stream()
                    .peek(p -> p.setFitness(FitnessUtil.calculateFitness(p)))
                    .collect(Collectors.toList());
            final List<Piece> pieces = Evolution.collectNewGeneration(copy, Collections.emptyList());

            Piece subBest = null;
            int skipped = 0;
            for (Piece p : pieces) {
                if (!picked.contains(p)) {
                    subBest = p;
                    break;
                }
                skipped++;
            }

            //Evolution.writeToFile(subEvo.getPopulation(), String.format("out/XXXX-%s", subtarget));
            System.out.println(subtarget + " " + pieces.get(0).getFitness() + " (" + skipped + " skipped)\t\t\t//  " + subBest);

            // todo - trzeba będzie sprawdzać podobieństwo z już dodanymi figurami
            chosen.add(subBest);
            picked.add(subBest);
            chosenNames.put(subBest, subtarget);
        }


        Main.writeToFile(chosen, String.format("out/%s", subdir), chosenNames);

        return chosen;
    }

    public static ArrayList<Piece> EvolverPlusEvolver(String target,
                                                      int generations,
                                                      int populationSize,
                                                      int initPopulationSize,
                                                      String pickerPieces,
                                                      int secondaryGenerations,
                                                      int secondaryPopulationSize,
                                                      int secondaryInitPopulationSize,
                                                      String testname) throws IOException {
        String subdir = String.format("PlusEvolver-%s_%s-%s_%d-%d_%d-%d%S",
                Configuration.InitPopShapeStr(),
                target,
                pickerPieces,
                generations,
                populationSize,
                secondaryGenerations,
                secondaryPopulationSize,
                testname == null ? "" : ("_" + testname));

        File directory = new File("out/"+subdir);
        if (! directory.exists()){
            directory.mkdir();
        }

        final ArrayList<Piece> chosen = new ArrayList<>();
        final HashMap<Piece, String> chosenNames = new HashMap<>();
        final String[] piecesToPick = pickerPieces.split(";");

        final List<Piece> finalPopulation = SimpleGeneration(target, generations, populationSize, initPopulationSize, subdir);

        final Piece best = finalPopulation.get(0);
        chosen.add(best);
        chosenNames.put(best, target);

        for (String subtarget : piecesToPick) {
            Configuration.TARGET_PIECE = subtarget;
            best.setFitness(FitnessUtil.calculateFitness(best));
            System.out.println("SecEvo " + subtarget + " (best AVG gets " + best.getFitness() + ")");

            ArrayList<Piece> secInitPop = new ArrayList<>();
            for (int i = 0; i < secondaryInitPopulationSize; i++) {
                Piece p = new Piece(best);
                secInitPop.add(p);
            }

            List<Piece> secResult = SimpleGeneration(subtarget, secondaryGenerations, secondaryPopulationSize, secInitPop, subdir);

            Piece subBest = secResult.get(0);

            //Evolution.writeToFile(subEvo.getPopulation(), String.format("out/XXXX-%s", subtarget));
            System.out.println(subtarget + " " + subBest.getFitness() + " \t\t\t//  " + subBest);

            chosen.add(subBest);
            chosenNames.put(subBest, subtarget);
        }

        Main.writeToFile(chosen, String.format("out/%s", subdir), chosenNames);

        return chosen;
    }


}
