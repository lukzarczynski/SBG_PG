package com.lukzar.services;

import com.lukzar.config.Configuration;
import com.lukzar.fitness.FitnessUtil;
import com.lukzar.model.Piece;

import java.io.IOException;
import java.util.*;

/**
 * Created by Kot on 2017-10-31.
 */
public class PieceSetEvolver
{

  public static TreeSet<Piece> SimpleGeneration(String target, int generations, int populationSize, int initialPopulationSize) throws IOException
  {
    Configuration.Evolution.INITIAL_SIZE = initialPopulationSize;

    final Evolution evolution = new Evolution();
    evolution.initialize();
    System.out.println("Initial population size: " + evolution.getPopulation().size());

    //Evolution.writeToFile(evolution.getPopulation(), "out/" + target + "_0");

    return SimpleGeneration(target, generations, populationSize, evolution.getPopulation());
  }

  public static TreeSet<Piece> SimpleGeneration(String target, int generations, int populationSize, Collection<Piece> startPopulation) throws IOException
  {
    Configuration.TARGET_PIECE = target;
    Configuration.NUMBER_OF_GENERATIONS = generations;
    Configuration.Evolution.MAXIMUM_POPULATION_SIZE = populationSize;

    final Evolution evolution = new Evolution();
    evolution.setPopulation(startPopulation);

    for (int i = 1; i <= Configuration.NUMBER_OF_GENERATIONS; i++) {
      evolution.evolvePopulation();
      System.out.println("Population " + i + " size: " + evolution.getPopulation().size());
      Evolution.writeToFile(evolution.getPopulation(),
              String.format("out/%s-%s_%s",
                      target,
                      Configuration.INIT_POP_TRIANGLE
                              ? "TRI"
                              : "RND",
                      i));
    }

    return evolution.getPopulation();
  }

  public static ArrayList<Piece> EvolverPlusPicker(String target, int generations, int populationSize, int initPopulationSize, String pickerPieces, String testname) throws IOException
  {
    ArrayList<Piece> chosen = new ArrayList<>();
    HashMap<Piece,String> chosenNames = new HashMap<>();
    String[] piecesToPick = pickerPieces.split(";");

    TreeSet<Piece> finalPopulation = SimpleGeneration(target, generations, populationSize, initPopulationSize);


    Piece best = finalPopulation.first();
    chosen.add(best);
    chosenNames.put(best, target);

    HashSet<Piece> picked = new HashSet<>();

    for (String subtarget:piecesToPick)
    {
      Configuration.TARGET_PIECE = subtarget;

      ArrayList<Piece> copy = new ArrayList<>(finalPopulation);
      copy.forEach(p -> p.setFitness(FitnessUtil.calculateFitness(p)));
      Evolution subEvo = new Evolution();
      subEvo.setPopulation(copy);
      Piece subBest = null;
      int skipped=0;
      for (Piece p : subEvo.getPopulation()) {
        if (!picked.contains(p)) {
          subBest = p;
          break;
        }
        skipped++;
      }

      //Evolution.writeToFile(subEvo.getPopulation(), String.format("out/XXXX-%s", subtarget));
      System.out.println(subtarget + " " + subEvo.getPopulation().first().getFitness()+ " ("+skipped+" skipped)\t\t\t//  " + subBest);

      chosen.add(subBest);
      picked.add(subBest);
      chosenNames.put(subBest, subtarget);
    }


    Evolution.writeToFile(chosen,
            String.format("out/PlusPicker-%s_%s-%s_%d-%d%S",
                    Configuration.INIT_POP_TRIANGLE
                            ? "TRI"
                            : "RND",
                    target,
                    pickerPieces,
                    generations,
                    populationSize,
                    testname==null?"":("_"+testname)),
            chosenNames);

    return chosen;
  }

  public static ArrayList<Piece> EvolverPlusEvolver(String target, int generations, int populationSize, int initPopulationSize, String pickerPieces,
                                                    int secondaryGenerations, int secondaryPopulationSize, int secondaryInitPopulationSize, String testname) throws IOException
  {
    ArrayList<Piece> chosen = new ArrayList<>();
    HashMap<Piece,String> chosenNames = new HashMap<>();
    String[] piecesToPick = pickerPieces.split(";");

    TreeSet<Piece> finalPopulation = SimpleGeneration(target, generations, populationSize, initPopulationSize);


    Piece best = finalPopulation.first();
    chosen.add(best);
    chosenNames.put(best, target);

    for (String subtarget:piecesToPick)
    {
      Configuration.TARGET_PIECE = subtarget;
      best.setFitness(FitnessUtil.calculateFitness(best));
      System.out.println("SecEvo " + subtarget + " (best AVG gets " + best.getFitness() + ")");

      ArrayList<Piece> secInitPop = new ArrayList<>();
      for (int i=0; i<secondaryInitPopulationSize; i++) // todo nie ma efektu na razie bo przeżywa tylko jedna - ale nie chcielibyśmy żeby tak było.. :(
      {
        Piece p = new Piece(best);
        p.setFitness(best.getFitness());
        secInitPop.add(p);
      }

      TreeSet<Piece> secResult = SimpleGeneration(subtarget, secondaryGenerations, secondaryPopulationSize, secInitPop);

      Piece subBest = secResult.first();

      //Evolution.writeToFile(subEvo.getPopulation(), String.format("out/XXXX-%s", subtarget));
      System.out.println(subtarget + " " + subBest.getFitness()+ " \t\t\t//  " + subBest);

      chosen.add(subBest);
      chosenNames.put(subBest, subtarget);
    }

    Evolution.writeToFile(chosen,
            String.format("out/PlusEvolver-%s_%s-%s_%d-%d_%d-%d%S",
                    Configuration.INIT_POP_TRIANGLE
                            ? "TRI"
                            : "RND",
                    target,
                    pickerPieces,
                    generations,
                    populationSize,
                    secondaryGenerations,
                    secondaryPopulationSize,
                    testname==null?"":("_"+testname)),
            chosenNames);

    return chosen;
  }


}
