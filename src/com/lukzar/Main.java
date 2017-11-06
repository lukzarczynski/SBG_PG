package com.lukzar;

import com.lukzar.config.ConfigLoader;
import com.lukzar.config.Configuration;
import com.lukzar.config.Templates;
import com.lukzar.model.Piece;
import com.lukzar.services.PieceSetEvolver;
import com.lukzar.utils.PieceFormatter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 28.05.17.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

        String game="ibis";
        Configuration.targetFeatureValues = ConfigLoader.getConfig("resources/configuration/"+game+".csv");


        //Chess.generate();


        final Set<String> targets = new HashSet<>(Configuration.targetFeatureValues.keySet());
        targets.remove("AVG");

        //PieceSetEvolver.EvolverPlusEvolver("AVG", 5, 100, 100, targets, 2, 100, 20, "A");

        Random rnd = new Random();

        Configuration.INIT_POP_SHAPE= Configuration.InitShape.triangle;
        for (int test=1; test <= 100; test++)
        {
            int generations = 50+50*rnd.nextInt(16); // 50-700
            int populationSize = 200+100*rnd.nextInt(9); // 200-1000
            int initPopulationSize = 100+100*rnd.nextInt(5); // 100-500
            int secondaryGenerations = 1 + 1*rnd.nextInt(10); // 1-10
            int secondaryPopulationSize = 200+100*rnd.nextInt(9); // 200-1000
            int secondaryInitPopulationSize = 100+100*rnd.nextInt(5); // 100-500

            Configuration.MAXIMUM_SIMILARITY = 0.85+0.05*rnd.nextInt(3); // 0.85-0.95
            Configuration.CHOICE_MIN_SIMILARITY = 0.3+0.1*rnd.nextInt(4); // 0.3-0.6
            Configuration.CHOICE_MAX_SIMILARITY = 0.80+0.05*rnd.nextInt(4); // 0.80-0.95

            Configuration.Evolution.TOURNAMENT_SIZE = 3+1*rnd.nextInt(3); // 3-5
            Configuration.Evolution.CROSSOVER_SIZE = populationSize; // Tu naprawdę była stała liczba crossoverów na populację??
            Configuration.Evolution.Mutation.STARTING_POINT_CHANCE = 0.05+0.05*rnd.nextInt(3); // 0.05-0.15 // co to jest???
            Configuration.Evolution.Mutation.CHANCE_TO_CHANGE_POINT = 0.4+0.1*rnd.nextInt(5); // 0.4-0.8
            Configuration.Evolution.Mutation.CHANCE_TO_CHANGE_PART = 0.25+0.05*rnd.nextInt(5); // 0.25-0.45
            Configuration.Evolution.Mutation.CHANCE_TO_SPLIT_LINE = 0.5+0.1*rnd.nextInt(6); // 0.5-1.0
            Configuration.Evolution.Mutation.OFFSET = 20 + 10*rnd.nextInt(4); // 20-50;

            PieceSetEvolver.EvolverPlusEvolver("AVG", generations, populationSize, initPopulationSize, targets, secondaryGenerations, secondaryPopulationSize, secondaryInitPopulationSize, game+"A"+test);
        }




    }

    public static void writeToFile(Collection<Piece> pieces, String path) throws IOException {
        writeToFile(pieces, path, new HashMap<>());
    }

    public static void writeToFile(Collection<Piece> pieces, String path, HashMap<Piece, String> names) throws IOException {
        final String content = String.format(Templates.getListTemplate(),
                getConfiguration(),
                pieces.stream()
                        .map(p -> names.getOrDefault(p, "") + p.toSvg())
                        .collect(Collectors.joining("\n"))
        );
        writeToFile(content, path);
    }

    private static void writeToFile(String content, String path) throws IOException {
        try (FileOutputStream os = new FileOutputStream(new File(path + ".html"))) {
            os.write(content.getBytes());
        }
    }

    // ignore this
    public static void writeToFileAsPoints(Collection<Piece> pieces, String path) throws IOException {
        final String content = String.format(Templates.getListTemplate(),
                getConfiguration(),
                pieces.stream()
                        .map(PieceFormatter::asPoints)
                        .collect(Collectors.joining("\n"))
        );
        writeToFile(content, path);
    }

    // ignore this
    public static void writeToFileAsLines(Collection<Piece> pieces, String path) throws IOException {
        final String content = String.format(Templates.getListTemplate(),
                getConfiguration(),
                pieces.stream()
                        .map(PieceFormatter::asLines)
                        .collect(Collectors.joining("\n"))
        );
        writeToFile(content, path);
    }

    private static String getConfiguration() {
        StringBuilder builder = new StringBuilder("<ul>");
        Configuration.getDescription().forEach(c -> {

            builder.append("<li>")
                    .append(c)
                    .append("</li>");
        });
        return builder.append("</ul>").toString();
    }


}
