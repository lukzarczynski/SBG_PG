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

        final Evolution evolution = new Evolution();
        evolution.initialize();
        System.out.println("Initial population size: " + evolution.getPopulation().size());
        evolution.getPopulation().sort(Evolution.FITNESS_COMPARATOR);
        writeToFile(evolution.getPopulation(), "out/population_0");

        for (int i = 1; i <= 10; i++) {
            evolution.evolvePopulation();
            System.out.println("Population " + i + " size: " + evolution.getPopulation().size());
            System.out.println(evolution.getPopulation().stream().filter(Piece::isAsymmetric).count());
            writeToFile(evolution.getPopulation(), String.format("out/population_%s", i));

        }
    }

    private static Piece getConverted(Piece piece) {
        Piece p = new Piece(piece.getStart());
        p.addAll(piece.getAsLines());
        p.updateStartPoints();
        return p;
    }

    public static void writeToFile(Collection<Piece> pieces, String path) throws IOException {
        final File file = new File(path + ".html");

        try (FileOutputStream os = new FileOutputStream(file)) {
            os.write(String.format(Templates.getListTemplate(), pieces.stream()
                    .map(Piece::toSvg)
                    .collect(Collectors.joining("\n"))
            ).getBytes());
        }
    }


}
