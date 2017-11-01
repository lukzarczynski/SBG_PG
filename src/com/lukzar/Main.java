package com.lukzar;

import com.lukzar.config.Configuration;
import com.lukzar.config.Templates;
import com.lukzar.model.Piece;
import com.lukzar.services.Evolution;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 28.05.17.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

//        runEvolution();

        String[] pieces = {
                //"pawn",
                //"rook",
                //"knight",
                //"bishop",
                //"queen",
                //"king",
                "AVG"
        };

        for (String piece : pieces) {
            Configuration.TARGET_PIECE = piece;
            System.out.println("GENERATING " + piece);
            runEvolution();
        }

    }

    public static void runEvolution() throws IOException {
        String target = Configuration.TARGET_PIECE == null
                ? "population"
                : Configuration.TARGET_PIECE;

        final Evolution evolution = new Evolution();
        evolution.initialize();
        System.out.println("Initial population size: " + evolution.getPopulation().size());
        Evolution.writeToFile(evolution.getPopulation(), "out/" + target + "_0");

        for (int i = 1; i <= Configuration.NUMBER_OF_EVOLUTIONS; i++) {
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
    }




}
