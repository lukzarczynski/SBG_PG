package com.lukzar;

import com.lukzar.config.Templates;
import com.lukzar.model.Piece;
import com.lukzar.services.Evolution;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 28.05.17.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        final Evolution evolution = new Evolution();
        evolution.initialize();
        System.out.println(evolution.getPopulation().size());
        writeToFile(evolution.getPopulation(), "out/initial_population");
//        writeToFile(evolution.getPopulation().stream().map(Main::getConverted).collect(Collectors.toList()), "out/initial_population_conv");

        for (int i = 0; i < 4; i++) {
            evolution.evolvePopulation();
            System.out.println(evolution.getPopulation().size());
            writeToFile(evolution.getPopulation(), String.format("out/image_%s", i));
//            writeToFile(evolution.getPopulation().stream().map(Main::getConverted).collect(Collectors.toList()), String.format("out/image_%s_conv", i));

        }
    }

    private static Piece getConverted(Piece piece) {
        Piece p = new Piece();
        p.getParts().addAll(piece.getConverted());
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
