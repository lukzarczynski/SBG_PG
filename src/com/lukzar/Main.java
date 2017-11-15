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

        String game=args.length>0?args[0]:"ibis";
        Configuration.targetFeatureValues = ConfigLoader.getConfig("resources/configuration/"+game+".csv");


        //Chess.generate();


        final Set<String> targets = new HashSet<>(Configuration.targetFeatureValues.keySet());
        targets.remove("AVG");

        //PieceSetEvolver.EvolverPlusEvolver(game, "AVG", 5, 50, 50, targets, 2, 100, 20, "A");
        //PieceSetEvolver.IndependentEvolver(game, 5, 50, 50, targets, "X");

        Random rnd = new Random();

        String initshape = args.length>1?args[1]:"TRI";

        Configuration.INIT_POP_SHAPE= initshape.equals("TRI")?Configuration.InitShape.triangle:
        initshape.equals("PWN")? Configuration.InitShape.pawn: Configuration.InitShape.random;

        String testname = args.length>2?args[2]:"A";
        String evoOrInit = args.length>3?args[3]:"E";

        int maxtests=100;
        for (int test=1; test <= maxtests; test++)
        {
            String dw="";
            int generations,populationSize;
            if (rnd.nextDouble() < 0.5)
            {
                dw = "wider";
                generations = 50 + 50 * rnd.nextInt(4); // 50-200
                populationSize = 200 + 100 * rnd.nextInt(4); // 200-500
            }
            else
            {
                dw = "deeper";
                generations = 200 + 50 * rnd.nextInt(5); // 200-400
                populationSize = 40 + 20 * rnd.nextInt(4); // 40-100
            }

            // todo - na twardo zmniejszyc tu trochę wartości?

            System.out.println(game+testname+test+"/"+maxtests+" "+evoOrInit+"      // "+dw);

            int initPopulationSize = 100+100*rnd.nextInt(5); // 100-500
            int secondaryGenerations = 1 + 1*rnd.nextInt(8); // 1-8
            int secondaryPopulationSize = 200+100*rnd.nextInt(9); // 200-1000
            int secondaryInitPopulationSize = 100+100*rnd.nextInt(5); // 100-500

            Configuration.MAXIMUM_SIMILARITY = 0.85+0.05*rnd.nextInt(3); // 0.85-0.95
            Configuration.CHOICE_MIN_SIMILARITY = 0.0; //0.3+0.1*rnd.nextInt(4); // 0.3-0.6
            Configuration.CHOICE_MAX_SIMILARITY = 0.80+0.05*rnd.nextInt(4); // 0.80-0.95

            Configuration.Evolution.TOURNAMENT_SIZE = 3+1*rnd.nextInt(3); // 3-5
            Configuration.Evolution.CROSSOVER_SIZE = populationSize; // Tu naprawdę była stała liczba crossoverów na populację??
            Configuration.Evolution.Mutation.STARTING_POINT_CHANCE = 0.05+0.05*rnd.nextInt(3); // 0.05-0.15 // co to jest???
            Configuration.Evolution.Mutation.CHANCE_TO_CHANGE_POINT = 0.4+0.1*rnd.nextInt(5); // 0.4-0.8
            Configuration.Evolution.Mutation.CHANCE_TO_CHANGE_PART = 0.25+0.05*rnd.nextInt(5); // 0.25-0.45
            Configuration.Evolution.Mutation.CHANCE_TO_SPLIT_LINE = 0.5+0.1*rnd.nextInt(6); // 0.5-1.0
            Configuration.Evolution.Mutation.OFFSET = 20 + 10*rnd.nextInt(4); // 20-50;



            if (evoOrInit.equals("E"))
                PieceSetEvolver.EvolverPlusEvolver(game, "AVG", generations, populationSize, initPopulationSize, targets, secondaryGenerations, secondaryPopulationSize, secondaryInitPopulationSize, testname+test);
            else
                PieceSetEvolver.IndependentEvolver(game, generations, populationSize, initPopulationSize, targets, testname+test);
        }




    }

    public static void writeToFile(Collection<Piece> pieces, String path) throws IOException {
        writeToFile(pieces, path, new HashMap<>());
    }

    public static void writeToFile(Collection<Piece> pieces, String path, HashMap<Piece, String> names) throws IOException {
        final String content = String.format(Templates.getListTemplate(),
                getConfiguration(),
                pieces.stream()
                        .filter(Objects::nonNull)
                        .map(p -> names.getOrDefault(p, "") + p.toSvg())
                        .collect(Collectors.joining("\n"))
        );
        writeToFile(content, path, null);
    }

    public static void writeToFile(String content, String path, String extension) throws IOException {
        try (FileOutputStream os = new FileOutputStream(new File(path + "."+(extension==null?"html":extension)))) {
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
        writeToFile(content, path, null);
    }

    // ignore this
    public static void writeToFileAsLines(Collection<Piece> pieces, String path) throws IOException {
        final String content = String.format(Templates.getListTemplate(),
                getConfiguration(),
                pieces.stream()
                        .map(PieceFormatter::asLines)
                        .collect(Collectors.joining("\n"))
        );
        writeToFile(content, path, null);
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
