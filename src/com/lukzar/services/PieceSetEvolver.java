package com.lukzar.services;

import com.lukzar.Main;
import com.lukzar.config.Configuration;
import com.lukzar.fitness.FitnessUtil;
import com.lukzar.model.Piece;
import com.lukzar.services.evolution.Evolution;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
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

        Configuration.TARGET_PIECE = target;
        final Collection<Piece> initialize = Evolution.initialize(initialPopulationSize);
        System.out.println("Initial population size: " + initialize.size());

        Main.writeToFile(initialize,
                String.format("out%s/%s-%s_%s",
                        subdir == null ? "" : "/" + subdir,
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

            System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) + " >> " + "Population " + i+"/"+Configuration.NUMBER_OF_GENERATIONS + " size: " + population.size());
            Main.writeToFile(population,
                    String.format("out%s/%s-%s_%s",
                            subdir == null ? "" : "/" + subdir,
                            target,
                            Configuration.InitPopShapeStr(),
                            i));
        }

        return population;
    }

    @Deprecated
    public static Collection<Piece> EvolverPlusPicker(String target,
                                                      int generations,
                                                      int populationSize,
                                                      int initPopulationSize,
                                                      Set<String> pickerPieces,
                                                      String testname) throws IOException {
        String subdir = String.format("PlusPicker-%s_%s-%s_%d-%d%s",
                Configuration.InitPopShapeStr(),
                target,
                pickerPieces,
                generations,
                populationSize,
                testname == null ? "" : ("_" + testname));

        File directory = new File("out/" + subdir);
        if (!directory.exists()) {
            directory.mkdir();
        }

        final Collection<Piece> chosen = new ArrayList<>();
        final HashMap<Piece, String> chosenNames = new HashMap<>();

        final List<Piece> finalPopulation = SimpleGeneration(target, generations, populationSize, initPopulationSize, subdir);

        final Piece best = finalPopulation.get(0);
        chosen.add(best);
        chosenNames.put(best, target);

        final HashSet<Piece> picked = new HashSet<>();

        for (String subtarget : pickerPieces) {
            Configuration.TARGET_PIECE = subtarget;

            final Collection<Piece> copy = finalPopulation.stream()
                    .peek(p -> p.setFitness(FitnessUtil.calculateFitness(p)))
                    .collect(Collectors.toList());
            final List<Piece> pieces = Evolution.collectNewGeneration(copy, Collections.emptyList());

            Piece subBest = null;
            int skipped = 0;
            for (Piece candidate : pieces)
            {
                boolean ok = true;
                for (Piece pick: picked)
                {
                    double sim = FitnessUtil.overlapRatio(pick, candidate);
                    if (sim > Configuration.CHOICE_MAX_SIMILARITY || sim < Configuration.CHOICE_MIN_SIMILARITY)
                    {
                        ok = false;
                        break;
                    }
                }
                if (ok)
                {
                    subBest = candidate;
                    break;
                }
                skipped++;
            }

            //Evolution.writeToFile(subEvo.getPopulation(), String.format("out/XXXX-%s", subtarget));
            System.out.println(subtarget + " " + pieces.get(0).getFitness() + " (" + skipped + " skipped)\t\t\t//  " + subBest);

            chosen.add(subBest);
            picked.add(subBest);
            chosenNames.put(subBest, subtarget);
        }


        Main.writeToFile(chosen, String.format("out/%s", subdir), chosenNames);

        return chosen;
    }

    public static void JustPicker(String target,
                                  List<Piece> population,
                                  Set<String> pickerPieces,
                                  String evolver_subdir) throws IOException {

        final List<Piece> finalPopulation = new ArrayList<Piece>();
        for(Piece p: population)
            finalPopulation.add(new Piece(p));

        final Collection<Piece> chosen = new ArrayList<>();
        final HashMap<Piece, String> chosenNames = new HashMap<>();

        final Piece best = finalPopulation.get(0);
        chosen.add(best);
        chosenNames.put(best, target);

        final HashSet<Piece> picked = new HashSet<>();

        for (String subtarget : pickerPieces) {
            Configuration.TARGET_PIECE = subtarget;

            final Collection<Piece> copy = finalPopulation.stream()
                    .peek(p -> p.setFitness(FitnessUtil.calculateFitness(p)))
                    .collect(Collectors.toList());
            final List<Piece> pieces = Evolution.collectNewGeneration(copy, Collections.emptyList());

            Piece subBest = null;
            int skipped = 0;
            for (Piece candidate : pieces)
            {
                boolean ok = true;
                for (Piece pick: picked)
                {
                    double sim = FitnessUtil.overlapRatio(pick, candidate);
                    if (sim > Configuration.CHOICE_MAX_SIMILARITY || sim < Configuration.CHOICE_MIN_SIMILARITY)
                    {
                        ok = false;
                        break;
                    }
                }
                if (ok)
                {
                    subBest = candidate;
                    break;
                }
                skipped++;
            }

            //Evolution.writeToFile(subEvo.getPopulation(), String.format("out/XXXX-%s", subtarget));
            System.out.println(subtarget + " " + pieces.get(0).getFitness() + " (" + skipped + " skipped)");

            // todo - trzeba będzie sprawdzać podobieństwo z już dodanymi figurami
            chosen.add(subBest);
            picked.add(subBest);
            chosenNames.put(subBest, subtarget);
        }


        Main.writeToFile(chosen, String.format("out/Picker%s", evolver_subdir.substring(evolver_subdir.indexOf("-")+1)), chosenNames);
    }

    public static ArrayList<Piece> EvolverPlusEvolver(String target,
                                                      int generations,
                                                      int populationSize,
                                                      int initPopulationSize,
                                                      Set<String> pickerPieces,
                                                      int secondaryGenerations,
                                                      int secondaryPopulationSize,
                                                      int secondaryInitPopulationSize,
                                                      String testname) throws IOException {
        String subdir = String.format("Evolver-%s_%s-%s_%d-%d_%d-%d%S",
                Configuration.InitPopShapeStr(),
                target,
                pickerPieces,
                generations,
                populationSize,
                secondaryGenerations,
                secondaryPopulationSize,
                testname == null ? "" : ("_" + testname));

        File directory = new File("out/" + subdir);
        if (!directory.exists()) {
            directory.mkdir();
        }

        final ArrayList<Piece> chosen = new ArrayList<>();
        final HashMap<Piece, String> chosenNames = new HashMap<>();

        final List<Piece> finalPopulation = SimpleGeneration(target, generations, populationSize, initPopulationSize, subdir);

        final Piece best = finalPopulation.get(0);
        chosen.add(best);
        chosenNames.put(best, target);


        JustPicker(target, finalPopulation, pickerPieces, subdir);


        for (String subtarget : pickerPieces) {
            Configuration.TARGET_PIECE = subtarget;
            best.setFitness(FitnessUtil.calculateFitness(best));
            System.out.println("SecEvo " + subtarget + " (best AVG gets " + best.getFitness() + ")");

            ArrayList<Piece> secInitPop = new ArrayList<>();
            for (int i = 0; i < secondaryInitPopulationSize; i++) {
                Piece p = new Piece(best);
                secInitPop.add(p);
            }

            List<Piece> secResult = SimpleGeneration(subtarget, secondaryGenerations, secondaryPopulationSize, secInitPop, subdir);


            final HashSet<Piece> picked = new HashSet<>();
            Piece subBest = null;
            int skipped = 0;
            for (Piece candidate : secResult)
            {
                boolean ok = true;
                for (Piece pick: picked)
                {
                    double sim = FitnessUtil.overlapRatio(pick, candidate);
                    if (sim > Configuration.CHOICE_MAX_SIMILARITY || sim < Configuration.CHOICE_MIN_SIMILARITY)
                    {
                        ok = false;
                        break;
                    }
                }
                if (ok)
                {
                    subBest = candidate;
                    break;
                }
                skipped++;
            }

            System.out.println(subtarget + " " + subBest.getFitness() + " (" + skipped + " skipped)");

            chosen.add(subBest);
            picked.add(subBest);
            chosenNames.put(subBest, subtarget);
        }

        Main.writeToFile(chosen, String.format("out/%s", subdir), chosenNames);

        return chosen;
    }


}
